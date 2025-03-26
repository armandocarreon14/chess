package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import static ui.EscapeSequences.*;

public class CreateBoard {
    private static final int BOARD_SIZE = 8; // Defines the size of the chessboard
    private static final ChessBoard board = new ChessBoard(); // Creates a new chessboard instance

    public static void displayBoard(String teamColor) {
        System.out.println();
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        board.resetBoard(); // Resets the board to the initial state

        out.print(ERASE_SCREEN); // Clears the console screen
        drawHeaders(out, teamColor); // Draws column headers
        drawBoard(out, board, teamColor); // Draws the chessboard with pieces
        drawHeaders(out, teamColor); // Draws headers again at the bottom

        out.print(SET_BG_COLOR_BLACK); // Resets background color
        out.print(SET_TEXT_COLOR_WHITE); // Resets text color
    }

    private static void drawHeaders(PrintStream out, String teamColor) {
        out.print("   "); // Prints leading spaces before headers
        String[] headers = getHeadersByColor(teamColor); // Gets headers based on team color
        for (String header : headers) {
            out.print(" " + header + "  "); // Prints each header with spacing
        }
        out.println(); // Moves to the next line
    }

    private static String[] getHeadersByColor(String teamColor) {
        // Returns column headers in order depending on the team color
        return Objects.equals(teamColor, "WHITE") ?
                new String[]{"a", "b", "c", "d", "e", "f", "g", "h"} :
                new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
    }

    private static void drawBoard(PrintStream out, ChessBoard chessBoard, String teamColor) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            int displayRow = teamColor.equalsIgnoreCase("BLACK") ? 7 - row : row; // Determines row order based on team color
            drawRow(out, displayRow, chessBoard); // Draws each row
        }
    }

    private static void drawRow(PrintStream out, int row, ChessBoard chessBoard) {
        out.print(" " + (8 - row) + " "); // Prints row number
        for (int col = 0; col < BOARD_SIZE; col++) {
            setSquareColor(out, row, col); // Sets square color based on position
            ChessPiece piece = chessBoard.getPiece(new ChessPosition(row + 1, col + 1)); // Gets piece at position
            out.print(" " + (piece != null ? getPieceSymbol(piece) : " ") + " "); // Prints piece symbol or empty space
            out.print(SET_BG_COLOR_BLACK); // Resets background color
        }
        out.print(" " + (8 - row) + " "); // Prints row number again
        out.println(); // Moves to the next line
    }

    private static void setSquareColor(PrintStream out, int row, int col) {
        // Sets alternating square colors for the chessboard
        out.print((row + col) % 2 == 0 ? SET_BG_COLOR_WHITE : SET_BG_COLOR_DARK_GREY);
    }

    private static String getPieceSymbol(ChessPiece piece) {
        // Maps chess piece types to symbols
        String symbol;
        switch (piece.getPieceType()) {
            case KING -> symbol = "K";
            case QUEEN -> symbol = "Q";
            case ROOK -> symbol = "R";
            case BISHOP -> symbol = "B";
            case KNIGHT -> symbol = "N";
            case PAWN -> symbol = "P";
            default -> symbol = "?";
        }
        // Converts black pieces to lowercase for differentiation
        return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? symbol : symbol.toLowerCase();

    }
}
