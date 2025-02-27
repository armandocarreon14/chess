package service;

import model.AuthData;
import model.UserData;
import dataaccess.*;
import java.util.UUID;
import passoff.exception.ResponseParseException;

public class UserService {

    private final UserData userData;
    private final AuthData authData;

    public UserService(UserData userData, AuthData authData){
        this.userData = userData;
        this.authData = authData;

    }

    public RegisterResult register(RegisterRequest registerRequest, UserDAO user) {

        if(registerRequest.username() ==null || registerRequest.password() == null || registerRequest. email() == null){
            throw new RuntimeException();
        }

        //taken
        UserData currentUser = user.getUser(registerRequest.username());
        if (currentUser != null){
            throw new RuntimeException();
        }

        //create user
        UserData newUser = new UserData(registerRequest.email(),registerRequest.password(), registerRequest.username());
        user.createUser(newUser);
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, registerRequest.username());
        return new RegisterResult(registerRequest.username(), authData.authToken());

    }
    public LoginResult login(LoginRequest loginRequest) {
        return null;
    }


    public void logout(LogoutRequest logoutRequest) {}
}