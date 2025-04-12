package websocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import service.userService;
import websocket.commands.*;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static websocket.messages.ServerMessage.ServerMessageType.*;

@WebSocket
public class WebSocketHandler {
    private final userService service;
    private final ConnectionManager connections = new ConnectionManager();
    private SQLGame games;
    private AuthDAO authDAO;
    private final Set<Integer> resignedGameIDs = new HashSet<>(); // Track resigned games

    public WebSocketHandler() throws DataAccessException {
        try {
            this.games = new SQLGame();
            this.service = new userService(new SQLUser(), new SQLAuth(), this.games);
        } catch (Exception e) {
            throw new DataAccessException(401, e.getMessage());
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), command.getGameID(), session);
                case MAKE_MOVE -> makeMove();
                case LEAVE -> leave(command.getAuthToken(), command.getGameID(), session);
                case RESIGN -> resign(command.getAuthToken(), command.getGameID(), session);
            }
        } catch (Exception e) {
            ServerMessage error = new ServerMessage(ERROR, null, null, "Error: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void connect(String authToken, Integer gameID, Session session) throws IOException, SQLException, DataAccessException {
        if (gameID == null || authToken == null) {
            ServerMessage error = new ServerMessage(ERROR, null, null, "Missing gameID or authToken.");
            sendMessage(session, error);
            return;
        }

        GameData gameData = service.getGameByID(gameID);
        if (gameData == null) {
            ServerMessage error = new ServerMessage(ERROR, null, null, "Invalid gameID.");
            sendMessage(session, error);
            return;
        }

        try {
            UserData userData = service.findUserByToken(authToken);
            if (userData == null) {
                ServerMessage error = new ServerMessage(ERROR, null, null, "Invalid authToken.");
                sendMessage(session, error);
                return;
            }
            String username = userData.username();

            String role = "an observer";
            if (username.equals(gameData.whiteUsername())) {
                role = "white";
            } else if (username.equals(gameData.blackUsername())) {
                role = "black";
            }

            connections.add(authToken, gameID, session);

            String notificationMessage = String.format("%s has joined the game as %s", username, role);
            ServerMessage notification = new ServerMessage(NOTIFICATION, null, notificationMessage, null);
            connections.broadcast(gameID, authToken, notification);

            ServerMessage loadGame = new ServerMessage(LOAD_GAME, gameData.game().getBoard(), null, null);
            sendMessage(session, loadGame);
        } catch (Exception e) {
            ServerMessage error = new ServerMessage(ERROR, null, null, "Failed to connect: " + e.getMessage());
            sendMessage(session, error);
        }
    }

    private void makeMove() {
        // TODO: Implement makeMove logic
    }

    private void leave(String authToken, Integer gameID, Session session) throws IOException {
        try {
            if (gameID == null || authToken == null) {
                ServerMessage error = new ServerMessage(ERROR, null, null, "Missing gameID or authToken.");
                sendMessage(session, error);
                return;
            }

            UserData userData = service.findUserByToken(authToken);
            if (userData == null) {
                ServerMessage error = new ServerMessage(ERROR, null, null, "Invalid auth token");
                sendMessage(session, error);
                return;
            }
            String username = userData.username();

            connections.remove(authToken);

            String message = String.format("%s has left", username);
            ServerMessage notification = new ServerMessage(NOTIFICATION, null, message, null);
            System.out.println("Leave: Broadcasting to gameID=" + gameID + ", exclude=" + authToken + ", message=" + message);
            connections.broadcast(gameID, username, notification);
        } catch (Exception e) {
            System.out.println("Leave error for authToken=" + authToken + ": " + e.getMessage());
            ServerMessage error = new ServerMessage(ERROR, null, null, "Error leaving game: " + e.getMessage());
            sendMessage(session, error);
        }
    }

    private void resign(String authToken, Integer gameID, Session session) throws IOException, InvalidMoveException {
        String user = null;
        try {
            if (gameID == null || authToken == null) {
                ServerMessage error = new ServerMessage(ERROR, null, null, "Missing gameID or authToken.");
                sendMessage(session, error);
                return;
            }

            GameData gameData = service.getGameByID(gameID);
            if (gameData == null) {
                ServerMessage error = new ServerMessage(ERROR, null, null, "Game not found for ID: " + gameID);
                sendMessage(session, error);
                return;
            }

            UserData userData = service.findUserByToken(authToken);
            if (userData == null) {
                ServerMessage error = new ServerMessage(ERROR, null, null, "Invalid auth token");
                sendMessage(session, error);
                return;
            }
            user = userData.username();

            boolean isWhite = user.equals(gameData.whiteUsername());
            boolean isBlack = user.equals(gameData.blackUsername());
            if (!isWhite && !isBlack) {
                throw new InvalidMoveException("Error: you are an observer");
            }

            if (resignedGameIDs.contains(gameID)) {
                ServerMessage error = new ServerMessage(ERROR, null, null, "Error: Game is already over");
                sendMessage(session, error);
                return;
            }

            String message = String.format("%s has resigned.", user);
            ServerMessage toOthers = new ServerMessage(NOTIFICATION, null, message, null);
            System.out.println("Resign: Broadcasting to gameID=" + gameID + ", exclude=" + authToken + ", message=" + message);
            connections.broadcast(gameID, authToken, toOthers);

            ServerMessage toSelf = new ServerMessage(NOTIFICATION, null, "You have resigned.", null);
            System.out.println("Resign: Sending to self, authToken=" + authToken);
            sendMessage(session, toSelf);

            GameData updatedGame = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game()
            );
            games.updateGame(updatedGame);

            resignedGameIDs.add(gameID);
        } catch (Exception e) {
            System.out.println("Resign error for user=" + user + ": " + e.getMessage());
            ServerMessage error = new ServerMessage(ERROR, null, null, "Error processing resignation: " + e.getMessage());
            sendMessage(session, error);
        }
    }
}