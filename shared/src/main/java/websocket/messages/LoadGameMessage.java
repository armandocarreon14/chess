package websocket.messages;

import chess.ChessGame;
import model.GameData;

public class LoadGameMessage extends  ServerMessage{

    private ChessGame game;

    public LoadGameMessage(String message) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }

}
