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
import jakarta.websocket.OnMessage;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

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
    public void handleClose(@NotNull WsCloseContext wsCloseContext) {
        System.out.println("Websocket closed");
    }

    @OnMessage
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(ctx.message(), (Session) ctx.session);
                case MAKE_MOVE -> makeMove(ctx.message(), (Session) ctx.session);
                case LEAVE -> leave(ctx.message(), (Session) ctx.session);
                case RESIGN -> resign(ctx.message(), (Session) ctx.session);
            }
        } catch (IOException ex) {
            ex.getMessage();
        }
    }

    private void connect(String rawMessage, Session session) throws IOException {
        ConnectCommand connectCommand = new Gson().fromJson(rawMessage, ConnectCommand.class);
        String authToken = connectCommand.getAuthToken();
        int gameID = connectCommand.getGameID();
        try {
            AuthData authData = authDAO.getAuth(authToken);
            String username = authData.username();
            GameData gameData = gameDAO.getGame(gameID);
            ChessGame game = gameData.game();
            String color;
            if (username.equals(gameData.whiteUsername())) {
                color = "white";
            } else if (username.equals(gameData.blackUsername())) {
                color = "black";
            } else {
                color = null; // observer
            }
            String message;

            if (color == null){
                message = String.format("%s has joined the game as an observer", username);
            }else{
                message = String.format("%s has joined the game as %s", username, color);
            }
            connections.add(gameID, session);
            var serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(gameID, session, serverMessage);
            var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            Thread.sleep(1500);
            session.getRemote().sendString(new Gson().toJson(loadGameMessage));

        }catch (Exception e){
            ErrorMessage errorMessage = new ErrorMessage(websocket.messages.ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }

    }

    private void makeMove(String rawMessage, Session session) throws DataAccessException, IOException {
        Map<Integer, String> columns = Map.of(
                1, "a", 2, "b", 3, "c", 4, "d",
                5, "e", 6, "f", 7, "g", 8, "h"
        );
        MoveCommand moveCommand = new Gson().fromJson(rawMessage, MoveCommand.class);
        String authToken = moveCommand.getAuthToken();
        int gameID = moveCommand.getGameID();
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        ChessMove move = moveCommand.getMove();


        try{
            String username = authDAO.getAuth(authToken).username();
            String color;
            if (username.equals(gameData.whiteUsername())) {
                color = "white";
            } else if (username.equals(gameData.blackUsername())) {
                color = "black";
            } else {
                color = null; // observer
            }
            System.out.format("Color: %s\n", color);
            System.out.format("TeamTurn: %s\n", game.getTeamTurn());
            if (("white".equals(color) && game.getTeamTurn() != ChessGame.TeamColor.WHITE) ||
                    ("black".equals(color) && game.getTeamTurn() != ChessGame.TeamColor.BLACK)
            || color == null) {
                ErrorMessage errorMessage = new ErrorMessage(websocket.messages.ServerMessage.ServerMessageType.ERROR, "Error: unauthorized");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
                return;
            }
            game.makeMove(move);
            GameData updatedGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(updatedGame);

            ChessPosition startPos = move.getStartPosition();
            ChessPosition endPos = move.getEndPosition();
            ChessPiece piece = game.getBoard().getPiece(endPos);

            String startPosCol = columns.get(startPos.getColumn());
            String startPosRow = columns.get(startPos.getRow());
            String endPosCol = columns.get(endPos.getColumn());
            String endPosRow = columns.get(endPos.getRow());

            String message = String.format("%s has moved their %s from position: %s%s to position %s%s",
                    username, piece.toString(), startPosCol, startPosRow, endPosCol, endPosRow);

            var notifMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            var loadMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);

            connections.broadcast(gameID, session, notifMessage); // send move notif to all other players
            connections.broadcast(gameID, null, loadMessage); //send loaded game to all sessions

            if (game.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                    game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                System.out.print("BEGINNING OF IF BLOCK\n");
                var winMessage = String.format("%s has achieved checkmate! Game is over.", username);
                game.endGame();
                var finalGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
                gameDAO.updateGame(finalGame);
                var notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, winMessage);
                connections.broadcast(gameID, null, notificationMessage);
            }
        }catch (Exception e){
            ErrorMessage errorMessage = new ErrorMessage(websocket.messages.ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }

    private void leave(String rawMessage, Session session) throws DataAccessException, IOException {
        LeaveCommand leaveCommand = new Gson().fromJson(rawMessage, LeaveCommand.class);
        String color;
        int gameID = leaveCommand.getGameID();
        String authToken = leaveCommand.getAuthToken();

        connections.remove(gameID, session);
        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();

        GameData updatedGame;
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        String message;

        if (username.equals(gameData.whiteUsername())) {
            color = "white";
        } else if (username.equals(gameData.blackUsername())) {
            color = "black";
        } else {
            color = null; // observer
        }

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

    private void resign(String rawMessage, Session session) throws DataAccessException, IOException {
        ResignCommand resignCommand = new Gson().fromJson(rawMessage, ResignCommand.class);
        String authToken = resignCommand.getAuthToken();
        int gameID = resignCommand.getGameID();
        GameData gameData = gameDAO.getGame(gameID);
        String color;
        try {
            String username = authDAO.getAuth(authToken).username();
            if (username.equals(gameData.whiteUsername())) {
                color = "white";
            } else if (username.equals(gameData.blackUsername())) {
                color = "black";
            } else {
                ErrorMessage errorMessage = new ErrorMessage(websocket.messages.ServerMessage.ServerMessageType.ERROR, "Error: unauthorized");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
                return;
            }
            GameData updatedGame;
            ChessGame game = gameData.game();

            if (game.gameIsOver()){
                ErrorMessage errorMessage = new ErrorMessage(websocket.messages.ServerMessage.ServerMessageType.ERROR, "Error: unauthorized");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
                return;
            }

            game.endGame();
            updatedGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(updatedGame);

            var message = String.format("%s has resigned from the game", username);

            var notifMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(gameID, null, notifMessage);
        }catch (Exception e){
            ErrorMessage errorMessage = new ErrorMessage(websocket.messages.ServerMessage.ServerMessageType.ERROR, e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }
}