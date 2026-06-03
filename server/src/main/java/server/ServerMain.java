package server;

import dataaccess.DataAccessException;

public class ServerMain {
    public static void main(String[] args) throws DataAccessException {
        try {
            Server server = new Server();
            server.run(8080);
        } catch(Exception e){
            e.printStackTrace();
        }


        System.out.println("♕ 240 Chess Server");
    }
}
