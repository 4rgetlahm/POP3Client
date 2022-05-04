package net.Arnas.POP3Client;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private Properties properties = new Properties();

    public ConfigReader(String configLocation){
        try(FileReader fileReader = new FileReader(configLocation)){
            properties.load(fileReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIP(){
        return properties.getProperty("IP");
    }

    public int getPort(){
        try {
            return Integer.parseInt(properties.getProperty("PORT"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }
}
