package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    final private HashMap<Integer, GameData> gameDataCollection = new HashMap<>();

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return gameDataCollection.values();
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        gameDataCollection.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return gameDataCollection.get(gameID);
    }



    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        gameDataCollection.put(gameData.gameID(), gameData);
    }

    @Override
    public void clear() throws DataAccessException {
        gameDataCollection.clear();
    }
}
