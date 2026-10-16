package com.shuffly;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.shuffly.ui.MainLayout;

public class ShufflyApp extends Application{
	
	@Override
	public void start(Stage primaryStage) {
	    MainLayout root = new MainLayout();

	    Scene scene = new Scene(root, 1280, 800);
	    
	    scene.getRoot().setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
	    //scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
	    
	    primaryStage.setTitle("Shuffly");
	    primaryStage.setScene(scene);
	    
	    // Important window settings
	    primaryStage.setMinWidth(1000);
	    primaryStage.setMinHeight(650);
	    primaryStage.setResizable(true);
	    
	    // This helps prevent the window from disappearing
	    primaryStage.setIconified(false);
	    primaryStage.setMaximized(false);
	    
	    primaryStage.show();
	    
	    // Force the window to come to the front when it starts
	    primaryStage.toFront();
	    primaryStage.requestFocus();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
