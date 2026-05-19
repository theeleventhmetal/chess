package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{

    private final HashMap<String, AuthData> auth = new HashMap<>();

    @Override
    public AuthData getAuth(AuthData a) throws DataAccessException {
        return auth.get(a.authToken());
    }

    public void createAuth(AuthData a){
        auth.put(a.authToken(), a);
    }

    @Override
    public void clear() throws DataAccessException {
        auth.clear();
    }
}
