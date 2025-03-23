package ui;

import exception.ResponseException;
import requestandresults.*;

import java.util.Arrays;

import static ui.ServerFacade.authToken;

public class ChessClient {

    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private String username = null;


    public ChessClient(ServerFacade server) {
        this.server = server;
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
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) {
        try {
            if (params.length == 3) {
                state = state.SIGNEDIN;
                RegisterResult registerResult = server.register(new RegisterRequest(params[0], params[1], params[2]));
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
                state = state.SIGNEDIN;
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

    public String create(String... params) {
        try {
            if (params.length == 1) {
                state = state.SIGNEDIN;
                CreateGameRequest createGameRequest = new CreateGameRequest("gameName", authToken);
                server.createGame(createGameRequest);
                return String.format("You created the game game %s.", createGameRequest.gameName());
            }
        } catch (Exception e) {
            return "Create Game error: " + e.getMessage();
        }
        return "Create Game error: incorrect usage";
    }

    public String list(String... params) {
        return "";
    }

    public String join(String... params) {
        return "";
    }

    public String logout(String... params) {
        return "";
    }

    public String help(String... params) {
        return "";
    }


}
