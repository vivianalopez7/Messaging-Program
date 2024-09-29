/**------------------------------------------
 Project 3: Messaging App
 Course: CS 342, Spring 2024
 System: IntelliJ and Windows 11 and macOS
 Student Author: Dana Fakhreddine and Viviana Lopez
 ---------------------------------------------**/

import java.util.HashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import static javafx.scene.paint.Color.rgb;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.image.ImageView;
import javafx.stage.WindowEvent;

public class GuiClient extends Application{
	VBox allMessages = new VBox(15);;
	Client clientConnection;
	TextField createUser = new TextField();
	Map<Button, ObservableList<String>> findConvos = new HashMap<Button, ObservableList<String>>(); //keeps track of which conversations go with which buttons
	ListView<String> sendAllConvos = new ListView<String>(); //holds conversation in send all screen
	ObservableList <String>usernamesList = FXCollections.observableArrayList(); //contains all the users
	ListView<String> listOfUsers = new ListView<String>(usernamesList); //makes usernames list viewable
	VBox messages = new VBox(20);

	public static void main(String[] args) {
		launch(args);
	}

	//takes care of all the functionality between the gui client and client class, deals with the callbacks
	//parameter: primaryStage
	//return: void
	@Override
	public void start(Stage primaryStage) throws Exception {
		//start new clientConnection and listens for stuff sent from client class
		clientConnection = new Client(data->{
			Platform.runLater(()-> {
				//if the user decides to send a message to all
				if(clientConnection.profile.getSendAllFlag().equals(1)){
					clientConnection.profile.setSendAllFlag(0); //reset flag
					sendAllConvos.getItems().add(clientConnection.profile.getMessage()); //add to conversations list that gets displayed
				}
				//if the user is just sending a message to one of their conversations
				else if(clientConnection.profile.getSendMessageFlag().equals(1)){
					clientConnection.profile.setSendMessageFlag(0); //reset flag
					//loop through the keys in the map to find specific conversation
					for(Button checkButton: findConvos.keySet()){
						//if this is the correct button, add message to the observable list inside the map that corresponds with that conversation
						if(checkButton.getText().contains(clientConnection.profile.getSender())){
							findConvos.get(checkButton).add(clientConnection.profile.getMessage());
							break;
						}
					}

					//if the current screen that client is currently in is with the user that is sending the message, update the screen with new message
					if(clientConnection.profile.receivers.contains(clientConnection.profile.getSender())){
						createChats(clientConnection.profile.getMessage());
					}
					clientConnection.profile.setSender(""); //reset sender
				}
				//if a new chat is being created
				else if (clientConnection.profile.getCreateChatFlag().equals(1)){
					clientConnection.profile.setCreateChatFlag(0); //reset flag

					//if conversation was not already made
					if(!clientConnection.profile.conversations.contains(clientConnection.profile.getSender())) {
						clientConnection.profile.conversations.add(clientConnection.profile.getSender()); //add conversation to conversations list

						//create name of conversation based off client who made it
						String text = "Message with " + clientConnection.profile.getSender();
						Button buttonUser = new Button(text);

						//customizing chat button
						buttonUser.setFont(Font.font("Courier New", FontWeight.BOLD, 15));
						buttonUser.setMinWidth(350);
						buttonUser.setMinHeight(60);

						//add conversation to the map
						ObservableList<String> tempList = FXCollections.observableArrayList();
						findConvos.put(buttonUser, tempList); //add button and list to map
						allMessages.getChildren().add(buttonUser); //add convo button to vbox displayed in view messages screen
					}
				}
				//if client is just creating their username and joining
				else{
					//if username is not a duplicate
					if(clientConnection.profile.getCheckDupFlag().equals(0)) {
						//loop through usernames list and display all usernames that are not already displayed
						for (String username : clientConnection.profile.usernames) {
							if (!usernamesList.contains(username)) {
								usernamesList.add(username);
							}
						}

						//go to allThreadsScreen
						try {
							allThreadsScreen(primaryStage);
						} catch (Exception ex) {
							throw new RuntimeException(ex);
						}
					}
					//if username is a duplicate, tell user to try again
					else{
						//if user exists, empty username and don't let them continue
						createUser.setText("UserName Exists...Try again");
					}
				}
			});
		});
		clientConnection.start(); //start client connection
		welcomeScreen(primaryStage); //go to welcomeScreen
	}

