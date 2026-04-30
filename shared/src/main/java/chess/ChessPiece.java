package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        int rowVal = myPosition.getRow();
        int colVal = myPosition.getColumn();
        Collection<ChessMove> possibleMoves = null;
        ChessGame.TeamColor color = piece.getTeamColor();
        ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;




        if (piece.getPieceType() == PieceType.BISHOP) {
            possibleMoves = new ArrayList<>();
            int i = rowVal;
            int j = colVal;
            while (i < 8 & j < 8){ // ++
                i++;
                j++;
                if (board.getPiece(new ChessPosition(i,j)) == null) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    System.out.println("BREAK");
                    break;
                }
            }
            i = rowVal;
            j = colVal;
            while (i < 8 & j > 1 ){ // +-
                i++;
                j--;
                if (board.getPiece(new ChessPosition(i,j)) == null ){
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i,j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    break;
                }
            }
            i = rowVal;
            j = colVal;
            while (i > 1 & j > 1){ // --
                i--;
                j--;
                if (board.getPiece(new ChessPosition(i,j)) == null){
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i,j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    break;
                }
            }
            i = rowVal;
            j = colVal;
            while (i > 1 & j < 8){ // -+
                i--;
                j++;
                if (board.getPiece(new ChessPosition(i,j)) == null){
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i,j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    break;
                }
            }
        }





        if (piece.getPieceType() == PieceType.KING) {
            possibleMoves = new ArrayList<>();
            int[][] neighborSquares = {{rowVal-1, colVal-1}, {rowVal-1, colVal}, {rowVal-1, colVal+1}, {rowVal, colVal-1}, {rowVal, colVal+1}, {rowVal+1, colVal-1}, {rowVal+1, colVal}, {rowVal+1, colVal+1}};

            for (int[] square:neighborSquares){
                int row = square[0];
                int col = square[1];
                if ((row > 1 && row < 8) & (col > 1 && col < 8)) {
                    if (board.getPiece(new ChessPosition(row,col)) == null) {
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(row, col), null));
                    }
                    else if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != color){
                        System.out.println("CAPTURABLE PIECE DETECTED");
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(row, col), null));
                        System.out.println("ADDED, NOW BREAKING LOOP");
                    }
                }
            }
        }


        if (piece.getPieceType() == PieceType.KNIGHT) {
            possibleMoves = new ArrayList<>();
            int[][] neighborSquares = {{rowVal+2, colVal+1}, {rowVal+2, colVal-1}, {rowVal-2, colVal-1}, {rowVal-2, colVal+1}, {rowVal+1, colVal+2}, {rowVal+1, colVal-2}, {rowVal-1, colVal-2}, {rowVal-1, colVal+2}};

            for (int[] square:neighborSquares){
                int row = square[0];
                int col = square[1];
                if ((row >= 1 && row <= 8) & (col >= 1 && col <= 8)) {
                    if (board.getPiece(new ChessPosition(row,col)) == null) {
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(row, col), null));
                    }
                    else if (board.getPiece(new ChessPosition(row,col)).getTeamColor() != color){
                        System.out.println("CAPTURABLE PIECE DETECTED");
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(row, col), null));
                        System.out.println("ADDED, NOW BREAKING LOOP");
                    }
                }
            }
        }

        if ((piece.getPieceType() == PieceType.PAWN)) {
            possibleMoves = new ArrayList<>();

            if (color == white){ //WHITE PIECES MOVE UP THE GRID
                if(rowVal == 2){ // WHITE FIRST MOVE
                    if (board.getPiece(new ChessPosition(rowVal+1,colVal)) == null){
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal), null));
                        if (board.getPiece(new ChessPosition(rowVal+2,colVal)) == null){
                            possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+2, colVal), null));
                        }
                    }
                    if ((colVal < 8 && colVal > 1) && board.getPiece(new ChessPosition(rowVal+1,colVal+1)) != null && board.getPiece(new ChessPosition(rowVal+1,colVal+1)).getTeamColor() != white){
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal+1), null));
                    }
                    if ((colVal < 8 && colVal > 1) &&board.getPiece(new ChessPosition(rowVal+1,colVal-1)) != null && board.getPiece(new ChessPosition(rowVal+1,colVal-1)).getTeamColor() != white){
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal-1), null));
                    }
                }
                else if (rowVal > 2 && rowVal < 7){
                    if (board.getPiece(new ChessPosition(rowVal+1,colVal)) == null) {
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal), null));
                    }
                    if ((colVal < 8 && colVal > 1) && board.getPiece(new ChessPosition(rowVal+1,colVal+1)) != null && board.getPiece(new ChessPosition(rowVal+1,colVal+1)).getTeamColor() != white){
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal+1), null));
                    }
                    if ((colVal-1 < 8 && colVal > 1) && board.getPiece(new ChessPosition(rowVal+1,colVal-1)) != null && board.getPiece(new ChessPosition(rowVal+1,colVal-1)).getTeamColor() != white){
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal-1), null));
                    }
                }
                else { // PROMOTION POTENTIAL
                    if (board.getPiece(new ChessPosition(rowVal+1,colVal)) == null) {
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal), PieceType.KNIGHT));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal), PieceType.QUEEN));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal), PieceType.ROOK));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal), PieceType.BISHOP));
                    }
                    if ((colVal < 8 && colVal > 1) && board.getPiece(new ChessPosition(rowVal+1,colVal+1)) != null && board.getPiece(new ChessPosition(rowVal+1,colVal+1)).getTeamColor() != white){
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal+1), PieceType.KNIGHT));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal+1), PieceType.QUEEN));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal+1), PieceType.ROOK));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal+1), PieceType.BISHOP));
                    }
                    if ((colVal-1 < 8 && colVal > 1) && board.getPiece(new ChessPosition(rowVal+1,colVal-1)) != null && board.getPiece(new ChessPosition(rowVal+1,colVal-1)).getTeamColor() != white){
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal-1), PieceType.KNIGHT));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal-1), PieceType.QUEEN));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal-1), PieceType.ROOK));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal+1, colVal-1), PieceType.BISHOP));

                    }
                }
            }


            else { //BLACK PIECES MOVE DOWN THE GRID
                if(rowVal == 7){ // BLACK FIRST MOVE
                    if (board.getPiece(new ChessPosition(rowVal-1,colVal)) == null){
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal), null));
                        if (board.getPiece(new ChessPosition(rowVal-2,colVal)) == null){
                            possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-2, colVal), null));
                        }
                    }
                    if ((colVal < 8 && colVal > 1) && board.getPiece(new ChessPosition(rowVal-1,colVal-1)) != null && board.getPiece(new ChessPosition(rowVal-1,colVal-1)).getTeamColor() != black){
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal-1), null));
                    }
                    if ((colVal < 8 && colVal > 1) && board.getPiece(new ChessPosition(rowVal-1,colVal+1))!= null && board.getPiece(new ChessPosition(rowVal-1,colVal+1)).getTeamColor() != black){
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal+1), null));
                    }
                }
                else if (rowVal < 7 && rowVal > 2){
                    if (board.getPiece(new ChessPosition(rowVal-1,colVal)) == null) {
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal), null));
                    }
                    if ((colVal-1 < 8 && colVal > 1) && board.getPiece(new ChessPosition(rowVal-1,colVal-1)) != null && board.getPiece(new ChessPosition(rowVal-1,colVal-1)).getTeamColor() != black){
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal-1), null));
                    }
                    if ((colVal < 8 && colVal > 1) && board.getPiece(new ChessPosition(rowVal-1,colVal+1))!= null && board.getPiece(new ChessPosition(rowVal-1,colVal+1)).getTeamColor() != black){
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal+1), null));
                    }
                }
                else { // PROMOTION POTENTIAL
                    if (board.getPiece(new ChessPosition(rowVal-1,colVal)) == null) {
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal), PieceType.KNIGHT));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal), PieceType.QUEEN));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal), PieceType.ROOK));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal), PieceType.BISHOP));
                    }
                    if ((colVal-1 < 8 && colVal > 1) && board.getPiece(new ChessPosition(rowVal-1,colVal-1)) != null && board.getPiece(new ChessPosition(rowVal-1,colVal-1)).getTeamColor() != black){
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal-1), PieceType.KNIGHT));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal-1), PieceType.QUEEN));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal-1), PieceType.ROOK));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal-1), PieceType.BISHOP));
                    }
                    if ((colVal < 8 && colVal > 1) && board.getPiece(new ChessPosition(rowVal-1,colVal+1))!= null && board.getPiece(new ChessPosition(rowVal-1,colVal+1)).getTeamColor() != black){
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal+1), PieceType.KNIGHT));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal+1), PieceType.QUEEN));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal+1), PieceType.ROOK));
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal-1, colVal+1), PieceType.BISHOP));
                    }
                }
            }
        }

        if (piece.getPieceType() == PieceType.QUEEN) {
            possibleMoves = new ArrayList<>();
            int i = rowVal;
            int j = colVal;
            while (i < 8 & j < 8){ // ++
                i++;
                j++;
                if (board.getPiece(new ChessPosition(i,j)) == null) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    break;
                }
            }
            i = rowVal;
            j = colVal;
            while (i < 8 & j > 1 ){ // +-
                i++;
                j--;
                if (board.getPiece(new ChessPosition(i,j)) == null ){
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i,j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    break;
                }
            }
            i = rowVal;
            j = colVal;
            while (i > 1 & j > 1){ // --
                i--;
                j--;
                if (board.getPiece(new ChessPosition(i,j)) == null){
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i,j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    break;
                }
            }
            i = rowVal;
            j = colVal;
            while (i > 1 & j < 8){ // -+
                i--;
                j++;
                if (board.getPiece(new ChessPosition(i,j)) == null){
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i,j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    break;
                }
            }
            i = rowVal;
            j = colVal;
            while (i < 8) { // +0
                i++;
                if (board.getPiece(new ChessPosition(i, j)) == null){
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i,j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    break;
                }
            }
            i = rowVal;
            while (i > 1) { // -0
                i--;
                if (board.getPiece(new ChessPosition(i, j)) == null){
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i,j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    break;
                }
            }
            i = rowVal;
            while (j < 8) { // 0+
                j++;
                if (board.getPiece(new ChessPosition(i, j)) == null){
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i,j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    break;
                }
            }
            j = colVal;
            while (j > 1) { // 0-
                j--;
                if (board.getPiece(new ChessPosition(i, j)) == null){
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i,j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    break;
                }
            }
        }

        if (piece.getPieceType() == PieceType.ROOK){
            possibleMoves = new ArrayList<>();
            int i = rowVal;
            int j = colVal;
            while (i < 8) { // +0
                i++;
                if (board.getPiece(new ChessPosition(i, j)) == null){
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i,j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    break;
                }
            }
            i = rowVal;
            while (i > 1) { // -0
                i--;
                if (board.getPiece(new ChessPosition(i, j)) == null){
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i,j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    break;
                }
            }
            i = rowVal;
            while (j < 8) { // 0+
                j++;
                if (board.getPiece(new ChessPosition(i, j)) == null){
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i,j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    break;
                }
            }
            j = colVal;
            while (j > 1) { // 0-
                j--;
                if (board.getPiece(new ChessPosition(i, j)) == null){
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i,j), null));
                }
                else if (board.getPiece(new ChessPosition(i,j)).getTeamColor() != color){
                    System.out.println("CAPTURABLE PIECE DETECTED");
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    System.out.println("ADDED, NOW BREAKING LOOP");
                    break;
                }
                else{
                    break;
                }
            }
        }

        return possibleMoves;
    }
}
