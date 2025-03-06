package requestandresults;

import chess.ChessGame;

public record joinGameRequest(String authToken, ChessGame.TeamColor playerColor, int gameID) {
}
