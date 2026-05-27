package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MySQLUserDAO implements UserDAO{


    @Override
    public void createUser(UserData u) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        int id = executeUpdate(statement, u.username(), u.password(), u.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException, SQLException {
        return null;
//        try(Connection conn = DatabaseManager.getConnection()) {
//            var statement =
//
//        }
    }

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public Collection<UserData> getAllUsers() throws DataAccessException {
        return List.of();
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
