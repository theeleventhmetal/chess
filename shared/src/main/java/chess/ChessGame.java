package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board;

    public ChessGame() {
        this.board = null;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        throw new RuntimeException("Not implemented");
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;

        ChessPiece currentPiece;

        outer:
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                currentPiece = board.getPiece(new ChessPosition(i, j));
                if (currentPiece != null) {
                    if (currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == teamColor) {
                        kingPosition = new ChessPosition(i, j);
                        System.out.printf("King Located at [%d, %d]\n",i, j);
                        break outer;
                    }
                }
            }
        }

        for (int x = 1; x <= 8; x++){
            for (int y = 1; y <= 8; y++){
                currentPiece = board.getPiece(new ChessPosition(x, y));
                if (currentPiece != null){
                    if (currentPiece.getTeamColor() != teamColor){
                        Collection<ChessMove> currentPossibleMoves = currentPiece.pieceMoves(board, new ChessPosition(x,y));
                        System.out.printf("CurrentPiece: %s\n", currentPiece.getPieceType().toString());
                        for(ChessMove move: currentPossibleMoves){
                            System.out.printf("%s ?= %s \n", kingPosition.toString(), move.getEndPosition().toString());
                            if (move.getEndPosition().equals(kingPosition)){
                                System.out.printf("Piece that can check found at [%d, %d]",x,y);
                                return true;
                            }
                        }
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
}
