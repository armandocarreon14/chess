package dataaccess;

import chess.ChessGame;
import model.GameData;
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
        SQLUser sqlUser = new SQLUser();
        UserData userData = new UserData("username", "passweord", "email");
        sqlUser.createUser(userData);

        UserData user = sqlUser.getUser("username");
        assertEquals("username", user.username());
    }

    @Test
    public void createUserInvalid() throws DataAccessException {
        SQLUser sqlUser = new SQLUser();
        UserData userData = new UserData("username", "passweord", "email");

        sqlUser.createUser(userData);
        assertThrows(DataAccessException.class, () -> {
            sqlUser.createUser(userData);
        });
    }

    @Test
    public void getUserValid() throws DataAccessException {
        SQLUser sqlUser = new SQLUser();
        UserData userData = new UserData("username", "passweord", "email");
        sqlUser.createUser(userData);

        UserData user = sqlUser.getUser("username");
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
        SQLUser sqlUser = new SQLUser();
        UserData userData = new UserData("username", "passweord", "email");
        sqlUser.createUser(userData);

        sqlUser.clear();
        UserData user = sqlUser.getUser("username");
        assertNull(user);
    }


}

