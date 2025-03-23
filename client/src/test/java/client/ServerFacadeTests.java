package client;

import chess.ChessGame;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import requestandresults.*;
import server.Server;
import ui.ServerFacade;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    static String authToken;

    @BeforeAll
    public static void init() throws Exception {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var sererURL = "http://localhost:" + port;
        facade = new ServerFacade(sererURL);
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }



    @Test
    public void registerValid() throws Exception {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        RegisterResult result = facade.register(request);
        assert result.username().equals("username");
    }

    @Test
    public void registerInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest(null, "password", "email");
        assertThrows(ResponseException.class, () -> facade.register(request));
    }

    @Test
    public void loginValid() throws Exception {
        RegisterRequest regRequest = new RegisterRequest("username", "password", "email");
        facade.register(regRequest);
        LoginRequest request = new LoginRequest("username", "password");
        LoginResult result = facade.login(request);
        assertNotNull(result);
        assertNotNull(result.authToken());
        authToken = result.authToken();
    }

    @Test
    public void loginInvalid() {
        LoginRequest request = new LoginRequest("username", "password");
        assertThrows(ResponseException.class, () -> facade.login(request));
    }

    @Test
    public void createGameValid() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult registerResult = facade.register(registerRequest);
        String auth = registerResult.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest("gameName", auth);
        assertNotNull(createGameRequest);
    }

    @Test
    public void createGameInvalid() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult registerResult = facade.register(registerRequest);
        String auth = registerResult.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest("gameName", auth);
        assertThrows(ResponseException.class, () -> facade.createGame(createGameRequest));
    }

    @Test
    public void listGamesValid() throws Exception {
        if (authToken == null) {
            loginValid();
        }
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult registerResult = facade.register(registerRequest);
        String auth = registerResult.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest("gameName", auth);
        facade.createGame(createGameRequest);
        ListGamesRequest listGamesRequest = new ListGamesRequest(auth);
        var result = facade.listGames(listGamesRequest);
        assertEquals(1, result.games().size());
    }


    @Test
    public void testListGamesValid() throws Exception {
        ListGamesRequest request = new ListGamesRequest(authToken);
        ListGamesResult result = facade.listGames(request);
        assertNotNull(result);
    }

    @Test
    public void listGamesInvalid() {
        ListGamesRequest request = new ListGamesRequest("invalidAuthToken");
        assertThrows(ResponseException.class, () -> facade.listGames(request));
    }

    @Test
    public void joinGameValid() throws Exception {
        String authToken = "validAuthToken";
        int gameID = 1;
        ChessGame.TeamColor playerColor = ChessGame.TeamColor.WHITE;
        facade.join(authToken, gameID, playerColor);
    }


    @Test
    public void joinGameInvalid() throws Exception {
        if (authToken == null) {
            loginValid();
        }
        assertThrows(ResponseException.class, () -> facade.join(authToken, -1, ChessGame.TeamColor.BLACK));
    }

    @Test
    public void clearValid() throws Exception {
        facade.clear(); // Assuming this deletes all data
    }



}
