package websocket;

import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import service.userService;
import websocket.commands.*;
import websocket.messages.ServerMessage;
import chess.ChessGame;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static websocket.messages.ServerMessage.ServerMessageType.*;

@WebSocket
public class WebSocketHandler {
    private final userService service;
    private final ConnectionManager connections = new ConnectionManager();
    private SQLGame games;
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
                case MAKE_MOVE -> makeMove(command.getAuthToken(), command.getGameID(), command.getMove(), session);
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

    private void connect(String authToken, Integer gameID, Session session) throws IOException, DataAccessException {
        GameData gameData = service.getGameByID(gameID);
        if (gameData == null) {
            ServerMessage error = new ServerMessage(ERROR, null, null, "Invalid gameID.");
            sendMessage(session, error);
            return;
        }
        try {
            UserData userData = service.findUserByToken(authToken);

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

    private void makeMove(String authToken, Integer gameID, ChessMove move, Session session) throws IOException {
        try {

            GameData gameData = service.getGameByID(gameID);
            UserData userData = service.findUserByToken(authToken);

            String username = userData.username();
            ChessGame game = gameData.game();

            //Cannot move after resign
            if (resignedGameIDs.contains(gameID)) {
                sendMessage(session, new ServerMessage(ERROR, null, null, "Game is already over."));
                return;
            }

            //Declare color
            boolean isWhite = username.equals(gameData.whiteUsername());
            ChessGame.TeamColor color = isWhite ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            //Make move for opponent
            if (game.getTeamTurn() != color) {
                sendMessage(session, new ServerMessage(ERROR, null, null, "Error: Not your turn"));
                return;
            }

            //Make invalid move
            try {
                game.makeMove(move);
            } catch (InvalidMoveException e) {
                sendMessage(session, new ServerMessage(ERROR, null, null, "Error: Invalid move"));
                return;
            }

            games.updateGame(new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));

            //Normal Make Move
            ServerMessage loadGame = new ServerMessage(LOAD_GAME, game.getBoard(), null, null);
            sendMessage(session, loadGame);
            connections.broadcast(gameID, authToken, loadGame);

            //Message
            String moveText = username + " moved from " + move.getStartPosition() + " to " + move.getEndPosition();
            ServerMessage moveNotification = new ServerMessage(NOTIFICATION, null, moveText, null);
            connections.broadcast(gameID, authToken, moveNotification);

        } catch (Exception e) {
            sendMessage(session, new ServerMessage(ERROR, null, null, "Invalid move: " + e.getMessage()));
        }
    }

    private void leave(String authToken, Integer gameID, Session session) throws IOException {
        try {
            UserData userData = service.findUserByToken(authToken);
            String username = userData.username();
            GameData gameData = service.getGameByID(gameID);

            // Declare variables for updating data
            String newWhiteUsername = username.equals(gameData.whiteUsername()) ? null : gameData.whiteUsername();
            String newBlackUsername = username.equals(gameData.blackUsername()) ? null : gameData.blackUsername();

            // Update GameData
            GameData updatedGame = new GameData(
                    gameData.gameID(),
                    newWhiteUsername,
                    newBlackUsername,
                    gameData.gameName(),
                    gameData.game()
            );
            games.updateGame(updatedGame);

            // Leave game and notify others
            connections.remove(authToken);
            String message = String.format("%s has left", username);
            ServerMessage notification = new ServerMessage(NOTIFICATION, null, message, null);
            connections.broadcast(gameID, authToken, notification);
        } catch (Exception e) {
            ServerMessage error = new ServerMessage(ERROR, null, null, "Error leaving game: " + e.getMessage());
            sendMessage(session, error);
        }
    }

    private void resign(String authToken, Integer gameID, Session session) throws IOException {
        try {

            GameData gameData = service.getGameByID(gameID);
            UserData userData = service.findUserByToken(authToken);
            String user = userData.username();

            //Observer resign
            if (!user.equals(gameData.whiteUsername()) && !user.equals(gameData.blackUsername())) {
                sendMessage(session, new ServerMessage(ERROR, null, null, "Error: you are an observer"));
                return;
            }
            //Double resign (attempts to resign after other player resigns)
            if (resignedGameIDs.contains(gameID)) {
                sendMessage(session, new ServerMessage(ERROR, null, null, "Error: Game is already over"));
                return;
            }
            //Normal resign
            connections.broadcast(gameID, authToken, new ServerMessage(NOTIFICATION, null, user + " has resigned.", null));
            sendMessage(session, new ServerMessage(NOTIFICATION, null, "You have resigned.", null));

            //Cannot move after resign
            games.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game()));
            resignedGameIDs.add(gameID);
        } catch (Exception e) {
            sendMessage(session, new ServerMessage(ERROR, null, null, "Error processing resignation: " + e.getMessage()));
        }
    }
}