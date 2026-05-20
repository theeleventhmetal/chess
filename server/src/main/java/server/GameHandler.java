package server;

import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import service.GameService;
import io.javalin.http.Context;
import model.*;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();


    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Context ctx){

        try{
            String authToken = ctx.header("authorization");


            ListGameResult result = gameService.listGames(authToken);

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(result));

        } catch (
        UnauthorizedException e) {
            ctx.status(401);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(new ErrorResult(e.getMessage())));
        }  catch (Exception e) {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
    }

    public void createGame(Context ctx){
        try{
            String authToken = ctx.header("authorization");
            String jsonBody = ctx.body();

            CreateGameRequest req = gson.fromJson(jsonBody, CreateGameRequest.class);

            CreateGameResponse result = gameService.createGame(authToken, req);

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
        }  catch (Exception e) {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
    }
}

