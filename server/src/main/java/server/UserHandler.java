package server;
import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.ErrorResult;
import model.RegisterRequest;
import model.RegisterResult;
import service.UserService;

public class UserHandler {

    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        try {

            String jsonBody = ctx.body();

            RegisterRequest req = gson.fromJson(jsonBody, RegisterRequest.class);

            RegisterResult result = userService.register(req);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.json(gson.toJson(result));

        } catch (AlreadyTakenException e) {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.json(new ErrorResult(e.getMessage()));
        }


        }
    }
}
