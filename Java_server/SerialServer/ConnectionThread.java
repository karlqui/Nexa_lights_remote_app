package SerialServer;
import java.net.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import java.util.Enumeration;

public class ConnectionThread extends Thread {
	SerialPort serialPort;
	private static final String PORT_NAMES[] = { 
		//	"/dev/tty.usbserial-A9007UX1", // Mac OS X
        //                "/dev/ttyACM0", // Raspberry Pi
	     "/dev/ttymxc3", // Udoo
	//		"/dev/ttyUSB0", // Linux
		//	"COM3", // Windows
	};
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;
	CommPortIdentifier portId;
    private Socket socket = null;
	public ConnectionThread(Socket socket) {
		super("MultiServerThread");
		this.socket = socket;
		System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttymxc3");
		portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}
	}

	public void run() {
		try {
			//PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
						new InputStreamReader(
						socket.getInputStream()));

			String inputLine;//, outputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
				try{
					serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);
					serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
					output = serialPort.getOutputStream();
					inputLine += "\n";
					output.write(inputLine.getBytes());
					serialPort.close();
				}catch(Exception e){
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
				
			if (inputLine.equals("Bye"))
				break;
			}
			in.close();
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}