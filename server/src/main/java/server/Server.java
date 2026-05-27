package server;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.json.JavalinGson;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server()  {

        try {
            DatabaseManager.configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        UserDAO userDAO = new MySQLUserDAO();
        AuthDAO authDAO = new MySQLAuthDAO();
        GameDAO gameDAO = new MySQLGameDAO();

        UserService userService = new UserService(userDAO, authDAO);
        UserHandler userHandler = new UserHandler(userService);

        GameService gameService = new GameService(gameDAO, authDAO);
        GameHandler gameHandler = new GameHandler(gameService);

        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        ClearHandler clearHandler = new ClearHandler(clearService);

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
        });
        javalin.delete("/db", clearHandler::clear);

        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);

        javalin.get("/game",gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
