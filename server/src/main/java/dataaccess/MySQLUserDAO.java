package dataaccess;

import com.google.gson.Gson;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static dataaccess.MySQLUtil.executeUpdate;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MySQLUserDAO implements UserDAO{


    @Override
    public void createUser(UserData u) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, u.username(), u.password(), u.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException( String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    @Override
    public Collection<UserData> getAllUsers() throws DataAccessException {
        Collection<UserData> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readUser(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    private UserData readUser(ResultSet rs) throws SQLException{
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username,password, email);
    }
}
