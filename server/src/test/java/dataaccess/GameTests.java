package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

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

        assertNotNull(gameDAO.getGame(testID));
    }

    @Test
    public void unsuccessfulCreateGame() throws DataAccessException{
        int testID = 12345;
        String whiteUser = "White";
        String blackUser = "Black";
        String gameName = "Name";
        ChessGame game = new ChessGame();

        GameData gameData = new GameData(testID, whiteUser, blackUser, gameName, game);

        gameDAO.createGame(gameData);

        int testID1 = 12345;
        String whiteUser1 = "White";
        String blackUser1 = "Black";
        String gameName1 = "Name";
        ChessGame game1 = new ChessGame();

        GameData gameData1 = new GameData(testID1, whiteUser1, blackUser1, gameName1, game1);

        assertThrows(DataAccessException.class, ()-> gameDAO.createGame(gameData1));
    }

    @Test
    public void successfulUpdateGame() throws DataAccessException, InvalidMoveException {
        int testID = 12345;
        String whiteUser = "White";
        String blackUser = "Black";
        String gameName = "Name";
        ChessGame game = new ChessGame();

        GameData gameData = new GameData(testID, whiteUser, blackUser, gameName, game);

        gameDAO.createGame(gameData);

        game.makeMove(new ChessMove(new ChessPosition(2,1), new ChessPosition(3,1), null));

        gameDAO.updateGame(gameData);

        GameData result = gameDAO.getGame(testID);

        //INCOMPLETE
    }
}
