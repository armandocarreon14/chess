package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    //--------------------------------------------------------------------
    //OBJECT OVERRIDE
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }
    //
    //--------------------------------------------------------------------


    //1) The chess piece has these two characteristics, this goes inside the class
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType pieceType;

    //this is the constructor, it has public and the name of the MAIN class (methods have a return)
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        //2) We add them to the constructor
        this.pieceColor = pieceColor;
        this.pieceType = type;
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
        //3) return color to know which color is it during the game (this will be used outside the class)
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        //4) return
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    //we check if there is 1) an empty space, 2) if it's out of range, and 3) if there is a piece there
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        //1) Create the collection
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        //2 if this piece is... (e.g. king)
        if (pieceType == PieceType.KING) {

            Collection<ChessPosition> directions = new ArrayList<>();

            directions.add(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()));
            directions.add(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1));
            directions.add(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1));
            directions.add(new ChessPosition(myPosition.getRow() -1, myPosition.getColumn()));
            directions.add(new ChessPosition(myPosition.getRow() -1, myPosition.getColumn()-1));
            directions.add(new ChessPosition(myPosition.getRow() -1, myPosition.getColumn()+1));
            directions.add(new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1));
            directions.add(new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1 ));

            //ChessPosition targetPosition1 = new ChessPosition(myPosition.getRow() + -1 , myPosition.getColumn());

            for (ChessPosition targetPosition : directions) {
                //check if it's inside the board
                if (targetPosition.getRow() >= 1 && targetPosition.getRow() <= 8) {
                    if (targetPosition.getColumn() >= 1 && targetPosition.getColumn() <= 8) {
                        //check if there is an empty space or if there is a piece and it is an enemy piece
                        if (board.getPiece(targetPosition) == null || board.getPiece(targetPosition).pieceColor != pieceColor ) {
                            //We add the piece. This structure comes from ChessMove
                            possibleMoves.add(new ChessMove(myPosition, targetPosition, null));

                        }
                    }
                }

            }

        } else if (pieceType == PieceType.QUEEN) {

        //Upwards
        for (int row = myPosition.getRow() +1; row < 9 ; row++) {
            ChessPosition newPosition = new ChessPosition(row, myPosition.getColumn());
            if (board.getPiece(newPosition) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (board.getPiece(newPosition).pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }

        //Downwards
        for (int row = myPosition.getRow() -1; row > 0; row --) {
            ChessPosition newPosition = new ChessPosition(row, myPosition.getColumn());
            if (board.getPiece(newPosition) == null ) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (board.getPiece(newPosition).pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }

        //Right
        for (int column = myPosition.getColumn() +1; column < 9; column ++) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow(), column);
            if (board.getPiece(newPosition) == null ) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (board.getPiece(newPosition).pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }

        //Left
        for (int column = myPosition.getColumn() - 1; column > 0; column --) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow(), column);
            if (board.getPiece(newPosition) == null ) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (board.getPiece(newPosition).pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }

        //Top right
        for (int row = myPosition.getRow() + 1, column = myPosition.getColumn() + 1; row < 9 && column < 9; row++, column++) {
            ChessPosition newPosition = new ChessPosition(row, column);
            if (board.getPiece(newPosition) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (board.getPiece(newPosition).pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }

        // Bottom right
        for (int row = myPosition.getRow() - 1, column = myPosition.getColumn() + 1; row > 0 && column < 9; row--, column++) {
            ChessPosition newPosition = new ChessPosition(row, column);
            if (board.getPiece(newPosition) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (board.getPiece(newPosition).pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }

        // Top left
        for (int row = myPosition.getRow() +1, column = myPosition.getColumn() -1; row < 9 && column > 0; row++, column--) {
            ChessPosition newPosition = new ChessPosition(row, column);
            if (board.getPiece(newPosition) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (board.getPiece(newPosition).pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }

        //Bottom left
        for (int row = myPosition.getRow() -1, column = myPosition.getColumn() -1; row > 0 && column > 0; row --, column --) {
            ChessPosition newPosition = new ChessPosition(row, column);
            if (board.getPiece(newPosition) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (board.getPiece(newPosition).pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }



        } else if (pieceType == PieceType.BISHOP) {

            //Top right
        for (int row = myPosition.getRow() + 1, column = myPosition.getColumn() + 1; row < 9 && column < 9; row++, column++) {
            ChessPosition newPosition = new ChessPosition(row, column);
            if (board.getPiece(newPosition) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (board.getPiece(newPosition).pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }

        // Bottom right
        for (int row = myPosition.getRow() - 1, column = myPosition.getColumn() + 1; row > 0 && column < 9; row--, column++) {
            ChessPosition newPosition = new ChessPosition(row, column);
            if (board.getPiece(newPosition) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (board.getPiece(newPosition).pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }


        //Bottom left
        for (int row = myPosition.getRow() -1, column = myPosition.getColumn() -1; row > 0 && column > 0; row --, column --) {
            ChessPosition newPosition = new ChessPosition(row, column);
            if (board.getPiece(newPosition) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (board.getPiece(newPosition).pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }

        // Top left
        for (int row = myPosition.getRow() +1, column = myPosition.getColumn() -1; row < 9 && column > 0; row++, column--) {
        ChessPosition newPosition = new ChessPosition(row, column);
        if (board.getPiece(newPosition) == null) {
            possibleMoves.add(new ChessMove(myPosition, newPosition, null));
        } else {
            if (board.getPiece(newPosition).pieceColor != pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            }
            break;
        }
    }

        } else if (pieceType == PieceType.KNIGHT) {

            Collection<ChessPosition> directions = new ArrayList<>();

            directions.add(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1));
            directions.add(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1));
            directions.add(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1));
            directions.add(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1));
            directions.add(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2));
            directions.add(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2));
            directions.add(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2));
            directions.add(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2));


            for (ChessPosition targetPosition : directions) {
                //check if it's inside the board
                if (targetPosition.getRow() >= 1 && targetPosition.getRow() <= 8) {
                    if (targetPosition.getColumn() >= 1 && targetPosition.getColumn() <= 8) {
                        //check if there is an empty space or if there is a piece and it is an enemy piece
                        if (board.getPiece(targetPosition) == null || board.getPiece(targetPosition).pieceColor != pieceColor ) {
                            //We add the piece. This structure comes from ChessMove
                            possibleMoves.add(new ChessMove(myPosition, targetPosition, null));

                        }
                    }
                }

            }

        } else if (pieceType == PieceType.ROOK) {

        //Upwards
        for (int row = myPosition.getRow() +1; row < 9 ; row++) {
            ChessPosition newPosition = new ChessPosition(row, myPosition.getColumn());
            if (board.getPiece(newPosition) == null) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (board.getPiece(newPosition).pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }

        //Downwards
        for (int row = myPosition.getRow() -1; row > 0; row --) {
            ChessPosition newPosition = new ChessPosition(row, myPosition.getColumn());
            if (board.getPiece(newPosition) == null ) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (board.getPiece(newPosition).pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }

        //Right
        for (int column = myPosition.getColumn() +1; column < 9; column ++) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow(), column);
            if (board.getPiece(newPosition) == null ) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (board.getPiece(newPosition).pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }

        //Left
        for (int column = myPosition.getColumn() - 1; column > 0; column --) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow(), column);
            if (board.getPiece(newPosition) == null ) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                if (board.getPiece(newPosition).pieceColor != pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            }
        }

        } else if (pieceType == PieceType.PAWN) {

            // whites moves up and blacks move down
            int direction = pieceColor == ChessGame.TeamColor.WHITE ? 1 : -1;

            // Moving one
            ChessPosition oneMove = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());

            if (oneMove.getRow() >= 1 && oneMove.getRow() <= 8 && board.getPiece(oneMove) == null) {
                //if it lands on the last/first row
                if ((pieceColor == ChessGame.TeamColor.WHITE && oneMove.getRow() == 8) ||
                        (pieceColor == ChessGame.TeamColor.BLACK && oneMove.getRow() == 1)) {
                    possibleMoves.add(new ChessMove(myPosition, oneMove, PieceType.ROOK));
                    possibleMoves.add(new ChessMove(myPosition, oneMove, PieceType.KNIGHT));
                    possibleMoves.add(new ChessMove(myPosition, oneMove, PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(myPosition, oneMove, PieceType.QUEEN));

                }
                else {
                    possibleMoves.add(new ChessMove(myPosition, oneMove, null));
                }

                    // Moving two
                //if it is in starting position
                if ((pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) ||
                        (pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7)) {
                    ChessPosition twoStepsForward = new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn());
                    if (board.getPiece(twoStepsForward) == null) {
                        possibleMoves.add(new ChessMove(myPosition, twoStepsForward, null));
                    }
                }
            }

            // Diagonal moves
            Collection<ChessPosition> diagonalMoves = new ArrayList<>();
            diagonalMoves.add(new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1));
            diagonalMoves.add(new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + 1));

            //iterates each potential diagonal move
            for (ChessPosition targetPosition : diagonalMoves) {
                //if it's inside the board
                if (targetPosition.getRow() >= 1 && targetPosition.getRow() <= 8 &&
                        targetPosition.getColumn() >= 1 && targetPosition.getColumn() <= 8) {
                    //get the piece that is in target position
                    ChessPiece targetPiece = board.getPiece(targetPosition);


                    if (targetPiece != null && targetPiece.getTeamColor() != pieceColor) {

                        if ((pieceColor == ChessGame.TeamColor.WHITE && targetPosition.getRow() == 8) ||
                            (pieceColor == ChessGame.TeamColor.BLACK && targetPosition.getRow() == 1)) {

                        possibleMoves.add(new ChessMove(myPosition, targetPosition, PieceType.ROOK));
                        possibleMoves.add(new ChessMove(myPosition, targetPosition, PieceType.KNIGHT));
                        possibleMoves.add(new ChessMove(myPosition, targetPosition, PieceType.BISHOP));
                        possibleMoves.add(new ChessMove(myPosition, targetPosition, PieceType.QUEEN));

                        }
                        else {
                            possibleMoves.add(new ChessMove(myPosition, targetPosition, null));

                        }
                    }
                }
            }
        }

        return possibleMoves;
}
}
