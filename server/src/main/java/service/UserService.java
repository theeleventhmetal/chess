package service;
import dataaccess.*;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;

import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }


    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null || request.email() == null){
            throw new BadRequestException("Error: bad request");
        }

        if (userDAO.getUser(request.username()) != null){
            throw new AlreadyTakenException("Error: already taken");
        }

        UserData userData = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(userData);

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        return new RegisterResult(request.username(), authToken);
    }
}
