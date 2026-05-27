package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTests {
    UserDAO userDAO = new MySQLUserDAO();
    AuthDAO authDAO = new MySQLAuthDAO();
    GameDAO gameDAO = new MySQLGameDAO();

    @BeforeEach
    void setup() throws DataAccessException {
        DatabaseManager.configureDatabase();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    @Test
    public void successfulCreateAuth() throws DataAccessException, SQLException {
        String testAuthToken = "TESTUSER";
        String testUsername = "TESTUSER";

        AuthData authData = new AuthData(testAuthToken, testUsername);

        authDAO.createAuth(authData);

        AuthData result = authDAO.getAuth(testAuthToken);

        assertEquals(authData, result);
    }

    @Test
    public void unsuccessfulCreateAuth() throws DataAccessException {
        String testAuthToken = "TESTUSER";
        String testUsername = "TESTUSER";

        AuthData authData = new AuthData(testAuthToken, testUsername);

        authDAO.createAuth(authData);

        String testAuthToken1 = "TESTUSER";
        String testUsername1 = "TESTUSER";

        AuthData authData1 = new AuthData(testAuthToken1, testUsername1);

        assertThrows(DataAccessException.class, () -> authDAO.createAuth(authData1));
    }

    @Test
    public void successfulGetAuth() throws DataAccessException {
        String testAuthToken = "TESTUSER";
        String testUsername = "TESTUSER";

        AuthData authData = new AuthData(testAuthToken, testUsername);

        authDAO.createAuth(authData);

        String testAuthToken1 = "TESTUSER1";
        String testUsername1 = "TESTUSER1";

        AuthData authData1 = new AuthData(testAuthToken1, testUsername1);

        authDAO.createAuth(authData1);

        AuthData result = authDAO.getAuth(testAuthToken);

        assertEquals(authData, result);
    }

    @Test
    void unsuccessfulGetAuth() throws DataAccessException, SQLException{
        assertNull(authDAO.getAuth("TestUser"));
    }

    @Test
    void successfulGetAllAuth() throws DataAccessException {
        String testAuthToken = "TESTUSER";
        String testUsername = "TESTUSER";

        AuthData authData = new AuthData(testAuthToken, testUsername);

        authDAO.createAuth(authData);

        String testAuthToken1 = "TESTUSER1";
        String testUsername1 = "TESTUSER1";

        AuthData authData1 = new AuthData(testAuthToken1, testUsername1);

        authDAO.createAuth(authData1);

        int resultSize = authDAO.getAllAuths().size();

        assertEquals(2, resultSize);
    }

    @Test
    void unsuccessfulGetAllAuth() throws DataAccessException {
        int resultSize = authDAO.getAllAuths().size();
        assertEquals(0, resultSize);
    }

    @Test
    void successfulRemoveAuth() throws DataAccessException {
        String testAuthToken = "TESTUSER";
        String testUsername = "TESTUSER";

        AuthData authData = new AuthData(testAuthToken, testUsername);

        authDAO.createAuth(authData);

        String testAuthToken1 = "TESTUSER1";
        String testUsername1 = "TESTUSER1";

        AuthData authData1 = new AuthData(testAuthToken1, testUsername1);

        authDAO.createAuth(authData1);

        authDAO.removeAuth(testAuthToken1);

        int resultSize = authDAO.getAllAuths().size();

        assertEquals(1, resultSize);
        assertNull(authDAO.getAuth(testAuthToken1));
    }

    @Test
    void unsuccessfulRemoveAuth() throws DataAccessException{
        authDAO.removeAuth("TestToken");
    }

    @Test
    void successfulClear() throws DataAccessException{
        String testAuthToken = "TESTUSER";
        String testUsername = "TESTUSER";

        AuthData authData = new AuthData(testAuthToken, testUsername);

        authDAO.createAuth(authData);

        String testAuthToken1 = "TESTUSER1";
        String testUsername1 = "TESTUSER1";

        AuthData authData1 = new AuthData(testAuthToken1, testUsername1);

        authDAO.createAuth(authData1);

        authDAO.clear();

        int resultSize = authDAO.getAllAuths().size();

        assertEquals(0, resultSize);
    }
}
