package dataaccess;

import model.*;
import org.junit.jupiter.api.*;


import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

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
    public void successfulCreateUser() throws DataAccessException, SQLException {
        String testUser = "TESTUSER";
        String testPassword = "TESTPASSWORD";
        String testEmail = "TESTEMAIL@GMAIL";

        UserData userData = new UserData(testUser, testPassword, testEmail);

        userDAO.createUser(userData);

        UserData result = userDAO.getUser(testUser);

        assertEquals(userData, result);
    }

    @Test void unsuccessfulCreateUser() throws DataAccessException {
        String testUser = "TESTUSER";
        String testPassword = "TESTPASSWORD";
        String testEmail = "TESTEMAIL@GMAIL";

        UserData userData = new UserData(testUser, testPassword, testEmail);

        userDAO.createUser(userData);

        String testUser1 = "TESTUSER";
        String testPassword1 = "TESTPASSWORD";
        String testEmail1 = "TESTEMAIL@GMAIL";

        UserData userData1 = new UserData(testUser1, testPassword1, testEmail1);

        assertThrows(DataAccessException.class, () -> userDAO.createUser(userData1));
    }

    @Test
    void successfulGetUser() throws DataAccessException, SQLException {

        String testUser = "TESTUSER";
        String testPassword = "TESTPASSWORD";
        String testEmail = "TESTEMAIL@GMAIL";

        UserData userData = new UserData(testUser, testPassword, testEmail);

        userDAO.createUser(userData);

        String testUser1 = "TESTUSER1";
        String testPassword1 = "TESTPASSWORD1";
        String testEmail1 = "TESTEMAIL@GMAIL";

        UserData userData1 = new UserData(testUser1, testPassword1, testEmail1);

        userDAO.createUser(userData1);

        UserData result = userDAO.getUser(testUser);

        assertEquals(userData, result);
    }

    @Test
    void unsuccessfulGetUser() throws DataAccessException, SQLException{
        assertNull(userDAO.getUser("TestUser"));
    }

    @Test
    void successfulGetAllUsers() throws DataAccessException, SQLException {
        String testUser = "TESTUSER";
        String testPassword = "TESTPASSWORD";
        String testEmail = "TESTEMAIL@GMAIL";

        UserData userData = new UserData(testUser, testPassword, testEmail);

        userDAO.createUser(userData);

        String testUser1 = "TESTUSER1";
        String testPassword1 = "TESTPASSWORD1";
        String testEmail1 = "TESTEMAIL@GMAIL";

        UserData userData1 = new UserData(testUser1, testPassword1, testEmail1);

        userDAO.createUser(userData1);

        int resultSize = userDAO.getAllUsers().size();

        assertEquals(2, resultSize);
    }

    @Test
    void unsuccessfulGetAllUsers() throws DataAccessException {
        int resultSize = userDAO.getAllUsers().size();
        assertEquals(0, resultSize);
    }

    @Test
    void successfulClear() throws DataAccessException{
        String testUser = "TESTUSER";
        String testPassword = "TESTPASSWORD";
        String testEmail = "TESTEMAIL@GMAIL";

        UserData userData = new UserData(testUser, testPassword, testEmail);

        userDAO.createUser(userData);

        String testUser1 = "TESTUSER1";
        String testPassword1 = "TESTPASSWORD1";
        String testEmail1 = "TESTEMAIL@GMAIL";

        UserData userData1 = new UserData(testUser1, testPassword1, testEmail1);

        userDAO.createUser(userData1);

        userDAO.clear();

        assertEquals(0, userDAO.getAllUsers().size());
    }







}
