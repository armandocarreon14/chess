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

import requestandresults.RegisterResult;
import requestandresults.*;

public class ServerFacade {

    private final String serverUrl;
    static String authToken = null;

    public ServerFacade(String url) { serverUrl = url;}

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }
            http.setDoOutput(true);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
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
            throw new ResponseException(status, "failure: " + status);
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
        return this.makeRequest("POST", path, request, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws Exception {
        var path = "/session";
        return this.makeRequest("POST", path, request, LoginResult.class);
    }

    public Object logout(String authToken) throws Exception {
        var path = "/session";
        return this.makeRequest("DELETE", path, null, null);
    }
    public ListGamesResult listGames(ListGamesRequest request) throws Exception {
        var path = "/game";
        return this.makeRequest("GET", path, request, ListGamesResult.class);
    }

    public void join(String authToken, int gameID, ChessGame.TeamColor playerColor) throws Exception {
        this.makeRequest("PUT", "/game",
                new JoinGameRequest(authToken, playerColor, gameID), null);
    }

    public void clear() throws Exception {
        var path = "/db";
        makeRequest("DELETE", path, null, null);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws Exception {
        var path = "/game";
        return this.makeRequest("POST", path, request, CreateGameResult.class);
    }

}
