package service;
import dataaccess.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }


    public RegisterResult register(RegisterRequest request) throws DataAccessException, SQLException {
        if (request.username() == null || request.password() == null || request.email() == null){
            throw new BadRequestException("Error: bad request");
        }

        if (userDAO.getUser(request.username()) != null){
            throw new AlreadyTakenException("Error: already taken");
        }

        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());

        UserData userData = new UserData(request.username(), hashedPassword, request.email());
        userDAO.createUser(userData);

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException, SQLException {
        UserData user = userDAO.getUser(request.username());

        if (request.username() == null || request.password() == null){
            throw new BadRequestException("Error: bad request");
        }

        if (user == null || !BCrypt.checkpw(request.password(), user.password())){
            throw new UnauthorizedException("Error: unauthorized");
        }

        String newAuthToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(newAuthToken, request.username());
        authDAO.createAuth(authData);

        return new LoginResult(request.username(), newAuthToken);
    }

    public void logout(String authToken) throws DataAccessException {

        if(authDAO.getAuth(authToken) == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        else if(authToken == null){
            throw new UnauthorizedException("Error: unauthorized");
        }

        authDAO.removeAuth(authToken);
    }
}
