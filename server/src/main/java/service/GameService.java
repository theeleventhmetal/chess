package service;

import chess.ChessGame;
import dataaccess.*;
import model.CreateGameRequest;
import model.CreateGameResponse;
import model.GameData;
import model.ListGameResult;

import java.util.Collection;
import java.util.UUID;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGameResult listGames(String authToken) throws DataAccessException{
        if(authDAO.getAuth(authToken) == null){
            throw new UnauthorizedException("Error: unauthorized");
        }

        Collection<GameData> list = gameDAO.listGames();

        return new ListGameResult(list);
    }

    public CreateGameResponse createGame(CreateGameRequest request) throws DataAccessException{
        String authToken = request.authToken();
        String gameName = request.gameName();

        if(authDAO.getAuth(authToken) == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (gameName == null){
            throw new BadRequestException("Error: bad request");
        }

        int gameID = UUID.randomUUID().variant();
        ChessGame game = new ChessGame();

        GameData gameData = new GameData(gameID, null, null, gameName, game);

        gameDAO.createGame(gameData);

        return new CreateGameResponse(gameID);
    }


}
