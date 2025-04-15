package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameData;
import requestandresults.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChessClient {

    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private final String serverUrl;
    private final CreateBoard board;
    private String username = null;
    private final String authToken = ServerFacade.authToken;
    private NotificationHandler notificationHandler;
    private WebSocketFacade webSocketFacade;


    public ChessClient(String serverurl) {
        server = new ServerFacade(serverurl);
        this.serverUrl = serverurl;
        this.board = new CreateBoard();
        this.notificationHandler = notificationHandler;
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
                case "creategame" -> create(params);
                case "listgames" -> list();
                case "joingame" -> join(params);
                case "logout" -> logout();
                case "observegame" -> observe(params);
                case "redraw" -> redraw();
                case "move" -> movePiece(params);
                case "highlight" -> highlight(params);
                case "resign" -> confirmation();
                case "yes" -> resign();
                case "no" -> "good choice";
                case "leave" -> leave();

                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String redraw() {
        return "";
    }


    public String movePiece(String... params) throws ResponseException {
        return "";
    }

    public String highlight(String... params) {
        return "";
    }

    public String confirmation() {
        return "Are you sure you want to resign? Yes/No";
    }

    public String resign() throws ResponseException {
        webSocketFacade.resign();
        return "Thanks for playing!";
    }

    public String leave() throws ResponseException {
        webSocketFacade.leave();
        return "You have left the game";
    }

    public String register(String... params)  {
        try {
            if (params.length == 3) {
                RegisterResult registerResult = server.register(new RegisterRequest(params[0], params[1], params[2]));
                String username = registerResult.username();
                // log in after registration
                server.login(new LoginRequest(params[0], params[1]));

                // Update state to signed in
                state = State.SIGNEDIN;
                this.username = username;

                return String.format("You registered and logged in as %s.", username);
            }
        } catch (Exception e) {
            return "Register error exception: username is taken";
        }
        return "Register error";
    }

    public String login(String... params) {
        try {
            if (params.length == 2) {
                state = State.SIGNEDIN;
                LoginResult loginResult = server.login(new LoginRequest(params[0], params[1]));
                username = loginResult.username();
                return String.format("\nYou logged in as %s.", username);
            }
        }
        catch (Exception e) {
            return "Incorrect login: Something is wrong with your username or password";
        }
        return "Login error";
    }

    public String create(String... params) throws Exception {
        try {
            assertSignedIn();
            if (params.length != 1) {
                throw new ResponseException(400, "Expected: <GameName>");
            }
            var gameName = params[0];

            CreateGameRequest createGameRequest = new CreateGameRequest(gameName, null);
            server.createGame(createGameRequest);

            return String.format("Game created successfully: %s\n\n%s", gameName, help());
        } catch (Exception e) {
            throw new RuntimeException("Make sure you name the game after using 'creategame'");
        }
    }

    public String list(String... params) throws ResponseException {
        assertSignedIn();

        try {
            ListGamesResult listGamesResult = server.listGames();
            var games = listGamesResult.games();

            if (games.isEmpty()) {
                return "No games available.";
            }

            StringBuilder sb = new StringBuilder("\nAvailable games:\n" +
                    "~ Use: joingame <GAME_NUMBER> <COLOR> to join the game\n" +
                    "~ Use: observegame <GAME_NUMBER> to observe the game\n");

            int index = 1;
            for (var game : games) {
                sb.append(String.format("%d. Name: %s, White: %s, Black: %s\n",
                        index++,
                        //game.gameID(),
                        game.gameName(),
                        game.whiteUsername() != null ? game.whiteUsername() : "Open",
                        game.blackUsername() != null ? game.blackUsername() : "Open"));
            }

            return sb.toString();
        } catch (Exception e) {
            return "Error retrieving game list: " + e.getMessage();
        }
    }

    public String join(String... params) {
        try {
            if (params.length != 2) {
                return "Error: Use 'joingame <Number> <Color>'\nMake sure to use a number that's listed.";
            }

            assertSignedIn();

            // Parse the game index
            int index = Integer.parseInt(params[0]);

            // Retrieve the game list and convert it to a List
            ListGamesResult listGamesResult = server.listGames();
            List<GameData> games = new ArrayList<>(listGamesResult.games());

            // Check if the index is within the valid range
            if (index < 1 || index > games.size()) {
                return "Error: Invalid game number. Choose a valid game from the list.";
            }

            GameData selectedGame = games.get(index - 1);
            int gameID = selectedGame.gameID();

            // Validate color input
            String colorInput = params[1].toUpperCase();
            ChessGame.TeamColor playerColor;
            if ("WHITE".equals(colorInput)) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if ("BLACK".equals(colorInput)) {
                playerColor = ChessGame.TeamColor.BLACK;
            } else {
                return "Error: Use 'WHITE' or 'BLACK'.";
            }

            // Join the game using the extracted game ID
            state = State.GAMEPLAY;
            webSocketFacade = new WebSocketFacade(serverUrl, notificationHandler);
            server.join(gameID, playerColor);
            board.showBoard(new ChessGame(), playerColor);

            return String.format("Joined game %s (Game ID: %d) as the %s player. Use the help command if you need help"
                    , selectedGame.gameName(), gameID, playerColor);

        } catch (NumberFormatException e) {
            return "Error: Game index must be a number.";
        } catch (Exception e) {
            return "Error: That color may already be taken.";
        }
    }


    public String observe(String... params) {
        try {
            assertSignedIn();

            if (params.length != 1) {
                return "Missing value: Use 'observe <Number>'\nMake sure to choose a number from the list.";
            }

            int index = Integer.parseInt(params[0]);

            ListGamesResult listGamesResult = server.listGames();
            List<GameData> games = new ArrayList<>(listGamesResult.games());

            if (index < 1 || index > games.size()) {
                return "Error: Invalid game number. Choose a number from the list.";
            }

            GameData selectedGame = games.get(index - 1);
            state = State.GAMEPLAY;
            webSocketFacade = new WebSocketFacade(serverUrl, notificationHandler);
            board.showBoard(selectedGame.game(), ChessGame.TeamColor.WHITE);

            return String.format("Observing game %d", index);

        } catch (NumberFormatException e) {
            return "Error: Game index must be a number.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }


    public String logout(String... params) {
        try {
            assertSignedIn();
            server.logout();
            state = State.SIGNEDOUT;
            username = null;
            return "Successfully logged out.";
        } catch (Exception e) {
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
        } else if (state == State.GAMEPLAY) {
            return """
                    - redraw
                    - move <startposition> <endposition>
                    - highlight <position>
                    - resign
                    - leave
                    - help
                    """;
        }
        return """
                 - CreateGame <Game_Name>
                 - ListGames
                 - JoinGame <gameID> <playerColor>
                 - ObserveGame <gameID>
                 - Logout (logout)
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
