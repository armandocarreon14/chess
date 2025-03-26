package ui;

import exception.ResponseException;
import requestandresults.*;

import java.util.Arrays;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class ChessClient {

    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private final String serverUrl;
    private final CreateBoard board;
    private String username = null;


    public ChessClient(String serverurl) {
        server = new ServerFacade(serverurl);
        this.serverUrl = serverurl;
        this.board = new CreateBoard();
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "logout" -> logout();
                case "observe" -> observe(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        try {
            if (params.length == 3) {
                RegisterResult registerResult = server.register(new RegisterRequest(params[0], params[1], params[2]));
                state = State.SIGNEDIN;
                username = registerResult.username();
                return String.format("You registered as %s.", username);
            }
        }
        catch (Exception e) {
            return "Register error exception: " + e.getMessage();
        }
        return "Register error";
    }

    public String login(String... params) {
        try {
            if (params.length == 2) {
                state = State.SIGNEDIN;
                LoginResult loginResult = server.login(new LoginRequest(params[0], params[1]));
                username = loginResult.username();
                return String.format("You logged in as %s.", username);
            }
        }
        catch (Exception e) {
            return "Login error exception: " + e.getMessage();
        }
        return "Login error";
    }

    public String create(String... params) throws Exception {
        assertSignedIn();
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: <game name>");
        }
        var gameName = params[0];

        CreateGameRequest createGameRequest = new CreateGameRequest(gameName, null);
        server.createGame(createGameRequest);

        return String.format(SET_TEXT_COLOR_GREEN + "Game created successfully: %s\n\n%s", gameName,help());
    }

    public String list(String... params) throws ResponseException {
        return "";
    }

    public String join(String... params) throws ResponseException {
        return "";
    }

    public String observe(String... params) throws ResponseException {
        return "";
    }

    /// check logout
    public String logout(String... params) {
        try {
            assertSignedIn();
            server.logout(null);
            state = State.SIGNEDOUT;
            return String.format("%s logged out", username);
        }
        catch (Exception e) {
            return "Logout error: " + e.getMessage();
        }
    }

    public String help(String... params) {
        if (state == State.SIGNEDOUT) {
            return """
                    - Register <username> <password> <email>
                    - Login <username> <password>
                    - Help
                    - Quit
                    """;
        }
        return """
                 - CreateGame <game name>
                 - ListGames
                 - JoinGame <gameID> <playerColor>
                 - ObserveGame <gameID>
                 - Logout <authentication token>
                 - Help
                 - Quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }



}
