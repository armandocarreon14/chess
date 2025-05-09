package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */

public class ChessGame {


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return gameOver == chessGame.gameOver && teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board, gameOver);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                ", gameOver=" + gameOver +
                '}';
    }

    private TeamColor teamTurn;
    private ChessBoard board = new ChessBoard();

    private boolean gameOver;

    public ChessGame() {

        this.board.resetBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    public boolean isOver() {
        return gameOver;
    }

    // Sets the game over status
    public void setOver(boolean isOver) {
        this.gameOver = isOver;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK;

    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece myPiece = board.getPiece(startPosition);
        if (myPiece == null){
            return null;
        }

        //Retrieving all moves from my list
        Collection<ChessMove> potentialMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for(ChessMove possibleMove : potentialMoves){
            ChessPiece targetPiece = board.getPiece(possibleMove.getEndPosition());
            board.addPiece(startPosition, null);
            board.addPiece(possibleMove.getEndPosition(), myPiece);

            if(!isInCheck(myPiece.getTeamColor())) {
                validMoves.add(possibleMove);
            }

            board.addPiece(possibleMove.getEndPosition(),targetPiece);
            board.addPiece(startPosition,myPiece);
        }
        return validMoves;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Get piece
        ChessPiece pieceToMove = board.getPiece(move.getStartPosition());

        // Check if there is a piece and if it belongs to my team
        if (pieceToMove == null || pieceToMove.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException();
        }

        // Check if any valid moves is applicable
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves == null || !validMoves.contains(move)) {
            throw new InvalidMoveException();
        }

        // Check promotion
        if (move.getPromotionPiece() != null) {
            pieceToMove = new ChessPiece(pieceToMove.getTeamColor(), move.getPromotionPiece());
        }

        // Move the piece (delete it and place it in the new position)
        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), pieceToMove);

        // Switch turns
        setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //Finding the king
        ChessPosition kingPosition = null;
        for (int row = 1; row <= 8 && kingPosition == null; row ++) {
            for (int column = 1; column <= 8 && kingPosition == null; column ++){
                ChessPiece potentialPiece = board.getPiece(new ChessPosition(row, column));
                if(potentialPiece == null){
                    continue;
                }
                //Find the piece type KING that is the same team as mine
                if(potentialPiece.getTeamColor() == teamColor && potentialPiece.getPieceType() == ChessPiece.PieceType.KING){
                    kingPosition = new ChessPosition(row, column);
                }
            }
        }

        //Check if any enemy piece can attack the king
        for (int row = 1; row <= 8; row ++){
            for(int column = 1; column <= 8; column ++){
                ChessPiece enemyPiece = board.getPiece(new ChessPosition(row, column));
                if (enemyPiece == null || enemyPiece.getTeamColor() == teamColor){
                    continue;
                }

                //Check if an enemy piece can make a new move (from the peicemoves list)
                for(ChessMove potentialEnemyMove : enemyPiece.pieceMoves(board, new ChessPosition(row, column))){
                    //If the enemyPiece kills my king
                    if (potentialEnemyMove.getEndPosition().equals(kingPosition)){
                        return  true;
                    }
                }

            }
        }
        //There is no pieces that would put the king in danger
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     *  Returns true if the specified team’s King could be captured by an opposing piece.
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //Returns true if the given team has no way to protect their king from being captured.

        //Important: check if the enemy can make any moves, if they can then it is not checkmate

        // Check if the team is in check
        if (!isInCheck(teamColor)){
            return false;
        }

        for (int row = 1; row <=8; row ++){
            for (int col = 1; col <= 8; col ++){
                ChessPosition targetPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(targetPosition);

                if (piece == null || piece.getTeamColor() != teamColor){
                    continue;
                }

                //Checking the list of valid moves, if the enemy piece can still make a move then it is not checkmate
                Collection <ChessMove> moves = validMoves(targetPosition);
                if (moves != null && !moves.isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        //Returns true if the given team has no legal moves but their king is not in immediate danger.

        //Check if the team is in check. A team in check cannot be in stalemate
        if (isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row < 9; row ++){
            for (int column = 1; column < 9; column ++){
                ChessPosition targetPosition = new ChessPosition(row, column);
                ChessPiece targetPiece = board.getPiece(targetPosition);

                Collection<ChessMove> moves;

                //If the square is not empty AND the piece is part of my team
                if(targetPiece != null && teamColor == targetPiece.getTeamColor()) {
                    moves = validMoves(targetPosition);
                    //There needs to be no valid moves to validate the stalemate
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
                }
            }
            return true;
        }

    /**
     * Sets this game's chessboard with a given board
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