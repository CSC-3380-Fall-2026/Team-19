package com.shuffly.ui;

import com.shuffly.AppState;
import com.shuffly.ui.pages.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainLayout extends BorderPane {

    private final AppState state = new AppState();
    private final VBox contentArea = new VBox();
    private final Sidebar sidebar;
    private final PlayerBar playerBar;

    public MainLayout() {
        contentArea.setStyle("-fx-background-color: #121212;");
        contentArea.setPadding(new javafx.geometry.Insets(40));

        sidebar = new Sidebar(this::showPage);
        playerBar = new PlayerBar(state);

        setLeft(sidebar);
        setCenter(contentArea);
        setBottom(playerBar);
        setStyle("-fx-background-color: #000000;");

        showPage("Home");
    }

    private void showPage(String pageName) {
        contentArea.getChildren().clear();

        switch (pageName) {
            case "Home" -> contentArea.getChildren().add(new HomePage(state));
            case "Library" -> contentArea.getChildren().add(
                new LibraryPage(state, () -> {
                    showPage("Queue");
                    sidebar.select("Queue");
                    playerBar.refresh();
                })
            );
            case "Queue" -> contentArea.getChildren().add(new QueuePage(state));
            case "Shuffle Mode" -> contentArea.getChildren().add(
                new ShuffleModePage(state, playerBar::refresh)
            );
            case "Settings" -> contentArea.getChildren().add(
                new SettingsPage(state, playerBar::refresh)
            );
        }

        sidebar.select(pageName);
    }
}