	//welcome screen where users pick their username and join the messaging app
	//parameter: Stage primaryStage
	//return: void
	public void welcomeScreen(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Welcome to YapaVerse!!!!"); //title for screen

		//create textMe image
		InputStream stream = new FileInputStream("src/Images/textmegif.gif");
		Image image = new Image(stream);
		ImageView imageView = new ImageView();

		//sets the width and height of the image
		imageView.setImage(image);
		imageView.setFitWidth(200);
		imageView.setFitHeight(225);
		imageView.setPreserveRatio(false);

		//header text at top of screen
		Text header = new Text();
		header.setText("WELCOME TO");
		header.setFill(rgb(191,132,234)); //color of text
		header.setFont(Font.font("Trattatello", FontWeight.BOLD, 54));
		header.setTranslateX(2);
		//creates shadow for text
		DropShadow dropShadow1 = new DropShadow();
		dropShadow1.setOffsetY(3.0f);
		dropShadow1.setColor(Color.color(0.01f, 0.01f, 0.01f));
		header.setEffect(dropShadow1);

		//second line for header text
		Text header2 = new Text();
		header2.setText("  YAPAVERSE");
		header2.setFill(rgb(191,132,234));
		header2.setFont(Font.font("Trattatello", FontWeight.BOLD, 54));

		//shadow for second header text
		DropShadow dropShadow2 = new DropShadow();
		dropShadow2.setOffsetY(3.0f);
		dropShadow2.setColor(Color.color(0.001f, 0.001f, 0.001f));
		header2.setEffect(dropShadow2);

		//vbox containing both headers
		VBox headerBox = new VBox(-25, header, header2);
		headerBox.setTranslateY(-10);

		headerBox.setTranslateX(20);

		//textfield above join button to enter username
		createUser.setAlignment(Pos.CENTER);
		createUser.setMaxWidth(250);

		//text next to textfield
		Text createMessage = new Text("Create Username:");
		createMessage.setFont(Font.font("Courier New", FontWeight.BOLD, 20));

		//vbox that contains textfield with text
		VBox userBox = new VBox(20,createMessage, createUser);
		userBox.setTranslateX(0);
		userBox.setAlignment(Pos.CENTER);
		userBox.setTranslateY(140);

		//creating the join button that allows a user to join the server
		Button joinButton = new Button("JOIN");
		joinButton.setStyle("-fx-background-radius: 40;");
		joinButton.setPrefWidth(300);
		joinButton.setPrefHeight(60);

		//setting font for text on button
		joinButton.setFont(Font.font("Courier New", FontWeight.BOLD, 30));
		joinButton.setTranslateY(160);
		joinButton.setTranslateX(32);

		//when the joinButton is clicked
		joinButton.setOnAction(e->{
			String tempUserId = createUser.getText(); //grab text from textfield

			clientConnection.profile.setUserId(tempUserId); //set the clients Message object to that username they inputted
			//check if the username exists or not
			try {
				//if they do not input anything, don't accept
				if(tempUserId.equals("")){
					createUser.clear();
				}
				//if the user chooses a username that is longer than 9 characters, the createUser text field will show an error message
				else if (tempUserId.length() > 9){
					createUser.setText("Username is too long (>9)!");
				}
				//else, send it to server to check and see if username already exists
				else{
					clientConnection.send(clientConnection.profile);

				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});

		//vbox that contains vbox with textfield/text and joinbutton
		VBox userNameBox = new VBox(10, userBox, joinButton);
		VBox imageBox = new VBox(10, imageView);
		imageBox.setAlignment(Pos.CENTER);
		imageBox.setTranslateY(-70);
		imageBox.setTranslateX(0);
		userNameBox.setTranslateY(-240);

		//vbox that contains all objects on the screen
		VBox all = new VBox(80, headerBox, imageBox, userNameBox);

		//creating BorderPane to add everything
		BorderPane pane = new BorderPane();
		pane.setCenter(all);
		pane.setPadding(new Insets(30)); //makes sure stuff does not touch the border

		//adding a border color
		BorderWidths newBorderWidth = new BorderWidths(10);
		BorderStroke newBorderStroke = new BorderStroke(Color.LIGHTPINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, newBorderWidth);
		Border newBorder = new Border(newBorderStroke);
		pane.setBorder(newBorder);

		//setting background color of screen
		pane.setStyle("-fx-background-color: rgb(255,225,240);");

     	primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

		//creating scene
		Scene mainScene = new Scene(pane, 450, 700);
		primaryStage.setResizable(false);
		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

	//scene that shows all users currently online and allows you to create a chat, view all chats, and send a chat to all online users
	//parameter:Stage primaryStage
	//return: void
	public void allThreadsScreen(Stage primaryStage) throws Exception {
		AtomicReference<String> userToText = new AtomicReference<>("");

		//creates a header
		Text header = new Text();
		header.setText(clientConnection.profile.getUserId());
		header.setFill(rgb(191,132,234));
		header.setFont(Font.font("Rockwell", FontWeight.BOLD, 50));
		header.setTranslateX(-10);

		//creates drop shadow for header
		DropShadow dropShadow1 = new DropShadow();
		dropShadow1.setOffsetY(3.0f);
		dropShadow1.setColor(Color.color(0.01f, 0.01f, 0.01f));
		header.setEffect(dropShadow1);

		//creating a new header
		Text header2 = new Text();
		header2.setText("USERS ONLINE");
		header2.setFill(rgb(191,132,234));
		header2.setFont(Font.font("Rockwell", FontWeight.BOLD, 20));
		header2.setTranslateX(-10);

		//creating the join button that allows a user to join the server
		Button sendAllButton = new Button("SEND TEXT TO ALL USERS");
		sendAllButton.setStyle("-fx-background-radius: 40;");
		sendAllButton.setPrefWidth(320);
		sendAllButton.setPrefHeight(60);

		//setting font for text on button
		sendAllButton.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
		sendAllButton.setTranslateX(20);
		sendAllButton.setTranslateY(15);

		//creates scroll pane that will show all users currently online
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(listOfUsers);

		scrollPane.getVbarPolicy(); //shows scroll bar
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		scrollPane.getPrefViewportWidth();
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setMinHeight(200);
		scrollPane.setMaxHeight(200);

		//creates the mario star image
		InputStream stream = new FileInputStream("src/Images/mariostar.gif");
		Image createImage = new Image(stream);

		// Create an ImageView with the createImage
		ImageView createChat = new ImageView(createImage); //make image viewable
		createChat.setFitWidth(85);
		createChat.setFitHeight(85);
		createChat.setTranslateX(6);

		//allowing user to select the other users on list
		listOfUsers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		//grabbing whatever string was selected on the list
		listOfUsers.getSelectionModel().selectedItemProperty().addListener((observable, oldUser, newUser) -> {
			userToText.set(newUser); //setting the user selected to a string
		});

		//if the user decides to click the sendAllButton, it will go to the sendToAllScreen
		sendAllButton.setOnAction(d->{
            try {
                sendToAllScreen(primaryStage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

		HBox errorHBox = new HBox();
		errorHBox.setAlignment(Pos.CENTER);
		errorHBox.setTranslateY(50);

		//what happens when create chat image is clicked
		createChat.setOnMouseClicked(e -> {
			try {
				//if the string is not null(nothing selected) or the conversation does not already exist, create chat
				if(String.valueOf(userToText).equals("")){
					errorHBox.getChildren().clear();
					Text errorMessage = new Text("SELECT USER!");
					errorMessage.setFill(rgb(213,19,19));
					errorMessage.setFont(Font.font("Rockwell", FontWeight.BOLD, 20));
					errorHBox.getChildren().add(errorMessage);
				}
				else if(!clientConnection.profile.conversations.contains(String.valueOf(userToText))) {
					errorHBox.getChildren().clear();
					clientConnection.profile.conversations.add(String.valueOf(userToText));

					//create button that will go on viewMessagesScreen screen to display each conversation
					String nameOfUser = "Message with " + userToText;
					Button buttonUser = new Button(nameOfUser);

					clientConnection.profile.receivers.add(String.valueOf(userToText)); //update receivers list to send to certain user
					ObservableList <String> tempList = FXCollections.observableArrayList();
					findConvos.put(buttonUser, tempList); //add button and observable list to map

					//customizing chat button
					buttonUser.setFont(Font.font("Courier New", FontWeight.BOLD, 15));
					buttonUser.setMinWidth(350);
					buttonUser.setMinHeight(60);

					allMessages.getChildren().add(buttonUser); //add convo button to vbox displayed in view messages screen

					clientConnection.profile.setCreateChatFlag(1);
					clientConnection.send(clientConnection.profile); //send new conversations to server to update array on server side
					clientConnection.profile.setCreateChatFlag(0);
					clientConnection.profile.receivers.clear(); //clear receivers list once we are done updating

					viewMessagesScreen(primaryStage);
				}
				//displays an error message if a user is trying to create an existing chat
				else{
					errorHBox.getChildren().clear();
					Text errorMessage = new Text("CHAT ALREADY EXISTS!");
					errorMessage.setFill(rgb(213,19,19));
					errorMessage.setFont(Font.font("Rockwell", FontWeight.BOLD, 20));
					errorHBox.getChildren().add(errorMessage);
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}

		});

		//creating the letter image
		InputStream stream2 = new FileInputStream("src/Images/letterwithstars.gif");
		Image chatImage = new Image(stream2);

		//creates an ImageView with the chatImage
		ImageView viewChats = new ImageView(chatImage);
		viewChats.setFitWidth(140);
		viewChats.setFitHeight(140);
		viewChats.setTranslateY(40);

		//if the user clicks the viewChats button then the program will go to the viewMessagesScreen
		viewChats.setOnMouseClicked(e -> {
			try {
				viewMessagesScreen(primaryStage);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});

		HBox holdButtons = new HBox(100, createChat, viewChats); //holding both buttons
		holdButtons.setAlignment(Pos.BOTTOM_CENTER);
		holdButtons.setTranslateX(22);

		//text that will go with images
		Text createView = new Text("    CREATE CHAT              VIEW CHATS");
		createView.setFill(rgb(191,132,234));
		createView.setFont(Font.font("Rockwell", FontWeight.BOLD, 20));

		//holds both images and text
		VBox imageText = new VBox(15, holdButtons, createView);
		imageText.setTranslateX(-15);
		imageText.setTranslateY(20);

		//holds the second header and scroll pane that displays conversation
		VBox scrollList = new VBox(15,header2,scrollPane);
		scrollList.setAlignment(Pos.CENTER);

		//holds the first header made and the vbox containing the other header and scroll pane
		VBox upperSection = new VBox(30, header, scrollList);
		upperSection.setAlignment(Pos.CENTER);

		//vbox that holds everything on screen
		VBox allBox = new VBox(upperSection, sendAllButton, errorHBox, imageText);

		BorderPane pane = new BorderPane();
		pane.setCenter(allBox);
		pane.setPadding(new Insets(30)); //ensures content does not touch the border

		//adding a border color
		BorderWidths newBorderWidth = new BorderWidths(10);
		BorderStroke newBorderStroke = new BorderStroke(Color.LIGHTPINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, newBorderWidth);
		Border newBorder = new Border(newBorderStroke);
		pane.setBorder(newBorder);

		//setting background color of screen
		pane.setStyle("-fx-background-color: rgb(255,225,240);");

		//creating screen
		Scene allThreadsScene = new Scene(pane, 450, 700);
		primaryStage.setResizable(false); //the user can't resize the screen
		primaryStage.setScene(allThreadsScene);
		primaryStage.show();
	}

	//screen where users can send a message to all other users
	//parameter: Stage primaryStage
	//return: void
	public void sendToAllScreen(Stage primaryStage) throws Exception{
		//textfield that allows user to enter the message they want to send
		TextField sendMessages = new TextField("Type Message...");
		sendMessages.setPrefWidth(400);
		sendMessages.setTranslateY(60);
		sendMessages.setTranslateX(10);
		sendMessages.setFont(Font.font("Courier New", FontWeight.BOLD, 15));

		//button that allows user to send their message
		Button send = new Button("Send");
		send.setFont(Font.font("Courier New", FontWeight.BOLD, 15));
		send.setTranslateY(60);
		send.setPrefHeight(sendMessages.getHeight());
		send.setPrefWidth(200);
		send.setTranslateX(15);

		//hbox that contains textfield and button
		HBox sendHBox = new HBox(20, sendMessages, send);
		sendHBox.setTranslateX(-10);

		//what happens when users pressed the send button
		send.setOnAction(e->{
			//grab text from textfield
			String message = clientConnection.profile.getUserId() +" sent to: " + clientConnection.profile.usernames + ":\n" +sendMessages.getText();
			clientConnection.profile.receivers = clientConnection.profile.usernames; //set receivers list to all users
			clientConnection.profile.setMessage(message); //set message in message object
			clientConnection.profile.setSendAllFlag(1);

			//send message to server
            try {
                clientConnection.send(clientConnection.profile);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
			clientConnection.profile.receivers.clear(); //reset receivers
			sendMessages.clear(); //clear textfield once message is sent
        });
		clientConnection.profile.setSendAllFlag(0);

		//backbutton image
		InputStream stream = new FileInputStream("src/Images/flippedpinkarrow.gif");
		Image backButtonImage = new Image(stream);

		// Create an ImageView with the image
		ImageView backbuttonView = new ImageView(backButtonImage);
		backbuttonView.setFitWidth(50);
		backbuttonView.setFitHeight(50);
		backbuttonView.setTranslateY(-30);
		backbuttonView.setTranslateX(-26);

		//what happens when chatImage is pressed
		backbuttonView.setOnMouseClicked(e -> {
			try {
				allThreadsScreen(primaryStage); //goes back to main screen
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: coral");

		pane.setCenter(sendAllConvos); //add all messages for send all to screen
		pane.setBottom(sendHBox);
		pane.setTop(backbuttonView);
		pane.setStyle("-fx-font-family: 'serif'");
		Scene sendToAllScene =  new Scene(pane, 500, 400);
		primaryStage.setResizable(false); //disable changing the size of window that appears
		primaryStage.setScene(sendToAllScene);
		primaryStage.show();
	}

	//screen that displays all conversations user has
	//parameter: Stage primaryStage
	//return: void
	public void viewMessagesScreen(Stage primaryStage) throws Exception{
		//header to indicate user is looking at all their conversations
		Text header = new Text("MESSAGES");
		header.setFill(rgb(191,132,234));
		header.setFont(Font.font("Rockwell", FontWeight.BOLD, 55));
		header.setTranslateX(-2);

		//backbutton image
		InputStream stream = new FileInputStream("src/Images/flippedpinkarrow.gif");
		Image backButtonImage = new Image(stream);

		// Create an ImageView with the image
		ImageView backbuttonView = new ImageView(backButtonImage);
		backbuttonView.setFitWidth(50);
		backbuttonView.setFitHeight(50);
		backbuttonView.setTranslateY(10);
		backbuttonView.setTranslateX(-26);

		//what happens when chatImage is pressed
		backbuttonView.setOnMouseClicked(e -> {
			try {
				allThreadsScreen(primaryStage); //goes back to main screen
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});

		//looping through all buttons(conversations) on screen to determine which one the user presses
		for (Button tempButton: findConvos.keySet()){
			ObservableList<String> tmpList = findConvos.get(tempButton); //set a temporary list equal to this buttons list to edit it

			//when one of the conversation buttons gets pressed
			tempButton.setOnAction(d->{
				//get name of person that is part of conversation
				String buttonText = tempButton.getText();

				//parsing the string
				int left = buttonText.indexOf("h"); //left index is inclusive
				String parsedString = buttonText.substring(left+2); //start after "Message with" and get rest of string

				//go to messaging screen
				try {
					messagingScreen(primaryStage, tmpList, parsedString);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}

		//hbox that contains top part of screen
		HBox top = new HBox(10,backbuttonView,header);

		//scroll pane that will display and hold all the conversations
		ScrollPane scroll = new ScrollPane();
		scroll.setContent(allMessages); //set content to vbox that holds all the buttons
		scroll.getVbarPolicy(); //shows scroll bar
		scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		scroll.getPrefViewportHeight();
		scroll.getPrefViewportWidth();
		scroll.setFitToWidth(true);
		scroll.setHmax(500);
		scroll.setFitToHeight(true);
		scroll.setTranslateX(10);
		scroll.setStyle("-fx-background: rgb(255,225,240);"); //color of scroll set to same as background color of the screen

		//vbox that holds top of screen and scrollpane
		VBox all = new VBox(30, top, scroll);
		BorderPane pane = new BorderPane();
		pane.setCenter(all);
		pane.setPadding(new Insets(30)); //makes sure stuff does not touch the border

		//adding a border color
		BorderWidths newBorderWidth = new BorderWidths(10);
		BorderStroke newBorderStroke = new BorderStroke(Color.LIGHTPINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, newBorderWidth);
		Border newBorder = new Border(newBorderStroke);
		pane.setBorder(newBorder);
		pane.setStyle("-fx-background-color: rgb(255,225,240);");

		//sets scene
		Scene viewMessagesScene = new Scene(pane,450, 700);
		primaryStage.setResizable(false);
		primaryStage.setScene(viewMessagesScene);
		primaryStage.show();
	}

	//screen that allows users to send messages to each other once they are in their conversation
	//parameters: Stage primaryStage, ObservableList<String> conversation,String receiver
	//return: void
	public void messagingScreen(Stage primaryStage, ObservableList<String> conversation,String receiver) throws Exception{
		clientConnection.profile.receivers.clear(); //clear receivers to make sure they are reset from anything before
		messages.getChildren().clear(); //clear messages shown on the screen to be updated with the correct ones
		clientConnection.profile.receivers.add(receiver); //add the current people in conversation to receivers

		//header to display name of person current user is texting
		Text header = new Text(receiver);
		header.setFill(rgb(191,132,234));
		header.setFont(Font.font("Rockwell", FontWeight.BOLD, 50));
		header.setTranslateX(50);

		//creates shadow for text
		DropShadow dropShadow1 = new DropShadow();
		dropShadow1.setOffsetY(3.0f);
		dropShadow1.setColor(Color.color(0.01f, 0.01f, 0.01f));
		header.setEffect(dropShadow1);

		//back button image
		InputStream stream = new FileInputStream("src/Images/flippedpinkarrow.gif");
		Image backButtonImage = new Image(stream);

		// Create an ImageView with the image
		ImageView backbuttonView = new ImageView(backButtonImage);
		backbuttonView.setFitWidth(50);
		backbuttonView.setFitHeight(50);
		backbuttonView.setTranslateY(10);

		//what happens when back button image is pressed
		backbuttonView.setOnMouseClicked(e -> {
			try {
				viewMessagesScreen(primaryStage);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});

		//textfield that allows user to input message they want to send
		TextField sendMessages = new TextField("Type Message...");
		sendMessages.setPrefWidth(400);
		sendMessages.setTranslateY(60);
		sendMessages.setTranslateX(10);
		sendMessages.setFont(Font.font("Courier New", FontWeight.BOLD, 15));

		//button that allows user to send the message
		Button send = new Button("Send");
		send.setFont(Font.font("Courier New", FontWeight.BOLD, 15));
		send.setTranslateY(60);
		send.setPrefHeight(sendMessages.getHeight());
		send.setPrefWidth(200);
		send.setTranslateX(15);
		HBox sendHBox = new HBox(20, sendMessages, send);

		//what happens when the send button is pressed
		send.setOnAction(e->{
			//format message that will be sent, including who is sending the message
			String messageToSend =clientConnection.profile.getUserId()+":\n" + sendMessages.getText();

			//set all the proper variables in the message class before sending to server
			clientConnection.profile.setMessage(messageToSend);
			clientConnection.profile.setSendMessageFlag(1);
			conversation.add(messageToSend); //add new message to conversation list

			//sends message object to server to send to recipient
			try {
				clientConnection.send(clientConnection.profile);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}

			clientConnection.profile.setSendMessageFlag(0); //reset flag
			sendMessages.clear(); //clear text field
			createChats(messageToSend); //show new messages on screen
		});

		//when user first enters this screen, loop through conversation list and print out all messages that were sent previous
		for (String message: conversation){
			createChats(message);
		}

		//scroll pane to contain messages
		ScrollPane scroll = new ScrollPane();
		scroll.setContent(messages);
		scroll.getVbarPolicy(); //shows scroll bar
		scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		scroll.setMinViewportHeight(450);
		scroll.setMaxHeight(450);
		scroll.setFitToWidth(true);
		scroll.setFitToHeight(true);
		scroll.setTranslateX(10);
		scroll.setStyle("-fx-background: rgb(255,225,240);");

		//holds messages scroll and send features at bottom of screen
		VBox sendVBox = new VBox(scroll, sendHBox);
		//holds top of part of screen
		HBox top = new HBox(10,backbuttonView,header);
		//holds all features on screen
		VBox all = new VBox(10,top, sendVBox);

		BorderPane pane = new BorderPane();
		pane.setCenter(all);
		pane.setPadding(new Insets(30)); //makes sure stuff does not touch the border

		//adding a border color
		BorderWidths newBorderWidth = new BorderWidths(10);
		BorderStroke newBorderStroke = new BorderStroke(Color.LIGHTPINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, newBorderWidth);
		Border newBorder = new Border(newBorderStroke);
		pane.setBorder(newBorder);
		pane.setStyle("-fx-background-color: rgb(255,225,240);");

		Scene messagingScreen = new Scene(pane,450, 700);
		primaryStage.setResizable(false);
		primaryStage.setScene(messagingScreen);
		primaryStage.show();
	}

	//screen that creates the text message flow and updates the vbox containing text messages
	//parameters: String message
	//return: void
	public void createChats(String message){
		TextFlow textMessage = new TextFlow(); //creates the text message bubble
		Text actualText = new Text(message); //sets the text with the message to update to vbox
		actualText.setFont(Font.font("Courier New", FontWeight.BOLD, 15));
		actualText.setFill(Color.WHITE);

		Integer rightIndex = message.indexOf(":"); //gets the index where the colon appears in the message string
		String parse = message.substring(0,rightIndex); //parsing the message to get the user id

		//if the parse string contains the current client's user id, then make the text message bubble light blue
		if (parse.equals(clientConnection.profile.getUserId())){
			textMessage.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(40), null))); // Set background color
			textMessage.setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, new CornerRadii(40), new BorderWidths(3))));
			textMessage.setTextAlignment(TextAlignment.RIGHT); //aligns the text message bubble to the right
			textMessage.setTranslateX(150);
		}
		//if the parse string does not contain the current client's user id (in other words, the receiver), then make the text message light green
		else{
			textMessage.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, new CornerRadii(40), null))); // Set background color
			textMessage.setBorder(new Border(new BorderStroke(Color.LIGHTGREEN, BorderStrokeStyle.SOLID, new CornerRadii(40), new BorderWidths(3))));
			textMessage.setTextAlignment(TextAlignment.LEFT); //aligns the text message bubble to the right
			textMessage.setTranslateX(0);
		}
		textMessage.setPadding(new Insets(10)); //ensures the message does not go outside the text message bubble
		textMessage.setMaxWidth(200);
		textMessage.getChildren().add(actualText);
		textMessage.boundsInLocalProperty();
		messages.getChildren().add(textMessage); //adds the text flow to the messages vbox
	}
}
