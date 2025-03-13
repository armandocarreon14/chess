package dataaccess;
import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameTest {

    private SQLGame gameDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = new SQLGame();
        gameDAO.clear();
    }

    @Test
    public void creteGameValid() throws DataAccessException {
        GameData gameData = new GameData(1, "White Username", "Black Username", "Game Name", new ChessGame());

        gameDAO.createGame(gameData);

        GameData game = gameDAO.getGame(1);
        assertNotNull(game);
        assertEquals("White Username", game.whiteUsername());
    }

    @Test
    public void createGameInvalid() throws DataAccessException {
        GameData gameData = new GameData(1, null, "Black Username", "Game Name", new ChessGame());

        gameDAO.createGame(gameData);
        assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(gameData);
        });
    }

    @Test
    public void listGameValid() throws DataAccessException {
        gameDAO.createGame(new GameData(1, "White Username", "Black Username", "Game Name", new ChessGame()));
        Collection<GameData> gamesList = gameDAO.listGames();

        assertEquals(1, gamesList.size());
    }

    @Test
    public void listGameInvalid() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();

        assertEquals(0, games.size());
    }

    @Test
    public void getGameValid() throws DataAccessException {
        gameDAO.createGame(new GameData(1, "White Username", "Black Username", "Game Name", new ChessGame()));

        GameData game = gameDAO.getGame(1);
        assertEquals(1, game.gameID());
    }

    @Test
    public void getGameInvalid() throws DataAccessException {
        GameData game = gameDAO.getGame(1);
        assertNull(game);
    }

    @Test
    public void updateGameValid() throws DataAccessException {
        gameDAO.createGame(new GameData(1, "White Username", "Black Username", "Game Name", new ChessGame()));

        GameData newGameData = new GameData(1, "White Username 2", "Black Username 2", "Game Name 2", new ChessGame());

        gameDAO.updateGame(newGameData);

        GameData game = gameDAO.getGame(1);
        assertEquals("White Username 2", game.whiteUsername());
        assertEquals("Black Username 2", game.blackUsername());

    }

    @Test
    public void updateGameInvalid() throws DataAccessException {
        GameData newGameData = new GameData(1, "White Username", "Black Username", "Game Name", new ChessGame());

        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(newGameData));
    }

    @Test
    public void clearValid() throws DataAccessException {
        GameData gameData = new GameData(1, "White Username", "Black Username", "Game Name", new ChessGame());
        gameDAO.createGame(gameData);
        gameDAO.clear();
        GameData game = gameDAO.getGame(1);
        assertNull(game);
    }




}
