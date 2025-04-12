package service;

import model.GameData;
import org.mindrot.jbcrypt.BCrypt;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import requestandresults.*;
import requestandresults.RegisterResult;

import java.sql.SQLException;
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

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException{
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
            return new RegisterResult(registerRequest.username(), authToken);
        }
        catch (DataAccessException e){
            throw new DataAccessException(500, "Error: " +e.getMessage());
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        if (loginRequest.username() == null) {
            throw new DataAccessException(500, "Error: bad request");
        }

        UserData userData = userDAO.getUser(loginRequest.username());
        //if (userData == null || !userData.password().equals(loginRequest.password())){
        if (userData == null || !BCrypt.checkpw(loginRequest.password(), userData.password())){
            throw new DataAccessException(401, "Error: unauthorized");
        }


        String authToken = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(authToken, loginRequest.username()));
        return new LoginResult(loginRequest.username(), authToken);
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        AuthData authData = authDAO.getAuth(logoutRequest.authToken());

        if (authData == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }

        authDAO.clearAuth(logoutRequest.authToken());
    }

    public GameData getGameByID(int id) throws DataAccessException {
        for (GameData gameData : gameDAO.listGames()) {
            if (gameData.gameID() == id){
                return gameData;
            }
        }
        return null;
    }

    public UserData findUserByToken(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();
        return userDAO.getUser(username);
    }

    public void clear() throws DataAccessException {
        authDAO.clearAllAuth();
        userDAO.clear();
        gameDAO.clear();
    }


}
