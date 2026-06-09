package websocket.commands;

public class ResignCommand extends LeaveCommand{
    public ResignCommand(CommandType commandType, String authToken, Integer gameID, String color) {
        super(commandType, authToken, gameID, color);
    }
}
