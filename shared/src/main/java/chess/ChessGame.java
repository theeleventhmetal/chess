package chess;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board = new ChessBoard();
    TeamColor activeColor;

    public ChessGame() {
        this.board.resetBoard();
        this.activeColor = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return activeColor;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.activeColor = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && activeColor == chessGame.activeColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, activeColor);
    }

    @Override
    public String toString() {
        return new Gson().toJson(board);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        ChessPiece piece = this.board.getPiece(startPosition);
        Collection<ChessMove> validMoves;
        validMoves = new ArrayList<>();
        ChessBoard actualBoard = this.board.deepCopy();

        if (piece == null){
            return null;
        }
        else{
            Collection<ChessMove> possibleMoves = piece.pieceMoves(this.board, startPosition);
            TeamColor color = piece.getTeamColor();
            for (ChessMove move: possibleMoves ){
                this.board = this.board.deepCopy();
                ChessPosition tempStartPosition = move.getStartPosition();
                ChessPosition tempEndPosition = move.getEndPosition();
                this.board.addPiece(tempEndPosition, piece);
                this.board.addPiece(tempStartPosition, null);
                if(!isInCheck(color)){
                    validMoves.add(move);
                }
                this.board = actualBoard;
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = this.board.getPiece(startPosition);
        if (piece != null && piece.getTeamColor() == this.activeColor){
            Collection<ChessMove> validMoves = validMoves(startPosition);
            if (validMoves.contains(move)){
                if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null){
                    ChessPiece.PieceType promotionPieceType = move.getPromotionPiece();
                    ChessPiece promotionPiece = new ChessPiece(this.activeColor, promotionPieceType);
                    this.board.addPiece(move.getEndPosition(), promotionPiece);
                }
                else{
                    this.board.addPiece(move.getEndPosition(), piece);
                }
                this.board.addPiece(startPosition, null);
                if (this.activeColor == TeamColor.WHITE) {
                    this.activeColor = TeamColor.BLACK;
                } else {
                    this.activeColor = TeamColor.WHITE;
                }
            }
            else {
                throw new InvalidMoveException("Move is Invalid");
            }
        }
        else {
            throw new InvalidMoveException("Move is Invalid");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition kingPosition = kingPositionHelper(teamColor);
        ChessPiece currentPiece;

        for (int x = 1; x <= 8; x++){
            for (int y = 1; y <= 8; y++){
                currentPiece = board.getPiece(new ChessPosition(x, y));
                if (currentPiece != null && currentPiece.getTeamColor() != teamColor){
                    if (isInCheckHelper(currentPiece, kingPosition, x, y)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = kingPositionHelper(teamColor);
        ChessPiece currentPiece;

        assert kingPosition != null;
        ChessPiece king = board.getPiece(kingPosition);
        Collection<ChessMove> possibleKingMoves = king.pieceMoves(board, kingPosition);
        ChessBoard actualBoard = this.board.deepCopy();


        for (int x = 1; x <= 8; x++){
            for (int y = 1; y <= 8; y++){
                currentPiece = board.getPiece(new ChessPosition(x, y));
                if (currentPiece != null && currentPiece.getTeamColor() == teamColor){
                    if (!validMoves(new ChessPosition(x,y)).isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPiece currentPiece;

        if (isInCheck(teamColor)){
            return false;
        }

        for (int x = 1; x <= 8; x++){
            for (int y = 1; y <= 8; y++){
                currentPiece = board.getPiece(new ChessPosition(x, y));
                if (currentPiece != null &&
                    currentPiece.getTeamColor() == teamColor &&
                    !validMoves(new ChessPosition(x,y)).isEmpty()){
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private boolean isInCheckHelper(ChessPiece currentPiece, ChessPosition kingPosition, int x, int y){
        Collection<ChessMove> currentPossibleMoves = currentPiece.pieceMoves(board, new ChessPosition(x,y));
        for(ChessMove move: currentPossibleMoves){
            if (move.getEndPosition().equals(kingPosition)){
                return true;
            }
        }
        return false;
    }

    private ChessPosition kingPositionHelper(TeamColor teamColor){
        ChessPiece currentPiece;
        ChessPosition kingPosition = null;
        outer:
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                currentPiece = board.getPiece(new ChessPosition(i, j));
                if (currentPiece != null &&
                        currentPiece.getPieceType() == ChessPiece.PieceType.KING &&
                        currentPiece.getTeamColor() == teamColor) {
                    kingPosition = new ChessPosition(i, j);
                    break outer;
                }
            }
        }
        return kingPosition;
    }
}
