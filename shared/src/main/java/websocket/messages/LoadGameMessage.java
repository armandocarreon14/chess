package websocket.messages;

import chess.ChessGame;
import model.GameData;

public class LoadGameMessage extends  ServerMessage{

    private ChessGame game;
    private String message;

    public LoadGameMessage(String message) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.message = message;
    }

    public ChessGame getGame() {
        return game;
    }

}
