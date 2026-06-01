package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.Collection;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(1);

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

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws DataAccessException{
        String gameName = request.gameName();

        if(authDAO.getAuth(authToken) == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (gameName == null){
            throw new BadRequestException("Error: bad request");
        }

        int gameID = ID_COUNTER.getAndIncrement();
        ChessGame game = new ChessGame();

        GameData gameData = new GameData(gameID, null, null, gameName, game);

        gameDAO.createGame(gameData);

        return new CreateGameResult(gameID);
    }

    public void joinGame(String authToken, JoinGameRequest request) throws DataAccessException {
        int gameID = request.gameID();
        String desiredColor = request.playerColor();

        if(authDAO.getAuth(authToken) == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (gameID == 0 || desiredColor == null ||(!desiredColor.equals("WHITE") && !desiredColor.equals("BLACK"))){
            throw new BadRequestException("Error: bad request");
        }

        GameData gameData = gameDAO.getGame(gameID);

        if (desiredColor.equals("WHITE") && gameData.whiteUsername() != null){
            throw new AlreadyTakenException("Error: already taken");
        }
        if (desiredColor.equals("BLACK") && gameData.blackUsername() != null){
            throw new AlreadyTakenException("Error: already taken");
        }

        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();

        if (desiredColor.equals("WHITE")) {
            GameData newGameData = new GameData(gameID, username, gameData.blackUsername(), gameData.gameName(), gameData.game());
            gameDAO.updateGame(newGameData);
        }
        else{
            GameData newGameData = new GameData(gameID, gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
            gameDAO.updateGame(newGameData);
        }
    }
}



