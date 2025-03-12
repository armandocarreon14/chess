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
        SQLGame sqlGame = new SQLGame();
        GameData gameData = new GameData(1, "White Username", "Black Username", "Game Name", new ChessGame());

        sqlGame.createGame(gameData);

        GameData game = sqlGame.getGame(1);
        assertNotNull(game);
        assertEquals("White Username", game.whiteUsername());
    }

    @Test
    public void createGameInvalid() throws DataAccessException {
        SQLGame sqlGame = new SQLGame();
        GameData gameData = new GameData(1, null, "Black Username", "Game Name", new ChessGame());

        sqlGame.createGame(gameData);
        assertThrows(DataAccessException.class, () -> {
            sqlGame.createGame(gameData);
        });
    }

    @Test
    public void listGameValid() throws DataAccessException {
        SQLGame sqlGame = new SQLGame();
        sqlGame.createGame(new GameData(1, "White Username", "Black Username", "Game Name", new ChessGame()));
        Collection<GameData> gamesList = sqlGame.listGames();

        assertEquals(1, gamesList.size());
    }

    @Test
    public void listGameInvalid() throws DataAccessException {
        SQLGame sqlGame = new SQLGame();
        Collection<GameData> games = sqlGame.listGames();

        assertEquals(0, games.size());
    }

    @Test
    public void getGameValid() throws DataAccessException {
        SQLGame sqlGame = new SQLGame();
        sqlGame.createGame(new GameData(1, "White Username", "Black Username", "Game Name", new ChessGame()));

        GameData game = sqlGame.getGame(1);
        assertEquals(1, game.gameID());
    }

    @Test
    public void getGameInvalid() throws DataAccessException {
        SQLGame sqlGame = new SQLGame();

        GameData game = sqlGame.getGame(1);
        assertNull(game);
    }

    @Test
    public void updateGameValid() throws DataAccessException {
        SQLGame sqlGame = new SQLGame();
        sqlGame.createGame(new GameData(1, "White Username", "Black Username", "Game Name", new ChessGame()));

        GameData newGameData = new GameData(1, "White Username 2", "Black Username 2", "Game Name 2", new ChessGame());

        sqlGame.updateGame(newGameData);

        GameData game = sqlGame.getGame(1);
        assertEquals("White Username 2", game.whiteUsername());
        assertEquals("Black Username 2", game.blackUsername());

    }

    @Test
    public void updateGameInvalid() throws DataAccessException {
        SQLGame sqlGame = new SQLGame();
        GameData newGameData = new GameData(1, "White Username", "Black Username", "Game Name", new ChessGame());

        assertThrows(DataAccessException.class, () -> sqlGame.updateGame(newGameData));
    }

    @Test
    public void clearValid() throws DataAccessException {
        SQLGame sqlGame = new SQLGame();
        GameData gameData = new GameData(1, "White Username", "Black Username", "Game Name", new ChessGame());
        sqlGame.createGame(gameData);
        sqlGame.clear();
        GameData game = sqlGame.getGame(1);
        assertNull(game);
    }




}
