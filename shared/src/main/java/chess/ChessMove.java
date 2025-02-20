package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */


public class ChessMove {

    //--------------------------------------------------------------------
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition) && Objects.equals(endPosition, chessMove.endPosition) && promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
    //--------------------------------------------------------------------


    @Override
    public String toString() {
        return "ChessMove{" +
                "startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                ", promotionPiece=" + promotionPiece +
                '}';
    }

    //--------------------------------------------------------------------

    //1) The chess piece has these two characteristics, this goes inside the class
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    //this is the constructor, it has public and the name of the MAIN class (methods have a return)
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {

        //2)   We add them to the constructor
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {

        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {

        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */


    public ChessPiece.PieceType getPromotionPiece() {

        return promotionPiece;
    }
}
