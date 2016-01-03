package SerialServer;
import java.net.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;

public class ConnectionThread extends Thread {
	SerialPort serialPort;
	private final static Logger LOGGER = Logger.getLogger("ServerLogger"); 
	private final static String SERIAL_PORT = "/dev/ttymxc3";
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;
	private CommPortIdentifier portId;
    private Socket socket = null;
	
	public ConnectionThread(Socket socket) {
		super("MultiServerThread");
		LOGGER.info("New thread started");
		this.socket = socket;
		System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttymxc3");
		portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			if(currPortId.getName().equals(SERIAL_PORT))
				portId = currPortId;
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}
	}

	public void run() {
		LOGGER.info("Thread running");
		try {
			//PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
						new InputStreamReader(
						socket.getInputStream()));

			String inputLine;//, outputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
				try{
					LOGGER.finest("Received data: " + inputLine);
					serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);
					serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
					output = serialPort.getOutputStream();
					inputLine += "\n";
					output.write(inputLine.getBytes());
					LOGGER.finest("Sent data over serial: " + inputLine);
					serialPort.close();
				}catch(Exception e){
					LOGGER.warning(e.getCause().getMessage());
				}
				
			if (inputLine.equals("Bye"))
				break;
			}
			LOGGER.warning("Closing socket to client");
			in.close();
			socket.close();

		} catch (IOException e) {
			LOGGER.warning(e.getCause().getMessage());
		}
	}
}