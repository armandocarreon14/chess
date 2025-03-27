package ui;

import chess.*;

public class CreateBoard {

    public static void showBoard(ChessGame game, ChessGame.TeamColor playerColor) {

        ChessBoard board = game.getBoard();
        boolean isWhitePerspective = (playerColor == ChessGame.TeamColor.WHITE);

        int startRow = isWhitePerspective ? 8 : 1;
        int endRow = isWhitePerspective ? 0 : 9;
        int step = isWhitePerspective ? -1 : 1;
        char[] columns = isWhitePerspective ? "abcdefgh".toCharArray() : "hgfedcba".toCharArray();

        printColumnLabels(columns);

        for (int row = startRow; row != endRow; row += step) {
            System.out.printf(" %2d ", row);
            for (int colIndex = 0; colIndex < 8; colIndex++) {
                int col = columns[colIndex] - 'a' + 1;
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                boolean isLightSquare = (row + col) % 2 == 0;
                String bgColor = isLightSquare ? EscapeSequences.SET_BG_COLOR_DARK_GREY : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

                System.out.print(bgColor + getPieceSymbol(piece) + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.printf(" %2d\n", row);
        }

        printColumnLabels(columns);
    }

    private static void printColumnLabels(char[] columns) {
        System.out.print("    ");
        for (char col : columns) {
            System.out.print(" " + col + " ");
        }
        System.out.println();
    }

    private static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) return EscapeSequences.EMPTY;

        return switch (piece.getPieceType()) {
            case KING   -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_KING : EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_KING;
            case QUEEN  -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_QUEEN : EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_QUEEN;
            case ROOK   -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_ROOK : EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_ROOK;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_BISHOP : EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_KNIGHT : EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_KNIGHT;
            case PAWN   -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.WHITE_PAWN : EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_PAWN;
        };
    }
}
