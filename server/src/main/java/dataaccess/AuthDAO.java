package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.Collection;

public interface AuthDAO {
    AuthData getAuth(String authToken) throws DataAccessException;
    void createAuth(AuthData a) throws DataAccessException;
    void removeAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
    Collection<AuthData> getAllAuths() throws DataAccessException;
}
