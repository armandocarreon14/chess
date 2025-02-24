package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.List;

public class MemoryUser implements UserDAO{

    public List<UserData> userList = new ArrayList<>();

    @Override
    public void createUser(UserData newUser) {
        userList.add(newUser);
    }

    @Override
    public UserData getUser(int user) {
        return userList.get(user);
    }

    @Override
    public void clearAll() {
        userList.clear();
    }
}

