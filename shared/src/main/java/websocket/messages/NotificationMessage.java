package websocket.messages;

import chess.ChessGame;

public class NotificationMessage extends  ServerMessage{

    private String message;

    public NotificationMessage(String notificationMessage) {
        super(ServerMessageType.NOTIFICATION);
        this.message = notificationMessage;
    }

    public String getGame() {
        return message;
    }
}
