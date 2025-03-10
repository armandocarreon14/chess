package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AuthData;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLGame  implements  GameDAO{

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(500, "Unable to configure database: %s" +ex.getMessage());
        }
    }

    private final String[] createStatements = {
                    """
        CREATE TABLE IF NOT EXISTS games (
        `ID` int NOT NULL,
        `whiteUsername` varchar(256),
        `blackUsername` varchar(256),
        `name` varchar(256) NOT NULL,
        `game` text NOT NULL,
        PRIMARY KEY (`ID`),
        INDEX(ID)
        )
        """
    };

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO games (ID, whiteUsername, blackUsername, name, game) VALUES (?, ?, ?, ?, ?)";
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        var json = gson.toJson(gameData.game());
        try {
            executeUpdate(statement, gameData.game(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), json);
        }
        catch (Throwable e) {
            throw new DataAccessException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM auths WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                var rs = ps.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt("ID");
                    String whiteUser = rs.getString("whiteUsername");
                    String blackUser = rs.getString("blackUsername");
                    String name  = rs.getString("name");
                    var json = rs.getString("game");

                    ChessGame chessGame = new Gson().fromJson(json, ChessGame.class);
                    return new GameData(id, whiteUser, blackUser, name, chessGame);
                }
            }
        }
        catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
