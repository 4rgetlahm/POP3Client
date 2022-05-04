package net.Arnas.POP3Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class POPInterpreter {
    public static HashMap<POPType, String> popTypeCodeHashMap = new HashMap<>(){
        {
            put(POPType.OK, "+OK");
            put(POPType.ERR, "-ERR");
            put(POPType.QUIT, "QUIT");
            put(POPType.STAT, "STAT");
            put(POPType.LIST, "LIST");
            put(POPType.RETR, "RETR");
            put(POPType.DELE, "DELE");
            put(POPType.RSET, "RSET");
            put(POPType.TOP, "TOP");
            put(POPType.UIDL, "UIDL");
            put(POPType.USER, "USER");
            put(POPType.PASS, "PASS");
            put(POPType.APOP, "APOP");
            put(POPType.NOOP, "NOOP");
        }
    };

    private HashMap<String, POPType> codePopTypeHashMap = new HashMap<>();

    public POPInterpreter(){
        for(Map.Entry<POPType, String> entry : popTypeCodeHashMap.entrySet()){
            codePopTypeHashMap.put(entry.getValue(), entry.getKey());
        }
    }

    public POPMessage convertToPOPMessage(String message){
        if(message == null){
            return null;
        }
        String[] splits = message.split(" ");
        POPType popType = codePopTypeHashMap.get(splits[0]);
        if(popType == null){
            popType = POPType.ANY;
        }
        String[] arguments = Arrays.copyOfRange(splits, 1, splits.length);
        return new POPMessage(popType, Arrays.asList(arguments));
    }

    public String convertFromPOPMessage(POPMessage popMessage){
        return popMessage.toString();
    }
}
