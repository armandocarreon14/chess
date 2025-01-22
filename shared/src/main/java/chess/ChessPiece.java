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
            //we start at the same row, and we go until <9
        for (int row = myPosition.getRow()+1; row < 9; row ++) {
                //creating a new position, i scan the row but the column stays the same
                ChessPosition newPosition = new ChessPosition(row, myPosition.getColumn());
                if (board.getPiece(newPosition) == null) {
                    //We add the piece. This structure comes from ChessMove
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
            ChessPosition newPosition = new ChessPosition(column, myPosition.getRow());
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
            ChessPosition newPosition = new ChessPosition(column, myPosition.getRow());
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


        } else if (pieceType == PieceType.KNIGHT) {

        } else if (pieceType == PieceType.ROOK) {

        } else if (pieceType == PieceType.PAWN) {
        }




    //2) calculate
    // ** create in the beginning the collection Collection<ChessMove> name = new ArrayList<>();
    //3) return a collection of possible chess moves = arraylist

        return possibleMoves;
}
}
