
import java.net.*;
import java.io.*;
import SerialServer.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;

public class main {
	private final static Logger LOGGER = Logger.getLogger("ServerLogger"); 
	private static FileHandler fh;  

    public static void main(String[] args){
        ServerSocket serverSocket = null;
		boolean listen = true;
		
		try{
			LOGGER.setLevel(Level.FINEST); 
			fh = new FileHandler("./logs/ServerLog.log");
			LOGGER.addHandler(fh);
		} catch(IOException e){
			e.printStackTrace();
		}
        try {
            serverSocket = new ServerSocket(6789);
			serverSocket.setSoTimeout(0);
        } catch (IOException e) {
            LOGGER.severe("Could not listen on port: 6789.");
			LOGGER.severe(e.getCause().getMessage());
            System.exit(-1);
        }

        while (listen){
			try{
				LOGGER.info("Client connected");
				new  ConnectionThread(serverSocket.accept()).start();
			}
			catch(IOException e){
				LOGGER.warning("Client connection failed");
				LOGGER.warning(e.getCause().getMessage());
			}
		}
		
		try{
			LOGGER.info("Server is closing");
			serverSocket.close();
		}
		catch(IOException e){
			LOGGER.severe("Failed to close server");
			LOGGER.severe(e.getCause().getMessage());
		}
    }
}
