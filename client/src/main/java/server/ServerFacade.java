package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    public String authToken = null;


    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void clear() throws DataAccessException{
        var request = buildRequest("DELETE", "/db", null);
        sendRequest(request);
    }

    public RegisterResult register(RegisterRequest regRequest) throws DataAccessException{
        var request = buildRequest("POST", "/user", regRequest);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        var request = buildRequest("POST", "/session", loginRequest);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout() throws DataAccessException{
        var request = buildRequest("DELETE", "/session", null);
        sendRequest(request);
    }

    public ListGameResult listGames() throws DataAccessException{
        var request = buildRequest("GET", "/game", null);
        var response = sendRequest(request);
        return handleResponse(response, ListGameResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest createRequest) throws DataAccessException{
        var request = buildRequest("POST", "/game", createRequest);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest joinRequest) throws DataAccessException{
        var request = buildRequest("PUT", "/game", joinRequest);
        sendRequest(request);
    }


    private HttpRequest buildRequest(String method, String path, Object body){
        var request = HttpRequest.newBuilder()
            .uri(URI.create(serverUrl + path))
            .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null){
            request.header("authorization", authToken);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw  new DataAccessException(body);
            }

            throw new DataAccessException("other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
