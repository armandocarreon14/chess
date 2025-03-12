package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLUser  implements UserDAO{

    public SQLUser() {
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
        CREATE TABLE IF NOT EXISTS `users` (
        `username` varchar(256) NOT NULL,
        `password` varchar(256) NOT NULL,
        `email` varchar(256) NOT NULL,
        PRIMARY KEY (`username`),
        INDEX(username)
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
    public void createUser(UserData userData) throws DataAccessException {
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {

            String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());

            ps.setString(1, userData.username());
            ps.setString(2, hashedPassword);
            ps.setString(3, userData.email());

            executeUpdate(statement, userData.username(), hashedPassword, userData.email());

        } catch (SQLException e) {
            throw new DataAccessException(500, "Database error: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String statement = "SELECT username, password, email FROM users WHERE username = ?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {

            ps.setString(1, username);  // Set the username in the query
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String user = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");

                return new UserData(user, password, email);
            } else {
                return null;  // User not found
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, "Database error: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "DELETE FROM users";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(500, "Database error while clearing users: " + e.getMessage());
        }
    }

}
