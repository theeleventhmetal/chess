package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class GameTests {
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
    public void successfulCreateGame() throws DataAccessException, SQLException{
        int testID = 12345;
        String whiteUser = "White";
        String blackUser = "Black";
        String gameName = "Name";
        ChessGame game = new ChessGame();

        GameData gameData = new GameData(testID, whiteUser, blackUser, gameName, game);

        gameDAO.createGame(gameData);
    }


}
