package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void clearAuth(String authToken) throws DataAccessException;
    void clearAllAuth() throws DataAccessException;

}
