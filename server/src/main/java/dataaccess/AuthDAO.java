package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth (AuthData authData);
    AuthData getAuth(int index);
    void deleteAuth(AuthData authData) throws DataAccessException;
    void clear();

}
