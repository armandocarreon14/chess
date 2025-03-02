package server;

import dataaccess.*;
import service.UserService;
import spark.*;
import server.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        UserDAO userDAO = new MemoryUserDAO();
        //GameDAO gameDAO = new MemoryGameDAO();        //not yet
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService userService = new UserService(userDAO, authDAO);
        ServerHandler handler = new ServerHandler(userService);     //game service and auth service here

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", handler::RegisterHandler);
        Spark.post("/db", handler::clearHandler);
        Spark.post("/session", handler::LoginHandler);


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
