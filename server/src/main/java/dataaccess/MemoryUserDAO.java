package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    final private HashMap<String, UserData> userDataList = new HashMap<>();

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        userDataList.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return userDataList.get(username);
    }



    @Override
    public void clear() throws DataAccessException {
        userDataList.clear();
    }
}
