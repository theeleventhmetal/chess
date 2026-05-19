package server;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.RegisterRequest;
import model.RegisterResult;
import service.UserService;

public class UserHandler {

    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) throws DataAccessException {
        String jsonBody = ctx.body();

        RegisterRequest req = gson.fromJson(jsonBody, RegisterRequest.class);

        RegisterResult result = userService.register(req);

        ctx.status(200);
        ctx.contentType("application/json");
        ctx.json(gson.toJson(result));

        // CATCH EXCEPTIONS HERE
    }
}
