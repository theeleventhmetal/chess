package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{

    private final HashMap<String, AuthData> auth = new HashMap<>();

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return auth.get(authToken);
    }

    public void createAuth(AuthData a){
        auth.put(a.authToken(), a);
    }

    @Override
    public void removeAuth(String authToken) throws DataAccessException {
        auth.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        auth.clear();
    }
}
