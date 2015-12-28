
import java.net.*;
import java.io.*;
import SerialServer.*;
public class main {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
		boolean listen = true;
        try {
            serverSocket = new ServerSocket(6789);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 6789.");
            System.exit(-1);
        }

        while (listen)
			new  ConnectionThread(serverSocket.accept()).start();

		System.out.println("Server is closing");
        serverSocket.close();
    }
}
