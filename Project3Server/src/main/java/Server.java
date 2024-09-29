/**------------------------------------------
 Project 3: Messaging App
 Course: CS 342, Spring 2024
 System: IntelliJ and Windows 11 and macOS
 Student Author: Dana Fakhreddine and Viviana Lopez
 ---------------------------------------------**/

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server{
	int count = 1;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	ArrayList<String> allUsers = new ArrayList<>();

	//this is the constructor for the Server class
	Server(Consumer<Serializable> call){
		callback = call;
		server = new TheServer();
		server.start();
	}

	public class TheServer extends Thread{

		//runs when server connection gets invoked
		//parameter: none
		//return: void
		public void run() {

			try(ServerSocket mysocket = new ServerSocket(5555);){
				System.out.println("Server is waiting for a client!");

				//reading in the Message object that gets sent in from server
				while(true) {
					ClientThread c = new ClientThread(mysocket.accept(), count);
					callback.accept("client has connected to server: " + "client #" + count);
					clients.add(c);
					c.start();

					count++;
				}
			}//end of try
			catch(Exception e) {
				callback.accept("Server socket did not launch");
			}
		}//end of while
	}

	class ClientThread extends Thread{
		Socket connection;
		int count;
		ObjectInputStream in;
		ObjectOutputStream out;
		Message profile;

		//this is the ClientThread constructor
		//parameters: Socket s, int count
		ClientThread(Socket s, int count){
			this.connection = s;
			this.count = count;
			profile = new Message();
		}

		//loops through clients array and writes out to each of them
		//parameter: Message message
		//return: void
		public void updateClients(Message message) {
			for(int i = 0; i < clients.size(); i++) {
				ClientThread t = clients.get(i); //getting specific client

				//if the Message object's sendAllFlag() is equal to 1, then set the clientThread's Message object's sendAllFlag to 1 and set its message to the Message object's message
				if (message.getSendAllFlag().equals(1)){
					t.profile.setMessage(message.getMessage());
					t.profile.setSendAllFlag(1);
				}
				t.profile.usernames = message.usernames; //updating the clients usernames array
				try {
					t.out.reset();
					t.out.writeObject(t.profile); //writing message object out to the client
					t.profile.setSendAllFlag(0);
				}
				catch(Exception e) {}
			}
		}

		//run gets activated when a client connects to the server
		//parameter: none
		//return: void
		public void run(){
			//creating input and output streams to read in and write to client
			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			}
			catch(Exception e) {
				System.out.println("Streams not open");
			}

			//waiting to read in something from a client
			while(true) {
				try {
					Message tmpProfile = (Message)in.readObject(); //reading in message object

					//if message object's getSendAllFlag() is 1, that means client is sending a message to all users
					if(tmpProfile.getSendAllFlag().equals(1)){
						callback.accept( tmpProfile.getMessage());
						updateClients(tmpProfile); //write message to all clients
					}
					//if message object's sendMessageFlag() is 1, client is sending a message to specific conversation
					else if(tmpProfile.getSendMessageFlag().equals(1)){
						callback.accept( tmpProfile.getUserId() + " sent a message: " + tmpProfile.getMessage() + " to " + tmpProfile.receivers);
						//look for receivers that client is sending a message to
						for(int i = 0; i < clients.size(); i++){

							//if receiver/s found, then send object to that receiver
							if(tmpProfile.receivers.contains(clients.get(i).profile.getUserId())){
								clients.get(i).profile.setSender(tmpProfile.getUserId()); //setting who is sending the message
								clients.get(i).profile.setSendMessageFlag(1); //setting the flag to indicate message is sent
								clients.get(i).out.reset();
								clients.get(i).profile.setMessage(tmpProfile.getMessage()); //set message being sent
								clients.get(i).out.writeObject(clients.get(i).profile); //write out to client receiver
								clients.get(i).profile.setSendMessageFlag(0);
							}
						}
					}

					//if new chat was created
					else if (tmpProfile.getCreateChatFlag().equals(1)){
						callback.accept("new conversation started with: " + tmpProfile.getUserId() + " and " + tmpProfile.receivers);
						//look for receivers
						for(int i = 0; i < clients.size(); i++){
							if(tmpProfile.getUserId().equals(clients.get(i).profile.getUserId())){
								clients.get(i).profile.conversations = tmpProfile.conversations;
							}

							//if receiver/s found, then send object to that receiver to update their conversations list
							if(tmpProfile.receivers.contains(clients.get(i).profile.getUserId()) && !clients.get(i).profile.conversations.contains(tmpProfile.getUserId())){
								clients.get(i).profile.setSender(tmpProfile.getUserId()); //setting who created the conversation
								clients.get(i).profile.setCreateChatFlag(1); //setting the flag to indicate conversation was created
								clients.get(i).out.reset();
								clients.get(i).out.writeObject(clients.get(i).profile); //write out to client receiver
								clients.get(i).profile.conversations.add(tmpProfile.getUserId()); //add to their conversations list
								clients.get(i).profile.setCreateChatFlag(0);
							}
						}
					}
					//if the user is choosing a username and trying to join
					else{
						Boolean userExists = false;
						//loops through all of the clients to check their usernames
						for (int i =0; i < clients.size(); i++){
							//checks if the username the client chose already exists
							if(clients.get(i).profile.getUserId().equals(tmpProfile.getUserId())){
								callback.accept("Duplicate username made!");
								userExists = true;
								tmpProfile.setCheckDupFlag(1); //set the dup flag 1 indicating the username already exists
								updateClients(tmpProfile);
							}
						}

						//if the username does not already exist, then it is a valid username
						if (userExists.equals(false)){
							callback.accept("client: " + count + " set their new username to: " + tmpProfile.getUserId());
							tmpProfile.setCheckDupFlag(0);
							clients.get(count-1).profile = tmpProfile; //stores the tmpProfile Message object in the clients array list
							allUsers.add(tmpProfile.getUserId()); //adds the Message object's username to the allUsers array list
							tmpProfile.usernames.addAll(allUsers); //adds all the usernames in the allUsers array list to the Message object's usernames array list
							updateClients(tmpProfile);
						}
					}
				}
				//client leaves
				catch(Exception e) {
					callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					clients.remove(this);
					break;
				}
			}
		}//end of run
	}//end of client thread
}
