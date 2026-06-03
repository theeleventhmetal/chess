package server;
import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import model.*;
import service.UserService;

import java.util.Map;

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
            ctx.result(gson.toJson(result));

        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(new ErrorResult(e.getMessage())));
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(new ErrorResult(e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
    }


    public void login(Context ctx) {
        try {

            String jsonBody = ctx.body();

            LoginRequest req = gson.fromJson(jsonBody, LoginRequest.class);

            LoginResult result = userService.login(req);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(result));

        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(new ErrorResult(e.getMessage())));
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(new ErrorResult(e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
    }

    public void logout(Context ctx) {
        try{

            String authToken = ctx.header("authorization");

            userService.logout(authToken);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.json(gson.toJson(Map.of()));

        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(new ErrorResult(e.getMessage())));
        }  catch (Exception e) {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
    }
}
















