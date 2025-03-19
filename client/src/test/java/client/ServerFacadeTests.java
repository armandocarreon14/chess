package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import requestandresults.*;
import server.Server;
import ui.ServerFacade;

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
        RegisterRequest regRequest = new RegisterRequest("testUser", "testPass", "test@email.com");
        facade.register(regRequest);
        LoginRequest request = new LoginRequest("testUser", "testPass");
        LoginResult result = facade.login(request);
        assertNotNull(result);
        assertNotNull(result.authToken());
        authToken = result.authToken();
    }

}
