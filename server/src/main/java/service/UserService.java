package service;

import RequestsAndResults.*;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {

    UserDAO userDAO;
    AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException{
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
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

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new DataAccessException(500, "Error: Bad request (missing username or password)");
        }

        UserData userData = userDAO.getUser(loginRequest.username());
        if (userData == null || !userData.password().equals(loginRequest.password())) {
            throw new DataAccessException(401, "Error: unauthorized");
        }

        try {
            String authToken = UUID.randomUUID().toString();
            authDAO.createAuth(new AuthData(authToken, loginRequest.username()));
            return new LoginResult(loginRequest.username(), authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException(500, "Error: " + e.getMessage());
        }
    }


    public void logout(LogoutRequest logoutRequest) throws DataAccessException{
        authDAO.getAuth(logoutRequest.authToken());
        authDAO.clearAuth(logoutRequest.authToken());
    }

    public void clear() throws DataAccessException {
        authDAO.clearAllAuth();
        userDAO.clear();
    }


}
