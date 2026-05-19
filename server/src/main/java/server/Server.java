package server;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.json.JavalinGson;
import service.ClearService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        UserService userService = new UserService(userDAO, authDAO);
        UserHandler userHandler = new UserHandler(userService);

        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        ClearHandler clearHandler = new ClearHandler(clearService);

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
        });
        javalin.post("/user", userHandler::register);
        javalin.delete("/db", clearHandler::clear);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
