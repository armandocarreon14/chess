package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class Connection {
    public String user;
    public Session session;
    public int gameID;

    public Connection(String user, int gameID, Session session) {
        this.user = user;
        this.session = session;
        this.gameID = gameID;
    }

    public void send(ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }

}