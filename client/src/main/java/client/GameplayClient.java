package client;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.ErrorResult;
import server.ClientException;
import server.ServerFacade;
import websocket.WebSocketFacade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class GameplayClient {
    private ServerFacade server;
    private final WebSocketFacade ws;
    private String color;
    private static final Map<ChessPiece.PieceType, String> PIECE_MAP= Map.of(
            ChessPiece.PieceType.BISHOP, "B",
            ChessPiece.PieceType.KING, "K",
            ChessPiece.PieceType.PAWN, "P",
            ChessPiece.PieceType.ROOK, "R",
            ChessPiece.PieceType.QUEEN, "Q",
            ChessPiece.PieceType.KNIGHT, "N");
    private State state;



    public GameplayClient(ServerFacade server, WebSocketFacade ws, String color, State state) {
        this.server = server;
        this.ws = ws;
        this.color = color;
        this.state = state;
    }

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private final ChessGame game = new ChessGame(); //TEMPORARY CHESS GAME JUST FOR RENDERING
    String[] whiteLetters = {"a", "b", "c", "d", "e", "f", "g", "h"};
    String[] blackLetters = {"h", "g","f", "e", "d", "c", "b", "a"};

    public void run(){
        System.out.print("\n");
        System.out.print(help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            if (color.equals("white")){
                drawWhiteView();
            }
            else if (color.equals("black")){
                drawBlackView();
            }
            printPrompt();
            String line = scanner.nextLine();
            try{
                result = eval(line);
            } catch (Throwable e){
                result = e.toString();
                var msg = e.toString();
                System.out.print(msg);
            }
            if (state == State.SIGNEDIN){
                return;
            }
        }
    }

    private void printPrompt() {
        out.print("\n"+ SET_TEXT_COLOR_LIGHT_GREY + "[GAMEPLAY] >>> " + SET_TEXT_COLOR_GREEN);
    }

    public String help(){
        return """
                \n
                AVAILABLE COMMANDS:
                redraw - to redraw chess board
                leave - to leave the game
                move - to make a move on your turn
                resign - to forfeit the game
                highlight - to highlight legal moves for a piece
                quit - to exit
                help
                """;
    }

    public String eval(String input){
        try{
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            return switch (cmd) {
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move();
                case "resign" -> resign();
                case "highlight" -> highlight();
                case "quit" -> quit();
                default -> help();
            };
        } catch (Exception except){
            try{
                var error =  new Gson().fromJson(except.getMessage(), ErrorResult.class);
                return error.message();
            } catch (com.google.gson.JsonSyntaxException e){
                return except.getMessage();
            }
        }
    }

    private String quit() {
        System.exit(0);
        return "exiting program";
    }

    private void redraw() throws ClientException{
        if (color.equals("white")){
            drawWhiteView();
        }
        else if (color.equals("black")){
            drawBlackView();
        }
    }

    private void leave() throws ClientException{

    }



    private void drawWhiteView(){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print("\n\n");
        out.print(ERASE_SCREEN);
        drawBorder(out, whiteLetters);
        out.print("\n");
        drawWhiteBoard(out);
        drawBorder(out, whiteLetters);
    }

    private void drawBorder(PrintStream out, String[] letters){
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
            whiteBoardHelper(out, leadingWhite, boardRowInt);
            leadingWhite = !leadingWhite;
        }
    }


    private void whiteBoardHelper(PrintStream out, boolean leadingWhite, int boardRowInt){
        setBlue(out);
        out.print(EMPTY);
        out.print(boardRowInt);
        out.print(EMPTY);
        if (leadingWhite){
            setWhite(out);
        }else{
            setBlack(out);
        }
        drawWhiteRow(out, leadingWhite, boardRowInt);
        setBlue(out);
        out.print(EMPTY);
        out.print(boardRowInt);
        out.print(EMPTY);
        out.print(RESET_BG_COLOR);
        out.print("\n");
    }

    private void drawWhiteRow(PrintStream out, boolean leadingWhite, int row){
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

    private void drawBlackView(){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print("\n\n");
        out.print(ERASE_SCREEN);
        drawBorder(out, blackLetters);
        out.print("\n");
        drawBlackBoard(out);
        drawBorder(out, blackLetters);
    }

    private void drawBlackBoard(PrintStream out){
        boolean leadingWhite = true;
        for (int boardRowInt = 1; boardRowInt <= BOARD_SIZE_IN_SQUARES; boardRowInt++){
            blackBoardHelper(out, leadingWhite, boardRowInt);
            leadingWhite = !leadingWhite;
        }
    }

    private void blackBoardHelper(PrintStream out, boolean leadingWhite, int boardRowInt){
        setBlue(out);
        out.print(EMPTY);
        out.print(boardRowInt);
        out.print(EMPTY);
        if (leadingWhite){
            setWhite(out);
        }else{
            setBlack(out);
        }
        drawBlackRow(out, leadingWhite, boardRowInt);
        setBlue(out);
        out.print(EMPTY);
        out.print(boardRowInt);
        out.print(EMPTY);
        out.print(RESET_BG_COLOR);
        out.print("\n");
    }

    private void drawBlackRow(PrintStream out, boolean leadingWhite, int row){
        boolean blackSquare = !leadingWhite;
        for (int i = 8; i >= 1; i--){
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
