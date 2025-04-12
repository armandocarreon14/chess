package server;

import dataaccess.*;
import service.GameService;
import service.userService;
import spark.*;
import websocket.WebSocketHandler;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        UserDAO userDAO;
        AuthDAO authDAO;
        GameDAO gameDAO;
        WebSocketHandler webSocketHandler;
        try {
            userDAO = new SQLUser();
            gameDAO = new SQLGame();
            authDAO = new SQLAuth();
            webSocketHandler = new WebSocketHandler();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        userService userService = new userService(userDAO, authDAO, gameDAO);
        UserHandler UserHandler = new UserHandler(userService);
        GameService gameService = new GameService(userDAO, authDAO, gameDAO);
        GameHandler gameHandler = new GameHandler(gameService);

        // Register your endpoints and handle exceptions here.
        Spark.webSocket("/ws", webSocketHandler);
        Spark.post("/user", UserHandler::registerHandler);
        Spark.delete("/db", UserHandler::clearHandler);
        Spark.post("/session", UserHandler::loginHandler);
        Spark.delete("/session", UserHandler::logoutHandler);
        Spark.post("/game", gameHandler::createGameHandler);
        Spark.put("/game", gameHandler::joinGameHandler);
        Spark.get("/game", gameHandler::listHandler);


        Spark.awaitInitialization();
        return Spark.port();
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
