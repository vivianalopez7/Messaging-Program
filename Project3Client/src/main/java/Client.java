/**------------------------------------------
 Project 3: Messaging App
 Course: CS 342, Spring 2024
 System: IntelliJ and Windows 11 and macOS
 Student Author: Dana Fakhreddine and Viviana Lopez
 ---------------------------------------------**/

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread{
	Socket socketClient;
	ObjectOutputStream out;
	ObjectInputStream in;
	private Consumer<Serializable> callback;
	Message profile = new Message();

	//constructor for the Client class and sets callback to the Consumer<Serializable> call
	Client(Consumer<Serializable> call){
		callback = call;
	}

	//runs when client connection gets create
	//parameter: none
	//return: void
	public void run() {
		try {
			socketClient= new Socket("127.0.0.1",5555); //creating a socket
			out = new ObjectOutputStream(socketClient.getOutputStream()); //create this to write to server
			in = new ObjectInputStream(socketClient.getInputStream()); //create this to read stuff in from server
			socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {}

		//reading in the Message object that gets sent in from server
		while (true){
			try{
				Message message = (Message)in.readObject(); //read in message object from server
				message.receivers = profile.receivers; //update receivers array
				profile = message;
				callback.accept(message); //send newly updated message object to gui client
			}
			catch(Exception e){}
		}
	}

	//this sends a Message object to the server
	//parameters: Message data
	//return: void
	public void send(Message data) throws Exception {
		try {
			out.reset();
			out.writeObject(data); //writes out the Message object to the server
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
