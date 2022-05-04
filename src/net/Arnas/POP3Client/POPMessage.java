package net.Arnas.POP3Client;

import java.util.ArrayList;
import java.util.List;

public class POPMessage{
    private POPType popType;
    private List<String> arguments;

    public POPMessage(POPType popType){
        this.popType = popType;
    }

    public POPMessage(POPType popType, List<String> arguments){
        this.popType = popType;
        this.arguments = arguments;
    }

    public POPType getPopType(){
        return this.popType;
    }

    public List<String> getArguments(){
        return this.arguments;
    }

    @Override
    public String toString(){
        String convertedString = "";
        convertedString += POPInterpreter.popTypeCodeHashMap.get(popType);
        for(String argument : arguments){
            convertedString += " " + argument;
        }
        return convertedString;
    }
}
