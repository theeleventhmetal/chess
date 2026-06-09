package websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.LeaveCommand;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.ConnectCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    public WebSocketHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), command.getGameID(), command, (Session) ctx.session);
                case MAKE_MOVE -> makeMove(command.getAuthToken(), command.getGameID(), command, (Session) ctx.session);
                case LEAVE -> leave(command.getAuthToken(), command.getGameID(), command, (Session) ctx.session);
                case RESIGN -> resign();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void connect(String authToken, Integer gameID, UserGameCommand command, Session session) throws SQLException, DataAccessException, IOException {
        ConnectCommand connectCommand = (ConnectCommand) command;
        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();
        String color = connectCommand.getColor();
        if (authDAO.getAuth(authToken) == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (gameDAO.getGame(gameID) == null){
            throw new BadRequestException("Error: game does not exist");
        }
        String message;

        if (color == null){
            message = String.format("%s has joined the game as an observer", username);
        }else{
            message = String.format("%s has joined the game as %s", username, color);
        }
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        connections.add(gameID, session);
        var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, session, serverMessage);
        var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        session.getRemote().sendString(new Gson().toJson(loadGameMessage));
    }

    private void makeMove(String authToken, Integer gameID, UserGameCommand command, Session session) throws DataAccessException, InvalidMoveException {
        MoveCommand moveCommand = (MoveCommand) command;
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        ChessMove move = moveCommand.getMove();

        try{
            game.makeMove(move);
            GameData updatedGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(updatedGame);

            ChessPosition startPos = move.getStartPosition();
            ChessPosition endPos = move.getEndPosition();
            ChessPiece piece = game.getBoard().getPiece(endPos);
            AuthData authData = authDAO.getAuth(authToken);
            String username = authData.username();

            String message = String.format("%s has moved their %s from position: %s to position %s", username, piece.toString(), startPos, endPos.toString());

            var notifMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            var loadMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);

            connections.broadcast(gameID, session, notifMessage); // send move notif to all other players
            connections.broadcast(gameID, null, loadMessage); //send loaded game to all sessions

        }catch (InvalidMoveException e){
            throw new DataAccessException("Error: Invalid move");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void leave(String authToken, Integer gameID, UserGameCommand command, Session session) throws DataAccessException, IOException {
        LeaveCommand leaveCommand = (LeaveCommand) command;
        String color = leaveCommand.getColor();

        connections.remove(gameID, session);
        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();


        GameData updatedGame;
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        String message;

        if ("white".equals(color)){
            updatedGame = new GameData(gameID, null, gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(updatedGame);
            message = String.format("%s left the game, %s is now open", username, color);
        }else if ("black".equals(color)){
            updatedGame = new GameData(gameID, gameData.whiteUsername(), null, gameData.gameName(), game);
            gameDAO.updateGame(updatedGame);
            message = String.format("%s left the game, %s is now open", username, color);
        }else{
            message = String.format("%s is no longer observing the game", username);

        }

        var notifMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, session, notifMessage);
    }

    private void resign(String authToken, Integer gameID, UserGameCommand command, Session session){

    }


}