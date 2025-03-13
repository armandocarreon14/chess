package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

    final private HashMap<String, AuthData> authMap = new HashMap<>();

    @Override
    public AuthData createAuth(AuthData auth) throws DataAccessException {
        authMap.put(auth.authToken(), auth);
        return auth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authMap.get(authToken);
    }

    @Override
    public void clearAuth(String authToken) throws DataAccessException {
        authMap.remove(authToken);
    }

    @Override
    public void clearAllAuth() throws DataAccessException {
        authMap.clear();
    }
}
