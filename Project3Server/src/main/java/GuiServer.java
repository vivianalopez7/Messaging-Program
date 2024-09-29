/**------------------------------------------
 Project 3: Messaging App
 Course: CS 342, Spring 2024
 System: IntelliJ and Windows 11 and macOS
 Student Author: Dana Fakhreddine and Viviana Lopez
 ---------------------------------------------**/

import java.util.HashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application{
	HashMap<String, Scene> sceneMap;
	Server serverConnection;
	ListView<String> listItems;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//reading in what is being sent to server
		serverConnection = new Server(data -> {
			Platform.runLater(()->{
				listItems.getItems().add(data.toString());
			});
		});

		listItems = new ListView<String>();
		sceneMap = new HashMap<String, Scene>();
		sceneMap.put("server",  createServerGui());

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

		primaryStage.setScene(sceneMap.get("server"));
		primaryStage.setTitle("This is the Server");
		primaryStage.show();
	}

	//creating scene that displays everything being sent/done to the server
	//parameter: none
	//return: Scene
	public Scene createServerGui() {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: coral");

		pane.setCenter(listItems);
		pane.setStyle("-fx-font-family: 'serif'");
		return new Scene(pane, 500, 400);
	}
}
