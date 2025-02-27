package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.List;

public class MemoryAuthDAO implements AuthDAO{

    public List<AuthData> memoryAuthList = new ArrayList<>();


    @Override
    public void createAuth(AuthData authData) {
        memoryAuthList.add(authData);
    }

    @Override
    public AuthData getAuth(int index) {
        return memoryAuthList.get(index);
    }

    @Override
    public void deleteAuth(AuthData authData){
        memoryAuthList.remove(authData);
    }

    @Override
    public void clear() {
        memoryAuthList.clear();
    }
}
