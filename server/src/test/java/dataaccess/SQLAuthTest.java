package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthTest {

    private SQLAuth authDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = new SQLAuth();
        authDAO.clearAllAuth();
    }

    @Test
    public void createAuthValid() {
        AuthData authData = new AuthData("authToken", "username");
        authDAO.createAuth(authData);

        AuthData auth = authDAO.createAuth(authData);
        assertEquals("authToken", auth.authToken());
    }

    @Test
    public void createAuthInvalid() throws DataAccessException {
        try {
            AuthData authData = new AuthData(null, "username");
            authDAO.createAuth(authData);
        } catch (RuntimeException e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }

    }

    @Test
    public void getAuthValid() throws DataAccessException {
        AuthData authData = new AuthData("authToken", "username");
        authDAO.createAuth(authData);

        AuthData auth = authDAO.getAuth("authToken");
        assertEquals("authToken", auth.authToken());
    }

    @Test
    public void getAuthInvalid() throws DataAccessException {
        SQLAuth sqlAuth = new SQLAuth();
        AuthData auth = sqlAuth.getAuth(null);
        assertNull(auth);
    }

    @Test
    public void clearValid() throws DataAccessException {
        AuthData authData = new AuthData("authToken", "username");
        authDAO.createAuth(authData);

        authDAO.clearAllAuth();
        AuthData user = authDAO.getAuth(authData.authToken());
        assertNull(user);
    }

}
