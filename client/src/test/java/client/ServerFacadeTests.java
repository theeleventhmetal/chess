package client;

import dataaccess.*;
import model.CreateGameRequest;
import model.JoinGameRequest;
import model.LoginRequest;
import model.RegisterRequest;
import org.junit.jupiter.api.*;
import server.ClientException;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    UserDAO userDAO = new MySQLUserDAO();
    AuthDAO authDAO = new MySQLAuthDAO();
    GameDAO gameDAO = new MySQLGameDAO();

    @BeforeEach
    void setup() throws DataAccessException {
        DatabaseManager.configureDatabase();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        facade.authToken = null;
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void successfulRegister() throws ClientException {
        var registerResult = facade.register(new RegisterRequest("username", "password", "email"));
        assertTrue(registerResult.authToken().length() > 10);
    }

    @Test
    public void unsuccessfulRegister() throws ClientException {
        assertThrows(ClientException.class, () -> facade.register(new RegisterRequest("username", "password", null)));

    }

    @Test
    public void successfulLogin() throws ClientException{
        facade.register(new RegisterRequest("username", "password", "email"));
        var loginResult = facade.login(new LoginRequest("username", "password"));
        assertTrue(loginResult.authToken().length() > 10);
    }

    @Test
    public void unsuccessfulLogin() {
        assertThrows(ClientException.class, () -> facade.login(new LoginRequest("username", null)));
    }

    @Test
    public void successfulLogout() throws ClientException {
        facade.register(new RegisterRequest("username", "password", "email"));
        facade.login(new LoginRequest("username", "password"));
        facade.logout();
    }

    @Test
    public void unsuccessfulLogout(){
        assertThrows(Exception.class, () -> facade.logout());
    }

    @Test
    public void successfulListGames() throws ClientException {
        facade.register(new RegisterRequest("username", "password", "email"));
        facade.login(new LoginRequest("username", "password"));
        facade.createGame(new CreateGameRequest("gameName"));
        assertTrue(facade.listGames().games().size() == 1);

    }

    @Test
    public void unsuccessfulListGames() {
        assertThrows(Exception.class, () -> facade.listGames());
    }

    @Test
    public void successfulCreateGame() throws ClientException {
        facade.register(new RegisterRequest("username", "password", "email"));
        facade.login(new LoginRequest("username", "password"));
        facade.createGame(new CreateGameRequest("gameName"));
        facade.createGame(new CreateGameRequest("gameName2"));
        assertTrue(facade.listGames().games().size() == 2);
    }

    @Test
    public void unsuccessfulCreateGame(){
        assertThrows(Exception.class, () -> facade.createGame(new CreateGameRequest("gameName2")));
    }

    @Test
    public void successfulJoinGame() throws ClientException {
        facade.register(new RegisterRequest("username", "password", "email"));
        facade.login(new LoginRequest("username", "password"));
        var result = facade.createGame(new CreateGameRequest("gameName"));
        facade.listGames();
        int gameID = result.gameID();
        facade.joinGame(new JoinGameRequest("WHITE", gameID ));
    }

    @Test
    public void unsuccessfulJoinGame(){
        assertThrows(Exception.class, () -> facade.joinGame(new JoinGameRequest("WHITE", 2)));
    }

}
