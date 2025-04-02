package websocket.messages;

import chess.ChessGame;

public class NotificationMessage extends  ServerMessage{

    private String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getGame() {
        return message;
    }
}
