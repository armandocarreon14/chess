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
        GameDAO gameDAO = new MemoryGameDAO();        //not yet
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService userService = new UserService(userDAO, authDAO);
        UserHandler handler = new UserHandler(userService);     //game service and auth service here
        GameService gameService = new GameService(userDAO, authDAO, gameDAO);
        GameHandler gameHandler = new GameHandler(gameService);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", handler::RegisterHandler);
        Spark.delete("/db", handler::clearHandler);
        Spark.post("/session", handler::LoginHandler);
        Spark.delete("/session", handler::LogoutHandler);
        Spark.post("/game", gameHandler::CreateGameHandler);


        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
