package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData u) throws DataAccessException {
        users.put(u.username(), u);
    }

    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }
}
