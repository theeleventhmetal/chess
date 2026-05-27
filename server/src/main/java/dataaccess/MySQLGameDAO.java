package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.gson.Gson;

import static dataaccess.MySQLUtil.executeUpdate;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLGameDAO implements GameDAO{
    private final Gson gson = new Gson();

    @Override
    public void createGame(GameData game) throws DataAccessException {
        String gameJson = gson.toJson(game.game());
        var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        System.out.println("Creating game: " + game.gameID() + ", " + game.whiteUsername() + ", " + game.blackUsername() + ", " + game.gameName() + ", " + gameJson);
        executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), gameJson);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        int gameID = game.gameID();
        String gameJson = gson.toJson(game.game());
        var updateStatement = "UPDATE games SET whiteUsername=?, blackUsername=?, game=? WHERE gameID=?";
        executeUpdate(updateStatement, game.whiteUsername(),game.blackUsername(), gameJson, gameID);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT *  FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException( String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT *  FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUserName");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var game = rs.getString("game");
        ChessGame gameObject = gson.fromJson(game, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, gameObject);
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    updateHelper(param, ps, i);
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private void updateHelper(Object param, PreparedStatement ps, int i) throws SQLException {
        switch (param) {
            case String p -> ps.setString(i + 1, p);
            case Integer p -> ps.setInt(i + 1, p);
            case ChessGame p -> ps.setString(i + 1, p.toString());
            case null -> ps.setNull(i + 1, NULL);
            default -> {
            }
        }
    }
}
