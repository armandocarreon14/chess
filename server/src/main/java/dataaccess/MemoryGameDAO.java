package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MemoryGameDAO implements GameDAO{

    public List<GameData> gameList = new ArrayList<>();

    @Override
    public void createGame(GameData game) {
        gameList.add(game);
    }

    @Override
    public GameData getGame(int gameID) {
        return gameList.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return gameList;
    }

    @Override
    public void updateGame(int gameID, GameData game) {

    }

    @Override
    public void clearGame(int gameID) {
        gameList.clear();
    }

    @Override
    public void clearAll() {

    }
}
