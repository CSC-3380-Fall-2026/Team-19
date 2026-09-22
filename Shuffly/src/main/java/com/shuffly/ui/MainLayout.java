package com.shuffly.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MainLayout extends BorderPane{
	
	private VBox contentArea;
	
	public MainLayout() {
		// === SIDEBAR ===
		VBox sidebar = createSidebar();
		
		// === CENTER CONTENT ===
		contentArea = new VBox();
		contentArea.setStyle("-fx-background-color: #121212;");
		contentArea.setAlignment(Pos.CENTER);
		contentArea.setPadding(new Insets(30));
		Label welcome = new Label("Welcome to Shuffly");
		welcome.setTextFill(Color.WHITE);
		welcome.setFont(Font.font("System", FontWeight.BOLD, 28));
		contentArea.getChildren().add(welcome);
		
		// === PLAYER BAR ===
		HBox playerBar = createPlayerBar();
		
		//Put everything together
		this.setLeft(sidebar);
		this.setCenter(contentArea);
		this.setBottom(playerBar);
		
		this.setStyle("-fx-background-color: #000000;");
	}
	
	private VBox createSidebar() {
		VBox sidebar = new VBox(15);
		sidebar.setPrefWidth(230);
		sidebar.setStyle("-fx-background-color: #1e1e2f;");
		sidebar.setPadding(new Insets(25, 15, 25, 15));
		
		Label logo = new Label("Shuffly");
		logo.setTextFill(Color.WHITE);
		logo.setFont(Font.font("System", FontWeight.BOLD, 24));
		logo.setPadding(new Insets(0, 0, 20, 10));
		
		Button homeBtn = createSidebarButton("Home");
		Button libraryBtn = createSidebarButton("Library");
		Button playlistsBtn = createSidebarButton("Playlists");
		Button stationsBtn = createSidebarButton("Stations");
		Button queueBtn = createSidebarButton("Queue");
		
		// Simple click actions for now
		homeBtn.setOnAction(e -> showContent("Home"));
		libraryBtn.setOnAction(e -> showContent("Library"));
		playlistsBtn.setOnAction(e -> showContent("Playlists"));
		stationsBtn.setOnAction(e -> showContent("Stations"));
		queueBtn.setOnAction(e -> showContent("Queue"));
		
		sidebar.getChildren().addAll(logo, homeBtn, libraryBtn, playlistsBtn, stationsBtn, queueBtn);
		return sidebar;
	}
	
	private Button createSidebarButton(String text) {
		Button btn = new Button(text);
		btn.setMaxWidth(Double.MAX_VALUE);
		btn.setAlignment(Pos.CENTER_LEFT);
		btn.setStyle(
				"-fx-background-color: transparent;" +
				"-fx-text-fill: #b3b3b3;" +
				"-fx-font-size: 15px;" +
				"-fx-padding: 10 15;" +
				"-fx-cursor: hand;"
		);
		
		//Hover effect
		btn.setOnMouseEntered(e -> btn.setStyle(
				"-fx-background-color: #2a2a3d;" +
				"-fx-text-fill: white;" +
				"-fx-font-size: 15px;" +
				"-fx-padding: 10 15;" +
				"-fx-cursor: hand;"
		));
		btn.setOnMouseExited(e -> btn.setStyle(
				"-fx-background-color: transparent;" +
				"-fx-text-fill: #b3b3b3;" +
				"-fx-font-size: 15px;" +
				"-fx-padding: 10 15;" +
				"-fx-cursor: hand;"
		));
		
		return btn;
	}
	
	private HBox createPlayerBar() {
		HBox playerBar = new HBox(20);
		playerBar.setPrefHeight(90);
		playerBar.setStyle("-fx-background-color: #181818;");
		playerBar.setAlignment(Pos.CENTER_LEFT);
		playerBar.setPadding(new Insets(0, 25,9,25));
		
		Label songInfo = new Label("No song playing");
		songInfo.setTextFill(Color.WHITE);
		songInfo.setFont(Font.font(14));
		
		Label controls = new Label("⏮   ▶   ⏭");
		controls.setTextFill(Color.WHITE);
		controls.setFont(Font.font(18));
		
		Label shuffleMode = new Label("Shuffle: Mood");
		shuffleMode.setTextFill(Color.LIGHTGREEN);
		shuffleMode.setFont(Font.font(13));
		
		// Push the shuffle mode to the right
		HBox spacer = new HBox();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		
		playerBar.getChildren().addAll(songInfo, controls, spacer, shuffleMode);
		return playerBar;
	}
	
	private void showContent(String pageName) {
		contentArea.getChildren().clear();
		
		Label title = new Label(pageName);
		title.setTextFill(Color.WHITE);
		title.setFont(Font.font("System", FontWeight.BOLD, 28));
		
		contentArea.getChildren().add(title);
	}
}
