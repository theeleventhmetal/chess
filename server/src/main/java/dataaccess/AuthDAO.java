package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData getAuth(AuthData a) throws DataAccessException;
    void createAuth(AuthData a) throws DataAccessException;
    void clear() throws DataAccessException;
}
