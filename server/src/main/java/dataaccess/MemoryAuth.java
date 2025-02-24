package dataaccess;

public class MemoryAuth implements AuthDAO{


    @Override
    public String createAuth(String username) throws DataAccessException {
        return "";
    }

    @Override
    public String getAuth(String authToken) throws DataAccessException {
        return "";
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
