package websocket.commands;

import chess.ChessMove;

public class MoveCommand extends UserGameCommand{

    private final ChessMove move;
    private final String color;

    public MoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move, String color) {
        super(commandType, authToken, gameID);
        this.move = move;
        this.color = color;
    }

    public ChessMove getMove(){return move; }
    public String getColor(){return color;}
}
