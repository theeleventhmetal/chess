package client;

import com.google.gson.Gson;
import dataaccess.UnauthorizedException;
import model.*;
import server.ClientException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLoginClient {

    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private String username;
    private String authToken;


    public PreLoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run(){
        System.out.println("Welcome to chess! Type Help to get started.");
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
            if (state == State.SIGNEDIN){
                new PostLoginClient(server, state, username, authToken).run();
            }
        }
    }

    private String help(){
       return """
               AVAILABLE COMMANDS:
               register <USERNAME> <PASSWORD> <EMAIL> - to create an account
               login <USERNAME> <PASSWORD> - to play chess
               quit - to exit
               help
               """;
    }

    private void printPrompt() {
        System.out.print("\n"+ SET_TEXT_COLOR_LIGHT_GREY + "[LOGGED OUT] >>> " + SET_TEXT_COLOR_GREEN);
    }

    public String eval(String input){
        try{
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            try{
                var error =  new Gson().fromJson(ex.getMessage(), ErrorResult.class);
                return error.message();
            } catch (Exception exc){
                return ex.getMessage();
            }
        }
    }

    public String register(String... params) throws ClientException {
        if (params.length >= 3){
            try{
                username = params[0];
                String password = params[1];
                String email = params[2];
                RegisterRequest registerRequest = new RegisterRequest(username, password, email);
                System.out.println("Registering...");
                server.register(registerRequest);
                System.out.println("Logging in...");
                LoginRequest loginRequest = new LoginRequest(username, password);
                LoginResult loginResult = server.login(loginRequest);
                String authToken = loginResult.authToken();
                server.setAuthToken(authToken);
                state = State.SIGNEDIN;
                return String.format("Successfully registered and logged in as: %s", username);
            }catch (Exception e){
                username = null;
                state = State.SIGNEDOUT;
                throw new ClientException("Username already taken");
            }
        }
        state = State.SIGNEDOUT;
        throw new ClientException("Expected: register  <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ClientException {
        if (params.length >= 2){
            try{
                username = params[0];
                String password = params[1];
                LoginRequest loginRequest = new LoginRequest(username, password);
                LoginResult loginResult = server.login(loginRequest);
                authToken = loginResult.authToken();
                server.setAuthToken(authToken);
                state = State.SIGNEDIN;
                return String.format("Successfully logged in as: %s", username);
            } catch (Exception e){
                username = null;
                state = State.SIGNEDOUT;
                throw new ClientException("Unauthorized");
            }
        }
        state = State.SIGNEDOUT;
        throw new ClientException("Expected: login <USERNAME> <PASSWORD>");
    }
}
