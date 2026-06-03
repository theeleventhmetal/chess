package client;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import server.ServerFacade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class GameplayClient {
    private ServerFacade server;
    private String color;
    private static final Map<ChessPiece.PieceType, String> PIECE_MAP= Map.of(
            ChessPiece.PieceType.BISHOP, "B",
            ChessPiece.PieceType.KING, "K",
            ChessPiece.PieceType.PAWN, "P",
            ChessPiece.PieceType.ROOK, "R",
            ChessPiece.PieceType.QUEEN, "Q",
            ChessPiece.PieceType.KNIGHT, "N");
    private State state;



    public GameplayClient(ServerFacade server, String color, State state) {
        this.server = server;
        this.color = color;
        this.state = state;
    }

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private final ChessGame game = new ChessGame(); //TEMPORARY CHESS GAME JUST FOR RENDERING

    public void run(){
        if (color.equals("white")){
            drawWhiteView();
        }
        else if (color.equals("black")){
            drawBlackView();
        }
        state = State.SIGNEDIN;
    }

    private void printPrompt() {
        out.print("\n"+ SET_TEXT_COLOR_LIGHT_GREY + "[LOGGED IN] >>> " + SET_TEXT_COLOR_GREEN);
    }

    private void drawWhiteView(){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print("\n\n");
        out.print(ERASE_SCREEN);
        drawWhiteBorder(out);
        out.print("\n");
        drawWhiteBoard(out);
        drawWhiteBorder(out);
    }

    private void drawWhiteBorder(PrintStream out){
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        setBlue(out);
        out.print(EMPTY);
        out.print(EMPTY);
        out.print(EMPTY);
        for(int i = 1; i <= BOARD_SIZE_IN_SQUARES; i++){
            out.print(EMPTY);
            out.print(SET_TEXT_BOLD);
            out.print(letters[i-1]);
            out.print(EMPTY);
        }
        setBlue(out);
        out.print(EMPTY);
        out.print(EMPTY);
        out.print(EMPTY);
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private void drawWhiteBoard(PrintStream out){
        boolean leadingWhite = true;
        for (int boardRowInt = BOARD_SIZE_IN_SQUARES; boardRowInt > 0; boardRowInt--){
            setBlue(out);
            out.print(EMPTY);
            out.print(boardRowInt);
            out.print(EMPTY);
            if (leadingWhite){
                setWhite(out);
            }else{
                setBlack(out);
            }
            drawRow(out, leadingWhite, boardRowInt);
            setBlue(out);
            out.print(EMPTY);
            out.print(boardRowInt);
            out.print(EMPTY);
            out.print(RESET_BG_COLOR);
            out.print("\n");
            leadingWhite = !leadingWhite;
        }
    }

    private void drawBlackView(){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print("\n\n");
        out.print(ERASE_SCREEN);
        drawBlackBorder(out);
        out.print("\n");
        drawBlackBoard(out);
        drawBlackBorder(out);
    }

    private void drawBlackBorder(PrintStream out){
        String[] letters = {"h", "g","f", "e", "d", "c", "b", "a"};
        setBlue(out);
        out.print(EMPTY);
        out.print(EMPTY);
        out.print(EMPTY);
        for(int i = 1; i <= BOARD_SIZE_IN_SQUARES; i++){
            out.print(EMPTY);
            out.print(SET_TEXT_BOLD);
            out.print(letters[i-1]);
            out.print(EMPTY);
        }
        setBlue(out);
        out.print(EMPTY);
        out.print(EMPTY);
        out.print(EMPTY);
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);

    }

    private void drawBlackBoard(PrintStream out){
        boolean leadingWhite = true;
        for (int boardRowInt = 1; boardRowInt <= BOARD_SIZE_IN_SQUARES; boardRowInt++){
            setBlue(out);
            out.print(EMPTY);
            out.print(boardRowInt);
            out.print(EMPTY);
            if (leadingWhite){
                setWhite(out);
            }else{
                setBlack(out);
            }
            drawRow(out, leadingWhite, boardRowInt);
            setBlue(out);
            out.print(EMPTY);
            out.print(boardRowInt);
            out.print(EMPTY);
            out.print(RESET_BG_COLOR);
            out.print("\n");
            leadingWhite = !leadingWhite;
        }
    }

    private void drawRow(PrintStream out, boolean leadingWhite, int row){
            boolean blackSquare = !leadingWhite;
            for(int i = 1; i <= 8; i++){
                if (blackSquare){
                    setBlack(out);
                }else{
                    setWhite(out);
                }
                out.print(EMPTY);
                ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row, i));
                if (piece == null){
                    out.print(EMPTY);
                }else{
                    ChessPiece.PieceType type = piece.getPieceType();
                    ChessGame.TeamColor color = piece.getTeamColor();
//                    String pieceInsert = color.toString() + "_" + type.toString();
                    if (color == ChessGame.TeamColor.WHITE){
                        out.print(SET_TEXT_COLOR_GREEN);
                    }
                    else if (color == ChessGame.TeamColor.BLACK){
                        out.print(SET_TEXT_COLOR_RED);
                    }
                    out.print(PIECE_MAP.get(type));
                }
                out.print(EMPTY);
                blackSquare = !blackSquare;
            }
    }


    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
    }

    private static void setBlue(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}
