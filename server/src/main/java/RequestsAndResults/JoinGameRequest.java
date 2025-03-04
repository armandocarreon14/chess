package RequestsAndResults;

import chess.ChessGame;

public record JoinGameRequest (String authToken, ChessGame.TeamColor playerColor, int gameID) {
}
