package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData game);
    GameData getGame(int gameID);
    Collection<GameData> listGames();
    void updateGame(int gameID, GameData game);
    void clearGame(int gameID);
    void clearAll();


}
