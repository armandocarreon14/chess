package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{

    final private HashMap<String, UserData> userList = new HashMap<>();

    @Override
    public void createUser(UserData newUser) {
        newUser = new UserData(newUser.username(), newUser.password(), newUser.email());
        userList.put(newUser.username(), newUser);
    }

    @Override
    public UserData getUser(String user) {
        return userList.get(user);
    }

    @Override
    public void clearAll() {
        userList.clear();
    }
}

