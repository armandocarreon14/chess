package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class SQLUserTest {

    private SQLUser userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new SQLUser();
        userDAO.clear();
    }

    @Test
    public void createUserValid() throws DataAccessException {
        UserData userData = new UserData("username", "passweord", "email");
        userDAO.createUser(userData);

        UserData user = userDAO.getUser("username");
        assertEquals("username", user.username());
    }

    @Test
    public void createUserInvalid() throws DataAccessException {
        UserData userData = new UserData("username", "passweord", "email");
        userDAO.createUser(userData);
        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(userData);
        });
    }

    @Test
    public void getUserValid() throws DataAccessException {
        UserData userData = new UserData("username", "passweord", "email");
        userDAO.createUser(userData);

        UserData user = userDAO.getUser("username");
        assertEquals("username", user.username());
    }

    @Test
    public void getUserInvalid() throws DataAccessException {
        SQLUser sqlUser = new SQLUser();

        UserData user = sqlUser.getUser("username");
        assertNull(user);
    }

    @Test
    public void clearValid() throws DataAccessException {
        UserData userData = new UserData("username", "passweord", "email");
        userDAO.createUser(userData);

        userDAO.clear();
        UserData user = userDAO.getUser("username");
        assertNull(user);
    }

}

