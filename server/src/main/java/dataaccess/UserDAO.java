package dataaccess;

import model.GameData;
import model.UserData;

import java.util.Collection;

public interface UserDAO {
    void createUser(UserData u) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;
    Collection<UserData> getAllUsers() throws DataAccessException;
}
