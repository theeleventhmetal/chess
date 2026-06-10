package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import io.javalin.websocket.WsMessageContext;
import jakarta.websocket.*;
import server.ClientException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import websocket.commands.ConnectCommand;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageHandler serverMessageHandler;

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ClientException{
        try{
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ClientException(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME -> serverMessageHandler.loadGame(new Gson().fromJson(message, LoadGameMessage.class));
                    case NOTIFICATION -> serverMessageHandler.notify(new Gson().fromJson(message, NotificationMessage.class));
                    case ERROR -> serverMessageHandler.throwError(new Gson().fromJson(message, ErrorMessage.class));
                }
            }
        });
    }

    public void connect(String authToken, Integer gameID, String color) throws ClientException{
        try{
            var command = new ConnectCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ClientException(e.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move, String color) throws ClientException{
        try{
            var command = new MoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move, color);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ClientException(e.getMessage());
        }
    }

    public void leave(String authToken, Integer gameID) throws ClientException{
        try{
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ClientException(e.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID) throws ClientException{
        try{
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ClientException(e.getMessage());
        }
    }
}
