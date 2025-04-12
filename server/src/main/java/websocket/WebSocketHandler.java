package websocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;

import dataaccess.DataAccessException;
import dataaccess.SQLAuth;
import dataaccess.SQLGame;
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

import javax.management.Notification;
import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    SQLGame games;


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        String user = getUser(command.getAuthToken());

        try {
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, user, command.getGameID());
                case MAKE_MOVE -> makeMove();
                case LEAVE -> leave(user, command.getGameID());
                case RESIGN -> resign(user, command);
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

    private void connect(Session session, String user, int gameID) throws IOException {
        connections.add(user, session);
        var message = String.format("%s has joined", user);
        var notification = new NotificationMessage(message);
        LoadGameMessage loadGameMessage = new LoadGameMessage(message);
        sendMessage(session, loadGameMessage);
        connections.broadcast(gameID, user, notification);
        connections.add(user, session);

    }

    private void makeMove () {
    }


    private void leave (String user, int gameID) throws IOException {
        connections.remove(user);
        var message = String.format("%s has left", user);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, user, notification);
    }

    private void resign(String user, UserGameCommand command) throws DataAccessException, InvalidMoveException, IOException {
        GameData gameData = games.getGame(command.getGameID());

        boolean isWhite = user.equals(gameData.whiteUsername());
        boolean isBlack = user.equals(gameData.blackUsername());

        if (!isWhite && !isBlack) {
            throw new InvalidMoveException("Error: you are an observer");
        }


        games.updateGame(gameData);

        String message = String.format("%s has resigned.", user);
        NotificationMessage toOthers = new NotificationMessage(message);
        connections.broadcast(command.getGameID(), user, toOthers);

        NotificationMessage toSelf = new NotificationMessage("You have resigned.");
        connections.send(user, toSelf);


    }


}
