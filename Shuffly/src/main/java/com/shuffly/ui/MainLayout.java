package com.shuffly.ui;

import com.shuffly.AppState;
import com.shuffly.LibraryStorage;
import com.shuffly.ui.pages.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import com.shuffly.spotify.SpotifyAuth;

public class MainLayout extends BorderPane {

    private final AppState state = new AppState();
    private final VBox contentArea = new VBox();
    private final Sidebar sidebar;
    private final PlayerBar playerBar;
    private final java.util.Deque<String> backHistory =
            new java.util.ArrayDeque<>();

    private final java.util.Deque<String> forwardHistory =
            new java.util.ArrayDeque<>();

    private String currentPage;

    public MainLayout() {
    	SpotifyAuth.loadSavedTokens();
        contentArea.setStyle("-fx-background-color: #121212;");
        contentArea.setPadding(new javafx.geometry.Insets(40));

        sidebar = new Sidebar(this::showPage, this::goBack, this::goForward);
        playerBar = new PlayerBar(state);

        setLeft(sidebar);
        setCenter(contentArea);
        setBottom(playerBar);
        setStyle("-fx-background-color: #000000;");
        
        try {
            boolean loaded = LibraryStorage.load(state);

            if (loaded) {
                System.out.println("Restored saved Spotify library.");
            }

        } catch (Exception e) {
            System.err.println("Could not load saved library:");
            e.printStackTrace();
        }
        
        showPage("Home");
    }
    
    private void showPage(String pageName) {

        if (pageName == null || pageName.equals(currentPage)) {
            return;
        }

        if (currentPage != null) {
            backHistory.push(currentPage);
        }

        forwardHistory.clear();

        currentPage = pageName;

        renderPage(pageName);
        updateNavigationButtons();
    }
    
    private void goBack() {
        if (backHistory.isEmpty()) {
            return;
        }

        forwardHistory.push(currentPage);
        currentPage = backHistory.pop();

        renderPage(currentPage);
        updateNavigationButtons();
    }

    private void goForward() {
        if (forwardHistory.isEmpty()) {
            return;
        }

        backHistory.push(currentPage);
        currentPage = forwardHistory.pop();

        renderPage(currentPage);
        updateNavigationButtons();
    }
    
    private void updateNavigationButtons() {
        sidebar.setBackDisabled(backHistory.isEmpty());
        sidebar.setForwardDisabled(forwardHistory.isEmpty());
    }

    private void renderPage(String pageName) {
        contentArea.getChildren().clear();

        switch (pageName) {
            case "Home" -> contentArea.getChildren().add(new HomePage(state));
            case "Search" -> contentArea.getChildren().add(
            	    new com.shuffly.ui.pages.SearchPage(state)
            	);
            case "Library" -> contentArea.getChildren().add(
            	    new LibraryPage(
            	        state,
            	        playlist -> {
            	            state.selectedPlaylist = playlist;
            	            showPage("PlaylistDetail");
            	        },
            	        station -> {
            	            state.selectedStation = station;
            	            showPage("StationDetail");
            	        }
            	    )
            	);
            case "PlaylistDetail" -> contentArea.getChildren().add(
	                new PlaylistDetailPage(state, () -> {
	                    showPage("Library");
	                    sidebar.select("Library");
	                })
            );
            case "StationDetail" -> contentArea.getChildren().add(
            	    new StationDetailPage(
            	        state,
            	        () -> {
            	            showPage("Library");
            	            sidebar.select("Library");
            	        },
            	        playerBar::refresh
            	    )
            );
            case "Queue" -> {
                	contentArea.getChildren().add(new QueuePage(state, playerBar::refresh));
            }
            case "Shuffle Mode" -> contentArea.getChildren().add(
            		new ShuffleModePage(state, playerBar::refresh)
            );
            case "Settings" -> contentArea.getChildren().add(
            		new SettingsPage(state, playerBar::refresh)
            );
        }

        if (!pageName.equals("PlaylistDetail")) {
            sidebar.select(pageName);
        }
    }
}