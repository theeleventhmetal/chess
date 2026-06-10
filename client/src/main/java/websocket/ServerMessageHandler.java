package websocket;

import websocket.messages.ServerMessage;

public interface ServerMessageHandler {

    void loadGame(ServerMessage message);
    void notify(ServerMessage message);
    void throwError(ServerMessage message);
}
