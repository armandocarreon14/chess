package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private final Map<String, Session> userSessions = new ConcurrentHashMap<>(); //usernames are stored here

    public void add(String authToken, int gameID, Session session) {
        var connection = new Connection(authToken, gameID, session);
        connections.put(authToken, connection);
    }

    public void remove(String user) {
        connections.remove(user);
    }

    public void broadcast(int GameID, String excluseUser, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.user.equals(excluseUser)) {
                    c.send(notification);
                }
            } else {
                removeList.add(c);
            }
        }
        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.user);
        }
    }

    public void send(String username, ServerMessage message) throws IOException {
        Session session = userSessions.get(username);
        if (session != null && session.isOpen()) {
            session.getRemote().sendString(new Gson().toJson(message));
        }
    }
}