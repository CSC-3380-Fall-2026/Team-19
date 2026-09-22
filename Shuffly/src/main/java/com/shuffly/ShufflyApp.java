package com.shuffly;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.shuffly.ui.MainLayout;

public class ShufflyApp extends Application{
	
	@Override
	public void start(Stage primaryStage) {
		MainLayout root = new MainLayout();
		
		Scene scene = new Scene(root, 12000, 800);
		primaryStage.setTitle("Shuffly");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
