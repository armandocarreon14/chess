package server;

import dataaccess.*;
import service.GameService;
import service.userService;
import spark.*;

public class Server {

    public int run(int desiredPort) throws DataAccessException {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        UserDAO userDAO = new SQLUser();
        GameDAO gameDAO = new SQLGame();
        AuthDAO authDAO = new SQLAuth();

        userService userService = new userService(userDAO, authDAO, gameDAO);
        UserHandler UserHandler = new UserHandler(userService);
        GameService gameService = new GameService(userDAO, authDAO, gameDAO);
        GameHandler gameHandler = new GameHandler(gameService);

        // Register your endpoints and handle exceptions here.
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
