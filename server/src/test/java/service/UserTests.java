package service;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    UserDAO userDAO = new MemoryUserDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();

    UserService userService = new UserService(userDAO, authDAO);

    GameService gameService = new GameService(gameDAO, authDAO);

    ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);


    @Test
    public void successfulRegister() throws  DataAccessException{
        String testUser = "TESTUSER";
        String testPassword = "TESTPASSWORD";
        String testEmail = "TESTEMAIL@GMAIL";


        RegisterRequest request = new RegisterRequest(testUser, testPassword, testEmail);

        RegisterResult result = userService.register(request);

        assertEquals(testUser, result.username());
        assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    public void invalidRegister(){
        String testUser = null;
        String testPassword = "TESTPASSWORD";
        String testEmail = "TESTEMAIL@GMAIL";

        RegisterRequest request = new RegisterRequest(testUser, testPassword, testEmail);

        assertThrows(BadRequestException.class, () -> userService.register(request));
    }

    @Test
    public void successfulLogin() throws DataAccessException {
        String testUser = "TESTUSER";
        String testPassword = "TESTPASSWORD";
        String testEmail = "TESTEMAIL@GMAIL";

        RegisterRequest registerRequest = new RegisterRequest(testUser, testPassword, testEmail);
        userService.register(registerRequest);

       LoginRequest request = new LoginRequest(testUser, testPassword);

       LoginResult result = userService.login(request);

        assertEquals(testUser, result.username());
        assertNotNull(authDAO.getAuth(result.authToken()));

    }

    @Test
    public void unsuccessfulLogin(){
        String testUser = "TESTUSER";
        String testPassword = null;

        LoginRequest request = new LoginRequest(testUser, testPassword);

        assertThrows(BadRequestException.class, () -> userService.login(request));

    }

    @Test
    public void successfulLogout() throws DataAccessException{
        String testUser = "TESTUSER1";
        String testPassword = "TESTPASSWORD";
        String testEmail = "TESTEMAIL@GMAIL";

        RegisterRequest registerRequest = new RegisterRequest(testUser, testPassword, testEmail);
        RegisterResult result = userService.register(registerRequest);

        userService.logout(result.authToken());

        assertNull(authDAO.getAuth(result.authToken()));
    }


    @Test
    public void unsuccessfulLogout(){
        String authToken = UUID.randomUUID().toString();
        assertThrows(UnauthorizedException.class, () -> userService.logout(authToken));
    }
}
