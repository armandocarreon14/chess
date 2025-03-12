package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLGame  implements  GameDAO{

    public SQLGame() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

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
        `ID` int NOT NULL AUTO_INCREMENT,
        `whiteUsername` varchar(256),
        `blackUsername` varchar(256),
        `name` varchar(256) NOT NULL,
        `game` text NOT NULL,
        PRIMARY KEY (`ID`),
        INDEX(ID)
    )
    """
    };


    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        String statement = "SELECT * FROM games";
        List<GameData> games = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS);
             var rs = ps.executeQuery()) {//result set

            while (rs.next()) {
                int id = rs.getInt("ID");
                String whiteUser = rs.getString("whiteUsername");
                String blackUser = rs.getString("blackUsername");
                String name = rs.getString("name");
                String json = rs.getString("game");

                ChessGame chessGame = new Gson().fromJson(json, ChessGame.class);
                games.add(new GameData(id, whiteUser, blackUser, name, chessGame));
            }

        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }

        return games;
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO games (ID, whiteUsername, blackUsername, name, game) VALUES (?, ?, ?, ?, ?)";
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        var json = gson.toJson(gameData.game());

        var conn = DatabaseManager.getConnection();
        try(var preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, gameData.gameID());
            preparedStatement.setString(2, gameData.whiteUsername());
            preparedStatement.setString(3, gameData.blackUsername());
            preparedStatement.setString(4, gameData.gameName());
            preparedStatement.setString(5, json);

            //preparedStatement.executeUpdate(statement, gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), json);
            preparedStatement.executeUpdate();

        }

        catch (Throwable e) {
            throw new DataAccessException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM games WHERE ID=?";
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
        var statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, name = ?, game = ? WHERE ID = ?";
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        var json = gson.toJson(gameData.game());

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, gameData.whiteUsername());
            preparedStatement.setString(2, gameData.blackUsername());
            preparedStatement.setString(3, gameData.gameName());
            preparedStatement.setString(4, json);
            preparedStatement.setInt(5, gameData.gameID());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataAccessException(500, "Error");
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, e.getMessage());
        }
    }


    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = "TRUNCATE games";
            try (var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
