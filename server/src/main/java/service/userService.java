package service;

import requestandresults.*;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class userService {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    public userService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public registerResult register(registerRequest registerRequest) throws DataAccessException{
        if (registerRequest.username() == null || registerRequest.password() == null) {
            throw new DataAccessException(400 , "Error: bad request");
        }

        UserData userData = userDAO.getUser(registerRequest.username());
        if(userData != null){
            throw new DataAccessException(403, "Error: User already taken");
        }

        try {
            UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            userDAO.createUser(newUser);
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(authToken, registerRequest.username());
            authDAO.createAuth(authData);
            return new registerResult(registerRequest.username(), authToken);
        }
        catch (DataAccessException e){
            throw new DataAccessException(500, "Error: " +e.getMessage());
        }
    }

    public loginResult login(loginRequest loginRequest) throws DataAccessException{
        if (loginRequest.username() == null) {
            throw new DataAccessException(500, "Error: bad request");
        }

        UserData userData = userDAO.getUser(loginRequest.username());
        if (userData == null || !userData.password().equals(loginRequest.password())){
            throw new DataAccessException(401, "Error: unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(authToken, loginRequest.username()));
        return new loginResult(loginRequest.username(), authToken);
    }

    public void logout(logoutRequest logoutRequest) throws DataAccessException {
        AuthData authData = authDAO.getAuth(logoutRequest.authToken());

        if (authData == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }

        authDAO.clearAuth(logoutRequest.authToken());
    }


    public void clear() throws DataAccessException {
        authDAO.clearAllAuth();
        userDAO.clear();
        gameDAO.clear();
    }


}
