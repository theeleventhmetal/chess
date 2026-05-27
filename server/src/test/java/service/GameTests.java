package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {
    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();

    UserService userService = new UserService(userDAO, authDAO);

    GameService gameService = new GameService(gameDAO, authDAO);

    ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

    @Test
    public void successfulListGames() throws DataAccessException, SQLException {
        String testUser = "TESTUSER";
        String testPassword = "TESTPASSWORD";
        String testEmail = "TESTEMAIL@GMAIL";


        RegisterRequest registerRequest = new RegisterRequest(testUser, testPassword, testEmail);

        RegisterResult registerResult = userService.register(registerRequest);


        String gameName = "TESTGAMENAME";
        CreateGameRequest request = new CreateGameRequest(gameName);

        gameService.createGame(registerResult.authToken(), request);

        ListGameResult result = gameService.listGames(registerResult.authToken());

        assertEquals(1, gameService.listGames(registerResult.authToken()).games().size());
    }

    @Test
    public void unsuccessfulListGames(){
        String authToken = UUID.randomUUID().toString();

        assertThrows(UnauthorizedException.class, () -> gameService.listGames(authToken));
    }

    @Test
    public void successfulCreateGame() throws DataAccessException, SQLException {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
        String testUser = "TESTUSER";
        String testPassword = "TESTPASSWORD";
        String testEmail = "TESTEMAIL@GMAIL";


        RegisterRequest registerRequest = new RegisterRequest(testUser, testPassword, testEmail);

        RegisterResult registerResult = userService.register(registerRequest);


        String gameName = "TESTGAMENAME";
        CreateGameRequest request = new CreateGameRequest(gameName);

        CreateGameResponse result = gameService.createGame(registerResult.authToken(), request);

        assertNotEquals(0, result.gameID());
    }

    @Test
    public void unsuccessfulCreateGame() throws DataAccessException, SQLException {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
        String testUser = "TESTUSER";
        String testPassword = "TESTPASSWORD";
        String testEmail = "TESTEMAIL@GMAIL";


        RegisterRequest registerRequest = new RegisterRequest(testUser, testPassword, testEmail);

        RegisterResult registerResult = userService.register(registerRequest);

        String gameName = null;
        CreateGameRequest request = new CreateGameRequest(gameName);

        assertThrows(UnauthorizedException.class, () -> gameService.createGame(gameName, request));
    }

    @Test
    public void successfulJoinGame() throws DataAccessException, SQLException {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
        String testUser = "TESTUSER";
        String testPassword = "TESTPASSWORD";
        String testEmail = "TESTEMAIL@GMAIL";


        RegisterRequest registerRequest = new RegisterRequest(testUser, testPassword, testEmail);

        RegisterResult registerResult = userService.register(registerRequest);

        String gameName = "TESTGAMENAME";
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
        CreateGameResponse createGameResult = gameService.createGame(registerResult.authToken(), createGameRequest);

        int gameID = createGameResult.gameID();

        String color = "WHITE";


        JoinGameRequest request = new JoinGameRequest(color, gameID);

        gameService.joinGame(registerResult.authToken(), request);

        assertEquals(testUser,gameDAO.getGame(gameID).whiteUsername());
    }

    @Test
    public void unsuccessfulJoinGame() throws DataAccessException, SQLException {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
        String testUser = "TESTUSER";
        String testPassword = "TESTPASSWORD";
        String testEmail = "TESTEMAIL@GMAIL";


        RegisterRequest registerRequest = new RegisterRequest(testUser, testPassword, testEmail);

        RegisterResult registerResult = userService.register(registerRequest);

        String gameName = "TESTGAMENAME";
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
        CreateGameResponse createGameResult = gameService.createGame(registerResult.authToken(), createGameRequest);

        int gameID = createGameResult.gameID();

        String color = "WHITE";

        JoinGameRequest request = new JoinGameRequest(color, gameID);

        gameService.joinGame(registerResult.authToken(), request);

        String testUser1 = "TESTUSER1";
        String testPassword1 = "TESTPASSWORD1";
        String testEmail1 = "TESTEMAIL@GMAIL1";

        RegisterRequest registerRequest1 = new RegisterRequest(testUser1, testPassword1, testEmail1);

        RegisterResult registerResult1 = userService.register(registerRequest1);

        JoinGameRequest request1 = new JoinGameRequest(color, gameID);

        assertThrows(AlreadyTakenException.class, ()-> gameService.joinGame(registerResult1.authToken(), request1));
    }
}
