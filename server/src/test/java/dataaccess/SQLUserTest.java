package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


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
    }

}

