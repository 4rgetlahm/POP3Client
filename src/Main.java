import net.Arnas.POP3Client.Communicator;
import net.Arnas.POP3Client.ConfigReader;
import net.Arnas.POP3Client.Connection;

public class Main {

    public static void main(String[] args){
        ConfigReader configReader = new ConfigReader("config.txt");
        Connection connection = new Connection(configReader.getIP(), configReader.getPort());
        Communicator communicator = new Communicator(connection);
        communicator.start();
    }

}
