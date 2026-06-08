package client;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import server.ClientException;
import model.*;
import server.ServerFacade;

import java.util.*;

import static ui.EscapeSequences.*;

public class PostLoginClient {
    private final ServerFacade server;
    private State state;
    private final String userName;
    private final String authToken;
    private String color;

    public PostLoginClient(ServerFacade server, State state, String clientName, String authToken) {
        this.server = server;
        this.state = state;
        this.userName = clientName;
        this.authToken = authToken;
    }

    Map<Integer, GameData> gameMap = new HashMap<>();

    public void run(){
        System.out.print("\n");
        System.out.print(help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();
            try{
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            }
            catch (Throwable e){
                result = e.toString();
                var msg = e.toString();
                System.out.print(msg);
            }
            if (state == State.GAMEPLAY){
                new GameplayClient(server, color, state).run();
            }
            else if (state == State.SIGNEDOUT){
                return;
            }
        }
    }



    public String help(){
        return """
                \n
                AVAILABLE COMMANDS:
                create <NAME> - create a game
                list - to list all games
                join <ID> [WHITE|BLACK] - to join a game by ID as white or black
                observe <ID> - to observe a game
                logout - to log out
                quit - to exit
                help
                """;
    }

    private void printPrompt() {
        System.out.print("\n"+ SET_TEXT_COLOR_LIGHT_GREY + "[LOGGED IN] >>> " + SET_TEXT_COLOR_GREEN);
    }

    public String eval(String input){
        try{
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list(params);
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "quit" -> quit();
                default -> help();
            };
        } catch (Exception ex) {
            try{
                var error =  new Gson().fromJson(ex.getMessage(), ErrorResult.class);
                return error.message();
            } catch (com.google.gson.JsonSyntaxException e){
                return ex.getMessage();
            }

        }
    }

    private String quit() {
        System.exit(0);
        return "exiting program";
    }

    private String create(String...params) throws ClientException {
        if (params.length >= 1){
            String gameName = params[0];
            CreateGameRequest request = new CreateGameRequest(gameName);
            server.createGame(request);
            return String.format("Successfully created game: %s", gameName);
        }
        throw new ClientException("Expected: create <NAME>");
    }

    private String list(String...params) throws ClientException {
        ListGameResult result = server.listGames();
        Collection<GameData> games = result.games();
        StringBuilder gameList = new StringBuilder();
        int i = 1;
        gameMap.clear();
        if (games.isEmpty()){
            return "Game list is empty";
        }
        else{
            for (GameData game: games){
                gameMap.put(i, game);
                String white = game.whiteUsername();
                String black = game.blackUsername();
                if (white == null){
                    white = "open";
                }
                if (black == null){
                    black = "open";
                }
                gameList.append(i++).append(". ").append(game.gameName()).append("\n")
                        .append("White Username: ").append(white).append("\n")
                        .append("Black Username: ").append(black).append("\n");
            }
            state = State.SIGNEDIN;
            return gameList.toString();
        }
    }

    private String join(String...params) throws ClientException{
        if (params.length >= 2){
            try {
                int gameNumber = Integer.parseInt(params[0]);
                GameData game = gameMap.get(gameNumber);
                int gameID = game.gameID();
                if (!gameMap.containsKey(gameNumber)){
                    throw new ClientException("Game does not exist");
                }
                color = params[1].toLowerCase().trim();
                if (!color.equals("white") && !color.equals("black")){
                    throw new ClientException("Color must be BLACK or WHITE");
                }
                JoinGameRequest request = new JoinGameRequest(color.toUpperCase(), gameID);
                server.joinGame(request);
                state = State.GAMEPLAY;
                return String.format("Successfully joined game number %s", gameNumber);
            }catch (NumberFormatException e){
                throw new ClientException("Game must be selected by number");
            }
            catch (NullPointerException e){
                throw new ClientException("Game does not exist");
            }
            catch (Exception e){
                state = State.SIGNEDIN;
                throw new ClientException(e.getMessage());
            }
        }
        throw new ClientException("Expected: join <ID> [WHITE|BLACK]");
    }

    private String logout(String...params) throws ClientException {
        try{
            server.logout();
            state = State.SIGNEDOUT;
            return "Logged out successfully!";
        } catch (Exception e){
            throw new ClientException("Invalid Credentials");
        }
    }

    private String observe(String...params) throws ClientException{
        if (params.length >= 1){
            try {
                int gameID = Integer.parseInt(params[0]);
                GameData game = gameMap.get(gameID);
                state = State.GAMEPLAY;
                return "Observing" + game.gameName();
            }
            catch(Exception e){
                throw new ClientException("Invalid ID");
            }
        }
        throw new ClientException("invalid request");
    }
}
