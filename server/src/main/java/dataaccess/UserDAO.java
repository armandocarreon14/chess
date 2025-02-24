package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData newUser);
    model.UserData getUser(int user);
    void clearAll();

}
