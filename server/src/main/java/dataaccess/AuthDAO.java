package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData getAuth(String authToken) throws DataAccessException;
    void createAuth(AuthData a) throws DataAccessException;
    void removeAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}
