package server;
import dataaccess.*;
import io.javalin.*;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        UserHandler userHandler = new UserHandler(userService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user", userHandler::register);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
