package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

public class UserService {

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {

        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null){
            throw new DataAccessException("Error");
        }

        //check for existing users

        // create user
        //validate request data
        //create a new user
        //generate an authentication token
        //storing authentication data
        //returning a register result

    }

    public LoginResult login(LoginRequest loginRequest) {

    }

    public void logout(LogoutRequest logoutRequest) {

    }

}