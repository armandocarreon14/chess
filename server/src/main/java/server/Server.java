package server;

import dataaccess.*;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService userService = new UserService(userDAO, authDAO, gameDAO);
        UserHandler UserHandler = new UserHandler(userService);
        GameService gameService = new GameService(userDAO, authDAO, gameDAO);
        GameHandler gameHandler = new GameHandler(gameService);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", UserHandler::RegisterHandler);
        Spark.delete("/db", UserHandler::clearHandler);
        Spark.post("/session", UserHandler::LoginHandler);
        Spark.delete("/session", UserHandler::LogoutHandler);
        Spark.post("/game", gameHandler::CreateGameHandler);
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
