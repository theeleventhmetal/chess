package service;

import dataaccess.*;
import model.CreateGameRequest;
import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ClearTests {

    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
    UserService userService = new UserService(userDAO, authDAO);

    GameService gameService = new GameService(gameDAO, authDAO);

    @Test
    public void clearTest() throws DataAccessException, SQLException {
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

        gameService.createGame(registerResult.authToken(), request);

        clearService.clear();

        assertTrue(userDAO.getAllUsers().isEmpty());
        assertTrue(gameDAO.listGames().isEmpty());
        assertTrue(authDAO.getAllAuths().isEmpty());
    }
}
