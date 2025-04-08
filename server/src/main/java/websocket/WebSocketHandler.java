package websocket;

import com.google.gson.Gson;

import dataaccess.DataAccessException;
import dataaccess.SQLAuth;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        String user = getUser(command.getAuthToken());

        try {
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, user);
                case MAKE_MOVE -> makeMove();
                case LEAVE -> leave(session, user);
                case RESIGN -> resign();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getUser(String authToken) throws DataAccessException {
        SQLAuth sqlAuth = new SQLAuth();
        AuthData user = sqlAuth.getAuth(authToken);
        return user.username();
    }

    private void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void connect(Session session, String user) throws IOException {
        connections.add(user, session);
        var message = String.format("%s has joined", user);
        var notification = new NotificationMessage(message);
        LoadGameMessage loadGameMessage = new LoadGameMessage(message);
        sendMessage(session, loadGameMessage);
        connections.broadcast(user, notification);
    }


    private void makeMove () {
    }


    private void leave (Session session, String user) throws IOException {
        connections.remove(user);
        var message = String.format("%s has left", user);
        var notification = new NotificationMessage(message);
        connections.broadcast(user, notification);
    }

    private void resign () {
    }

}
