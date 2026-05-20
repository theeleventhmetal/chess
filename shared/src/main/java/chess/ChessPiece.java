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
        Collection<ChessMove> possibleMoves = null;
        possibleMoves = new ArrayList<>();
        int rowVal = myPosition.getRow();
        int colVal = myPosition.getColumn();
        ChessGame.TeamColor color = piece.getTeamColor();
        ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;

        if (piece.getPieceType() == PieceType.PAWN) {
            if (color == white) { // WHITE MOVES UP THE BOARD
                if (rowVal < 7) {
                    if (board.getPiece(new ChessPosition(rowVal + 1, colVal)) == null) {
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal + 1, colVal), null));
                        if ((rowVal == 2) && board.getPiece(new ChessPosition(rowVal + 2, colVal)) == null) {
                            addMove(possibleMoves, rowVal, colVal, 2, 0, false);
                        }
                    }
                    if ((colVal < 8) &&
                            board.getPiece(new ChessPosition(rowVal + 1, colVal + 1)) != null &&
                            board.getPiece(new ChessPosition(rowVal + 1, colVal + 1)).getTeamColor() == black) {
                        addMove(possibleMoves, rowVal, colVal, 1, 1, false);

                    }
                    if ((colVal > 1) &&
                            board.getPiece(new ChessPosition(rowVal + 1, colVal - 1)) != null &&
                            board.getPiece(new ChessPosition(rowVal + 1, colVal - 1)).getTeamColor() == black) {
                        addMove(possibleMoves, rowVal, colVal, 1, -1, false);
                    }
                }
                if (rowVal == 7) {
                    if (board.getPiece(new ChessPosition(rowVal + 1, colVal)) == null) {
                        addMove(possibleMoves, rowVal, colVal, 1, 0, true);
                    }
                    if ((colVal < 8) &&
                            board.getPiece(new ChessPosition(rowVal + 1, colVal + 1)) != null &&
                            board.getPiece(new ChessPosition(rowVal + 1, colVal + 1)).getTeamColor() == black) {
                        addMove(possibleMoves, rowVal, colVal, 1, 1, true);

                    }
                    if ((colVal > 1) &&
                            board.getPiece(new ChessPosition(rowVal + 1, colVal - 1)) != null &&
                            board.getPiece(new ChessPosition(rowVal + 1, colVal - 1)).getTeamColor() == black) {
                        addMove(possibleMoves, rowVal, colVal, 1, -1, true);
                    }
                }
            } else { // BLACK MOVES DOWN
                if (rowVal > 2) {
                    if (board.getPiece(new ChessPosition(rowVal - 1, colVal)) == null) {
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal - 1, colVal), null));
                        if ((rowVal == 7) && board.getPiece(new ChessPosition(rowVal - 2, colVal)) == null) {
                            addMove(possibleMoves, rowVal, colVal, -2, 0, false);
                        }
                    }
                    if ((colVal < 8) &&
                            board.getPiece(new ChessPosition(rowVal - 1, colVal + 1)) != null &&
                            board.getPiece(new ChessPosition(rowVal - 1, colVal + 1)).getTeamColor() == white) {
                        addMove(possibleMoves, rowVal, colVal, -1, 1, false);
                    }
                    if ((colVal > 1) &&
                            board.getPiece(new ChessPosition(rowVal - 1, colVal - 1)) != null &&
                            board.getPiece(new ChessPosition(rowVal - 1, colVal - 1)).getTeamColor() == white) {
                        addMove(possibleMoves, rowVal, colVal, -1, -1, false);
                    }
                }
                if (rowVal == 2) {
                    if (board.getPiece(new ChessPosition(rowVal - 1, colVal)) == null) {
                        addMove(possibleMoves, rowVal, colVal, -1, 0, true);
                    }
                    if ((colVal < 8) &&
                            board.getPiece(new ChessPosition(rowVal - 1, colVal + 1)) != null &&
                            board.getPiece(new ChessPosition(rowVal - 1, colVal + 1)).getTeamColor() == white) {
                        addMove(possibleMoves, rowVal, colVal, -1, +1, true);

                    }
                    if ((colVal > 1) &&
                            board.getPiece(new ChessPosition(rowVal - 1, colVal - 1)) != null &&
                            board.getPiece(new ChessPosition(rowVal - 1, colVal - 1)).getTeamColor() == white) {
                        addMove(possibleMoves, rowVal, colVal, -1, -1, true);
                    }
                }

            }
        }

        if (piece.getPieceType() == PieceType.KING) {
            int[][] surroundingSquares = {
                    {rowVal + 1, colVal + 1},
                    {rowVal + 1, colVal},
                    {rowVal + 1, colVal - 1},
                    {rowVal, colVal + 1},
                    {rowVal, colVal - 1},
                    {rowVal - 1, colVal - 1},
                    {rowVal - 1, colVal},
                    {rowVal - 1, colVal + 1}};

            for (int[] square : surroundingSquares) {
                int row = square[0];
                int col = square[1];
                if ((row >= 1 && row <= 8) && (col >= 1 && col <= 8)) {
                    if (board.getPiece(new ChessPosition(row, col)) == null) {
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(row, col), null));
                    }
                    if (board.getPiece(new ChessPosition(row, col)) != null && board.getPiece(new ChessPosition(row, col)).getTeamColor() != color) {
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(row, col), null));
                    }
                }
            }
        }

        if (piece.getPieceType() == PieceType.KNIGHT) {
            int[][] surroundingSquares = {
                    {rowVal + 1, colVal + 2},
                    {rowVal + 1, colVal - 2},
                    {rowVal - 1, colVal + 2},
                    {rowVal - 1, colVal - 2},
                    {rowVal + 2, colVal + 1},
                    {rowVal + 2, colVal - 1},
                    {rowVal - 2, colVal + 1},
                    {rowVal - 2, colVal - 1}};

            for (int[] square : surroundingSquares) {
                int row = square[0];
                int col = square[1];
                if ((row >= 1 && row <= 8) && (col >= 1 && col <= 8)) {
                    if (board.getPiece(new ChessPosition(row, col)) == null) {
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(row, col), null));
                    }
                    if (board.getPiece(new ChessPosition(row, col)) != null && board.getPiece(new ChessPosition(row, col)).getTeamColor() != color) {
                        possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(row, col), null));
                    }
                }
            }

        }

        if (piece.getPieceType() == PieceType.QUEEN || piece.getPieceType() == PieceType.BISHOP) {
            int i = rowVal;
            int j = colVal;
            while (i < 8 && j < 8) { //++
                i++;
                j++;
                if (board.getPiece(new ChessPosition(i, j)) == null) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                } else if (board.getPiece(new ChessPosition(i, j)) != null && board.getPiece(new ChessPosition(i, j)).getTeamColor() != color) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    break;
                } else {
                    break;
                }
            }
            i = rowVal;
            j = colVal;
            while (i < 8 && j > 1) { //+-
                i++;
                j--;
                if (board.getPiece(new ChessPosition(i, j)) == null) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                } else if (board.getPiece(new ChessPosition(i, j)) != null && board.getPiece(new ChessPosition(i, j)).getTeamColor() != color) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    break;
                } else {
                    break;
                }
            }
            i = rowVal;
            j = colVal;
            while (i > 1 && j > 1) { //--
                i--;
                j--;
                if (board.getPiece(new ChessPosition(i, j)) == null) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                } else if (board.getPiece(new ChessPosition(i, j)) != null && board.getPiece(new ChessPosition(i, j)).getTeamColor() != color) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    break;
                } else {
                    break;
                }
            }
            i = rowVal;
            j = colVal;
            while (i > 1 && j < 8) { //-+
                i--;
                j++;
                if (board.getPiece(new ChessPosition(i, j)) == null) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                } else if (board.getPiece(new ChessPosition(i, j)) != null && board.getPiece(new ChessPosition(i, j)).getTeamColor() != color) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    break;
                } else {
                    break;
                }
            }


        }

        if (piece.getPieceType() == PieceType.QUEEN || piece.getPieceType() == PieceType.ROOK){
            int i = rowVal;
            int j = colVal;
            while (i < 8) { // +0
                i++;
                if (board.getPiece(new ChessPosition(i, j)) == null) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                } else if (board.getPiece(new ChessPosition(i, j)) != null && board.getPiece(new ChessPosition(i, j)).getTeamColor() != color) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    break;
                } else {
                    break;
                }
            }
            i = rowVal;
            while (i > 1) { // -0
                i--;
                if (board.getPiece(new ChessPosition(i, j)) == null) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                } else if (board.getPiece(new ChessPosition(i, j)) != null && board.getPiece(new ChessPosition(i, j)).getTeamColor() != color) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    break;
                } else {
                    break;
                }
            }
            i = rowVal;
            while (j < 8) { // 0+
                j++;
                if (board.getPiece(new ChessPosition(i, j)) == null) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                } else if (board.getPiece(new ChessPosition(i, j)) != null && board.getPiece(new ChessPosition(i, j)).getTeamColor() != color) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    break;
                } else {
                    break;
                }
            }
            j = colVal;
            while (j > 1) { // -0
                j--;
                if (board.getPiece(new ChessPosition(i, j)) == null) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                } else if (board.getPiece(new ChessPosition(i, j)) != null && board.getPiece(new ChessPosition(i, j)).getTeamColor() != color) {
                    possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(i, j), null));
                    break;
                } else {
                    break;
                }
            }
        }

        return possibleMoves;
    }

    private void addMove(Collection<ChessMove> possibleMoves, int rowVal, int colVal, int rowOffset, int colOffset, boolean promotionPiece){

        if (!promotionPiece){
            possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal + rowOffset, colVal + colOffset), null));
        }
        else{
            possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal + rowOffset, colVal + colOffset), PieceType.QUEEN));
            possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal + rowOffset, colVal + colOffset), PieceType.KNIGHT));
            possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal + rowOffset, colVal + colOffset), PieceType.ROOK));
            possibleMoves.add(new ChessMove(new ChessPosition(rowVal, colVal), new ChessPosition(rowVal + rowOffset, colVal + colOffset), PieceType.BISHOP));
        }
    }
}
