package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import requestandresults.*;
import requestandresults.RegisterResult;

public class ServerFacade {

    private final String serverUrl;
    static String authToken = null;

    public ServerFacade(String url) { serverUrl = url;}

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setRequestProperty("Authorization", authToken);
            http.setDoOutput(!method.equals("GET"));
            writeBody(request, http);

            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    String message = ResponseException.fromJson(respErr);
                    throw new ResponseException(status, message);
                }
            }
            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    public RegisterResult register(RegisterRequest request) throws Exception {
        var path = "/user";
        RegisterResult registerResult = this.makeRequest("POST", path, request, RegisterResult.class, null);
        authToken = registerResult.authToken();
        return registerResult;
    }

    public LoginResult login(LoginRequest request) throws Exception {
        var path = "/session";
        LoginResult loginResult = this.makeRequest("POST", path, request, LoginResult.class, null);
        authToken = loginResult.authToken();
        return loginResult;
    }

    public Object logout() throws Exception {
        var path = "/session";
        return this.makeRequest("DELETE", path, null, null, authToken);
    }

    public ListGamesResult listGames() throws Exception {
        var path = "/game";
        return this.makeRequest("GET", path, null, ListGamesResult.class, authToken);
    }

    public void join(int gameID, ChessGame.TeamColor playerColor) throws Exception {
        this.makeRequest("PUT", "/game",
                new JoinGameRequest(authToken, playerColor, gameID), null, authToken);
    }

    public void clear() throws Exception {
        var path = "/db";
        makeRequest("DELETE", path, null, null, null);
    }

    public void createGame(CreateGameRequest request) throws Exception {
        var path = "/game";
        this.makeRequest("POST", path, request, CreateGameResult.class, authToken);
    }

}
