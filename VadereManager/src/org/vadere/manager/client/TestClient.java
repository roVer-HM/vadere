package org.vadere.manager.client;

import org.vadere.manager.TraCISocket;
import org.vadere.manager.stsc.TraCIPacket;
import org.vadere.manager.stsc.commands.control.TraCIGetVersionCommand;
import org.vadere.manager.stsc.commands.control.TraCISendFileCommand;
import org.vadere.manager.stsc.commands.control.TraCISimStepCommand;
import org.vadere.manager.stsc.reader.TraCIPacketBuffer;
import org.vadere.manager.stsc.respons.TraCIResponse;
import org.vadere.manager.stsc.respons.TraCISimTimeResponse;
import org.vadere.util.io.IOUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TestClient implements Runnable{

	private int port;
	private TraCISocket traCISocket;
	private ConsoleReader consoleReader;
	private Thread consoleThread;
	private boolean running;

	public static void main(String[] args) throws IOException, InterruptedException {
		TestClient testClient = new TestClient(9999);
		testClient.run();
	}


	public TestClient(int port) {
		this.port = port;
		this.running = false;
	}


	private void addCommands(ConsoleReader consoleReader){
		consoleReader.addCommand("getVersion", "", this::getVersion);
		consoleReader.addCommand("sendFile", "send file. default: scenario001", this::sendFile);
		consoleReader.addCommand("nextStep", "default(-1) one loop.", this::nextSimTimeStep);
	}



	private void establishConnection() throws IOException, InterruptedException {
			Socket socket = new Socket();
			socket.setTcpNoDelay(true);
			int waitTime = 500; //ms
			System.out.println("Connect to 127.0.0.1:" + this.port);
			for (int i = 0; i < 14; i++) {
				try {
					socket.connect(new InetSocketAddress("127.0.0.1", this.port));
					break;
				} catch (ConnectException ex) {
					Thread.sleep(waitTime);
					waitTime *= 2;
				}
			}

			if (!socket.isConnected()) {
				System.out.println("can't connect to Server!");
				return;
			}

			System.out.println("connected...");
			traCISocket = new TraCISocket(socket);

			running = true;
	}

	private void handleConnection() throws IOException {
		try{

			consoleReader = new ConsoleReader();
			addCommands(consoleReader);
			consoleThread = new Thread(consoleReader);
			consoleThread.start();

			consoleThread.join();


		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (traCISocket != null)
				traCISocket.close();
			if (consoleReader != null)
				consoleReader.stop();
		}
	}


	@Override
	public void run() {

		try {
			establishConnection();
			handleConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	// Commands

	void getVersion(String[] args) throws IOException {
		TraCIPacket p = TraCIGetVersionCommand.build();
		traCISocket.sendExact(p);

		TraCIPacketBuffer buf = traCISocket.receiveExact();
		TraCIResponse cmd = buf.nextResponse();

		System.out.println(cmd.toString());
	}

	void nextSimTimeStep(String[] args) throws IOException{
		double nextSimTime = -1.0;

		if (args.length>2)
			nextSimTime = Double.parseDouble(args[1]);

		TraCIPacket packet = TraCISimStepCommand.build(nextSimTime);
		traCISocket.sendExact(packet);

		TraCIPacketBuffer buf = traCISocket.receiveExact();
		TraCISimTimeResponse cmd = (TraCISimTimeResponse) buf.nextResponse();
		System.out.println(cmd.toString());
	}

	void getPersonList(String[] args) throws IOException{

	}

	void sendFile(String[] args) throws IOException {

		String filePath = "/home/stsc/repos/vadere/VadereManager/testResources/testProject001/scenarios/scenario001.scenario";

		if (args.length > 1)
			filePath = args[1];

		TraCIPacket packet = TraCISendFileCommand.TraCISendFileCommand(IOUtils.readTextFile(filePath));

		traCISocket.sendExact(packet);

		TraCIPacketBuffer buf = traCISocket.receiveExact();
		TraCIResponse cmd = buf.nextResponse();

		System.out.println(cmd.toString());
	}

}