package websocket;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public interface ServerMessageHandler {

    void loadGame(LoadGameMessage message);
    void notify(NotificationMessage message);
    void throwError(ErrorMessage message);
}
