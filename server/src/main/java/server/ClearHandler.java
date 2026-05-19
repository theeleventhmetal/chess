package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.ClearService;
import io.javalin.http.Context;

import java.util.Map;

public class ClearHandler {

    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear(Context ctx) throws DataAccessException {
        clearService.clear();

        ctx.status(200);
        ctx.json(gson.toJson(Map.of()));
    }
}
