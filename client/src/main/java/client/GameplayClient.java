package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.ErrorResult;
import server.ClientException;
import server.ServerFacade;
import websocket.ServerMessageHandler;
import websocket.WebSocketFacade;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class GameplayClient implements ServerMessageHandler{
    private final ServerFacade server;
    private final String color;
    private static final Map<ChessPiece.PieceType, String> PIECE_MAP= Map.of(
            ChessPiece.PieceType.BISHOP, "B",
            ChessPiece.PieceType.KING, "K",
            ChessPiece.PieceType.PAWN, "P",
            ChessPiece.PieceType.ROOK, "R",
            ChessPiece.PieceType.QUEEN, "Q",
            ChessPiece.PieceType.KNIGHT, "N");
    private State state;
    private WebSocketFacade ws;
    private final int gameID;
    Collection<ChessPosition> validMoves;



    public GameplayClient(ServerFacade server, String color, State state, String serverUrl, int gameID) throws ClientException {
        this.server = server;
        this.color = color;
        this.state = state;
        this.gameID = gameID;
        this.ws = new WebSocketFacade(serverUrl,this);
        this.ws.connect(server.authToken, gameID, color);
    }

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private ChessGame game;
    String[] whiteLetters = {"a", "b", "c", "d", "e", "f", "g", "h"};
    String[] blackLetters = {"h", "g","f", "e", "d", "c", "b", "a"};
    final Map<String, Integer> columns = Map.of(
            "a", 1, "b", 2, "c", 3, "d", 4,
            "e", 5, "f", 6, "g", 7, "h", 8
    );

    public void run() {
        System.out.print("\n");
        System.out.print(help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();
            try{
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
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
        return SET_TEXT_COLOR_LIGHT_GREY+ """
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
//                case "highlight" -> highlight();
                case "quit" -> quit();
                case "help" -> help();
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

    private String redraw() throws ClientException{
        if (color.equals("white")){
            drawWhiteView();
        }
        else if (color.equals("black")){
            drawBlackView();
        }
        return "\nRedrawn!";
    }

    private String leave() throws ClientException{
        ws.leave(server.authToken, gameID);
        state = State.SIGNEDIN;
        return "Leaving game...";
    }

    public String moveDialog(){
        return SET_TEXT_COLOR_LIGHT_GREY+ """
                \n
                Input desired move formatted as:
                move <FROM> <TO>
                
                example:
                move a2 a4
                """;
    }

    private String move() throws ClientException{
        final Map<String, ChessPiece.PieceType> promotionPieces = Map.of(
                "queen", ChessPiece.PieceType.QUEEN,
                "bishop", ChessPiece.PieceType.BISHOP,
                "knight", ChessPiece.PieceType.KNIGHT,
                "rook", ChessPiece.PieceType.ROOK
        );

        out.print(moveDialog());
        out.print("\n"+ SET_TEXT_COLOR_LIGHT_GREY + "[INPUT MOVE] >>> " + SET_TEXT_COLOR_GREEN);

        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] tokens = line.toLowerCase().split(" ");
        if (tokens.length != 2){
            throw new ClientException("Error: incorrect move format");
        }
        String startPos = tokens[0];
        String endPos = tokens[1];
        Integer startCol = columns.get(startPos.substring(0, 1));
        Integer endCol = columns.get(endPos.substring(0, 1));
        int startRow = Integer.parseInt(startPos.substring(1, 2));
        int endRow = Integer.parseInt(endPos.substring(1, 2));

        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(startRow, startCol));

        ChessMove move;

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && (endRow == 8 || endRow ==1)){
            out.print("select promotion piece [rook|queen|bishop|knight]\n"
                    + SET_TEXT_COLOR_LIGHT_GREY + "[PROMOTION PIECE] >> "+ SET_TEXT_COLOR_GREEN);

            line = scanner.nextLine();
            tokens = line.toLowerCase().split(" ");
            if (tokens.length != 1 || !promotionPieces.containsKey(tokens[0])){
                throw new ClientException("Error: incorrect promotion piece format");
            }
            ChessPiece.PieceType type = promotionPieces.get(tokens[0]);
            move = new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol), type);
        }else{
            move = new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol), null);
        }

        ws.makeMove(server.authToken, gameID, move, color);

        return "Move executed";
    }

    private String resign() throws ClientException{
        out.print("\nAre you sure you would like to resign?\n"
                + SET_TEXT_COLOR_LIGHT_GREY + "[y/n] >> "+ SET_TEXT_COLOR_GREEN);
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] tokens = line.toLowerCase().split(" ");
        if (tokens.length != 1){
            throw new ClientException("Error: incorrect answer format");
        }
        if(tokens[0].equals("y")){
            ws.resign(server.authToken, gameID);
            return "Successfully resigned from game";
        }
        else{
            return "Resignation cancelled";
        }
    }

    public String highlightDialog(){
        return SET_TEXT_COLOR_LIGHT_GREY+ """
                \n
                Input the position of the piece you would like to see possible moves for
                example:
                a2
                """;
    }

    private String highlight() throws ClientException{
        out.print(highlightDialog());
        out.print("\n"+ SET_TEXT_COLOR_LIGHT_GREY + "[INPUT POSITION] >>> " + SET_TEXT_COLOR_GREEN);
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] tokens = line.toLowerCase().split(" ");
        if (tokens.length != 1){
            throw new ClientException("Error: incorrect answer format");
        }
        String position = tokens[0];
        Integer col = columns.get(position.substring(0, 1));
        int row = Integer.parseInt(position.substring(1, 2));

        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row, col));
        Collection<ChessMove> possibleMoves = piece.pieceMoves(game.getBoard(), new ChessPosition(row, col));
        for (ChessMove move: possibleMoves){
            validMoves.add(move.getEndPosition());
        }

        if (color.equals("black")){
            drawBlackView();
        }
        else{
            drawWhiteView();
        }

        return "Possible moves highlighted";
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
                ChessPosition currentPos = new ChessPosition(row, i);
                if (validMoves != null && validMoves.contains(currentPos)) {
                    setHighlight(out);
                } else if (blackSquare) {
                    setBlack(out);
                } else {
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
            ChessPosition currentPos = new ChessPosition(row, i);
            if (validMoves != null && validMoves.contains(currentPos)) {
                setHighlight(out);
            } else if (blackSquare) {
                setBlack(out);
            } else {
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

    private static void setHighlight(PrintStream out){
        out.print(SET_BG_COLOR_YELLOW);
    }

    @Override
    public void loadGame(LoadGameMessage message) {
        game = message.getGame();
        if ("black".equals(color)){
            drawBlackView();
        }else{
            drawWhiteView();
        }
        printPrompt();
    }

    @Override
    public void notify(NotificationMessage message) {
        System.out.print(message.getMessage());
        printPrompt();
    }

    @Override
    public void throwError(ErrorMessage message) {
        System.out.print(message.getErrorMessage());
        printPrompt();
    }
}
