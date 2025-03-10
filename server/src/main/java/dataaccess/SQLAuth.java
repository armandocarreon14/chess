package dataaccess;

import model.AuthData;

import java.util.UUID;

public class SQLAuth implements AuthDAO {
    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void clearAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clearAllAuth() throws DataAccessException {

    }
}
