package com.shuffly.ui;
// Brad Fenasci
import javafx.geometry.Insets;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.shuffly.model.*;
import com.shuffly.QueueManager;
import com.shuffly.ShuffleAlgorithm;
import java.util.ArrayList;
import java.util.List;


public class MainLayout extends BorderPane{
	
	private VBox contentArea;
	
	private Button homeBtn;
	private Button libraryBtn;
	private Button shuffleModeBtn;
	private Button queueBtn;
	private Button settingsBtn;
	
	private Color accentColor = Color.web("#1db954"); // default green
	private javafx.scene.control.ProgressBar progressBar;
	private Button playBtn;
	private Label shuffleLabel;
	
	private final QueueManager<Track> queueManager = new QueueManager<>();
	
	private boolean spotifyConnected = false;
	private String spotifyUserName = null;
	
	public MainLayout() {
		// === SIDEBAR ===
		VBox sidebar = createSidebar();
		
		// === CENTER CONTENT ===
		contentArea = new VBox();
		contentArea.setStyle("-fx-background-color: #121212;");
		contentArea.setPadding(new Insets(40, 40, 40, 40));
		VBox.setVgrow(contentArea, Priority.ALWAYS);

		showContent("Home"); // load the Home page by default
		
		// === PLAYER BAR ===
		HBox playerBar = createPlayerBar();
		
		//Put everything together
		this.setLeft(sidebar);
		this.setCenter(contentArea);
		this.setBottom(playerBar);
		
		this.setStyle("-fx-background-color: #000000;");
	}
	
	private VBox createSidebar() {
	    VBox sidebar = new VBox(8);
	    sidebar.setPrefWidth(240);
	    sidebar.setStyle("-fx-background-color: #000000;");
	    sidebar.setPadding(new Insets(24, 12, 24, 12));

	    // Logo
	    Label logo = new Label("Shuffly");
	    logo.setTextFill(Color.WHITE);
	    logo.setFont(Font.font("System", FontWeight.BOLD, 26));
	    logo.setPadding(new Insets(0, 0, 28, 12));

	    // Navigation buttons
	    homeBtn = createSidebarButton("Home");
	    libraryBtn = createSidebarButton("Library");
	    shuffleModeBtn = createSidebarButton("Shuffle Mode");
	    queueBtn = createSidebarButton("Queue");
	    settingsBtn = createSidebarButton("Settings");

	    // Default selected
	    setSelectedButton(homeBtn);

	    // Actions
	    homeBtn.setOnAction(e -> {
	        showContent("Home");
	        setSelectedButton(homeBtn);
	    });
	    libraryBtn.setOnAction(e -> {
	        showContent("Library");
	        setSelectedButton(libraryBtn);
	    });
	    shuffleModeBtn.setOnAction(e -> {
	        showContent("Shuffle Mode");
	        setSelectedButton(shuffleModeBtn);
	    });
	    queueBtn.setOnAction(e -> {
	        showContent("Queue");
	        setSelectedButton(queueBtn);
	    });
	    settingsBtn.setOnAction(e -> {
	        showContent("Settings");
	        setSelectedButton(settingsBtn);
	    });

	    // Push Settings to the bottom
	    Region spacer = new Region();
	    VBox.setVgrow(spacer, Priority.ALWAYS);

	    sidebar.getChildren().addAll(
	        logo,
	        homeBtn,
	        libraryBtn,
	        shuffleModeBtn,
	        queueBtn,
	        spacer,
	        settingsBtn
	    );

	    return sidebar;
	}
	
	private Button createSidebarButton(String text) {
	    Button btn = new Button(text);
	    btn.setMaxWidth(Double.MAX_VALUE);
	    btn.setAlignment(Pos.CENTER_LEFT);
	    
	    String baseStyle = 
	        "-fx-background-color: transparent;" +
	        "-fx-text-fill: #b3b3b3;" +
	        "-fx-font-size: 15px;" +
	        "-fx-font-weight: bold;" +
	        "-fx-padding: 12 16;" +
	        "-fx-background-radius: 6px;" +
	        "-fx-cursor: hand;" +
	        "-fx-focus-color: transparent;" +
	        "-fx-faint-focus-color: transparent;";

	    btn.setStyle(baseStyle);

	    // Hover
	    btn.setOnMouseEntered(e -> {
	        if (!btn.getStyle().contains("#282828")) { // only if not selected
	            btn.setStyle(
	                "-fx-background-color: #181818;" +
	                "-fx-text-fill: white;" +
	                "-fx-font-size: 15px;" +
	                "-fx-font-weight: bold;" +
	                "-fx-padding: 12 16;" +
	                "-fx-background-radius: 6px;" +
	                "-fx-cursor: hand;" +
	                "-fx-focus-color: transparent;" +
	                "-fx-faint-focus-color: transparent;"
	            );
	        }
	    });

	    btn.setOnMouseExited(e -> {
	        if (!btn.getStyle().contains("#282828")) {
	            btn.setStyle(baseStyle);
	        }
	    });

	    return btn;
	}
	
	private HBox createPlayerBar() {
	    HBox playerBar = new HBox(20);
	    playerBar.setPrefHeight(90);
	    playerBar.setStyle("-fx-background-color: #181818; -fx-border-color: #282828; -fx-border-width: 1 0 0 0;");
	    playerBar.setAlignment(Pos.CENTER_LEFT);
	    playerBar.setPadding(new Insets(0, 20, 0, 20));

	    // === LEFT: Album Art + Song Info ===
	    HBox leftSection = new HBox(15);
	    leftSection.setAlignment(Pos.CENTER_LEFT);
	    leftSection.setPrefWidth(320);

	    Label albumArt = new Label("♪");
	    albumArt.setPrefSize(56, 56);
	    albumArt.setAlignment(Pos.CENTER);
	    albumArt.setStyle(
	        "-fx-background-color: #333333;" +
	        "-fx-text-fill: #b3b3b3;" +
	        "-fx-font-size: 24px;" +
	        "-fx-background-radius: 4px;"
	    );

	    VBox songInfo = new VBox(3);
	    Label songTitle = new Label("No song playing");
	    songTitle.setTextFill(Color.WHITE);
	    songTitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));

	    Label artistName = new Label("—");
	    artistName.setTextFill(Color.web("#b3b3b3"));
	    artistName.setFont(Font.font(12));

	    songInfo.getChildren().addAll(songTitle, artistName);
	    leftSection.getChildren().addAll(albumArt, songInfo);

	    // === CENTER: Controls + Progress ===
	    VBox centerSection = new VBox(8);
	    centerSection.setAlignment(Pos.CENTER);
	    HBox.setHgrow(centerSection, Priority.ALWAYS);

	    // Playback buttons
	    HBox controls = new HBox(20);
	    controls.setAlignment(Pos.CENTER);

	    Button prevBtn = createControlButton("⏮");
	    playBtn = createPlayButton();
	    Button nextBtn = createControlButton("⏭");

	    controls.getChildren().addAll(prevBtn, playBtn, nextBtn);

	    // Progress row
	    HBox progressRow = new HBox(10);
	    progressRow.setAlignment(Pos.CENTER);

	    Label currentTime = new Label("0:00");
	    currentTime.setTextFill(Color.web("#b3b3b3"));
	    currentTime.setFont(Font.font(11));
	    currentTime.setMinWidth(35);

	    // Simple progress slider (no external CSS needed)
	    Slider progressSlider = new Slider(0, 100, 30);
	    progressSlider.setPrefWidth(400);
	    progressSlider.setStyle(
	        "-fx-control-inner-background: #4d4d4d;"
	    );

	    Label totalTime = new Label("3:45");
	    totalTime.setTextFill(Color.web("#b3b3b3"));
	    totalTime.setFont(Font.font(11));
	    totalTime.setMinWidth(35);

	    progressRow.getChildren().addAll(currentTime, progressSlider, totalTime);
	    centerSection.getChildren().addAll(controls, progressRow);

	    // === RIGHT: Shuffle Mode ===
	    HBox rightSection = new HBox();
	    rightSection.setAlignment(Pos.CENTER_RIGHT);
	    rightSection.setPrefWidth(220);

	    shuffleLabel = new Label("Shuffle: Mood-based");
	    shuffleLabel.setTextFill(Color.web("#1db954"));
	    shuffleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
	    shuffleLabel.setStyle("-fx-padding: 6 12; -fx-background-color: #282828; -fx-background-radius: 20;");

	    rightSection.getChildren().add(shuffleLabel);

	    // Put everything together
	    playerBar.getChildren().addAll(leftSection, centerSection, rightSection);
	    return playerBar;
	}
	
	private void updatePlayerBar() {
	    Track current = queueManager.getCurrent();
	    // Later update the actual labels in the player bar here
	    // For now we just make sure the method exists
	    if (current != null) {
	        System.out.println("Now playing: " + current);
	    }
	}
	
	private Button createPlayButton() {
	    Button playBtn = new Button("▶");
	    
	    String baseStyle = 
	        "-fx-background-color: white;" +
	        "-fx-text-fill: black;" +
	        "-fx-font-size: 18px;" +
	        "-fx-cursor: hand;" +
	        "-fx-background-radius: 50%;" +          // makes it a circle
	        "-fx-min-width: 48px;" +
	        "-fx-min-height: 48px;" +
	        "-fx-max-width: 48px;" +
	        "-fx-max-height: 48px;" +
	        "-fx-focus-color: transparent;" +
	        "-fx-faint-focus-color: transparent;" +
	        "-fx-background-insets: 0;" +
	        "-fx-border-color: transparent;";

	    playBtn.setStyle(baseStyle);

	    // Hover effect
	    playBtn.setOnMouseEntered(e -> {
	        playBtn.setStyle(
	        	"-fx-background-color: #" + accentColor.toString().substring(2, 8) + ";" +
	            "-fx-text-fill: black;" +
	            "-fx-font-size: 18px;" +
	            "-fx-cursor: hand;" +
	            "-fx-background-radius: 50%;" +
	            "-fx-min-width: 48px;" +
	            "-fx-min-height: 48px;" +
	            "-fx-max-width: 48px;" +
	            "-fx-max-height: 48px;" +
	            "-fx-focus-color: transparent;" +
	            "-fx-faint-focus-color: transparent;" +
	            "-fx-background-insets: 0;" +
	            "-fx-border-color: transparent;"
	        );
	    });

	    playBtn.setOnMouseExited(e -> {
	        playBtn.setStyle(baseStyle);
	        playBtn.getParent().requestFocus();
	    });

	    playBtn.setOnAction(e -> playBtn.getParent().requestFocus());

	    return playBtn;
	}
	
	private Button createControlButton(String text) {
	    Button btn = new Button(text);
	    
	    String baseStyle = 
	        "-fx-background-color: transparent;" +
	        "-fx-text-fill: #b3b3b3;" +
	        "-fx-font-size: 16px;" +
	        "-fx-cursor: hand;" +
	        "-fx-padding: 8;" +
	        "-fx-focus-color: transparent;" +
	        "-fx-faint-focus-color: transparent;" +
	        "-fx-background-insets: 0;" +
	        "-fx-border-color: transparent;";

	    btn.setStyle(baseStyle);

	    // Hover effect
	    btn.setOnMouseEntered(e -> {
	        btn.setStyle(
	            "-fx-background-color: transparent;" +
	            "-fx-text-fill: white;" +
	            "-fx-font-size: 16px;" +
	            "-fx-cursor: hand;" +
	            "-fx-padding: 8;" +
	            "-fx-focus-color: transparent;" +
	            "-fx-faint-focus-color: transparent;" +
	            "-fx-background-insets: 0;" +
	            "-fx-border-color: transparent;"
	        );
	    });

	    btn.setOnMouseExited(e -> {
	        btn.setStyle(baseStyle);
	        // Force remove focus so the outline disappears
	        btn.getParent().requestFocus();
	    });

	    // Also remove focus when clicked
	    btn.setOnAction(e -> btn.getParent().requestFocus());

	    return btn;
	}
	
	private void showContent(String pageName) {
	    contentArea.getChildren().clear();

	    Label title = new Label(pageName);
	    title.setTextFill(Color.WHITE);
	    title.setFont(Font.font("System", FontWeight.BOLD, 32));
	    title.setPadding(new Insets(0, 0, 8, 0));

	    Label subtitle = new Label(getSubtitleForPage(pageName));
	    subtitle.setTextFill(Color.web("#b3b3b3"));
	    subtitle.setFont(Font.font(14));
	    subtitle.setPadding(new Insets(0, 0, 30, 0));

	    contentArea.getChildren().addAll(title, subtitle);

	    switch (pageName) {
	        case "Library":
	            contentArea.getChildren().add(createLibraryPage());
	            break;
	        case "Shuffle Mode":
	            contentArea.getChildren().add(createShuffleModePage());
	            break;
	        case "Settings":
	            contentArea.getChildren().add(createAppearanceSection());
	            break;
	        case "Queue":
	            contentArea.getChildren().add(createQueuePage());
	            break;
	        case "Home":
	            contentArea.getChildren().add(createHomePage());
	            break;
	    }
	}
	
	private VBox createHomePage() {
	    VBox page = new VBox(25);
	    page.setPadding(new Insets(10, 0, 20, 0));

	    // Welcome text
	    Label welcome = new Label("Welcome to Shuffly");
	    welcome.setTextFill(Color.WHITE);
	    welcome.setFont(Font.font("System", FontWeight.BOLD, 28));

	    Label subtitle = new Label("Connect your Spotify account to start using smarter shuffle modes");
	    subtitle.setTextFill(Color.web("#b3b3b3"));
	    subtitle.setFont(Font.font(15));
	    subtitle.setPadding(new Insets(0, 0, 10, 0));

	    // Connection card
	    VBox connectionCard = new VBox(18);
	    connectionCard.setPadding(new Insets(28));
	    connectionCard.setStyle(
	        "-fx-background-color: #181818;" +
	        "-fx-background-radius: 12px;"
	    );
	    connectionCard.setMaxWidth(480);

	    Label statusLabel = new Label();
	    statusLabel.setFont(Font.font(15));

	    Button connectBtn = new Button();
	    connectBtn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
	    connectBtn.setCursor(javafx.scene.Cursor.HAND);
	    connectBtn.setStyle(
	        "-fx-background-radius: 20px;" +
	        "-fx-padding: 12 28;"
	    );

	    // Update UI based on connection state
	    updateConnectionUI(statusLabel, connectBtn);

	    // Temporary click handler (replace this with real OAuth)
	    connectBtn.setOnAction(e -> {
	        if (!spotifyConnected) {
	            // Simulate successful connection for now
	            spotifyConnected = true;
	            spotifyUserName = "Your Name"; // later this comes from Spotify
	            updateConnectionUI(statusLabel, connectBtn);
	        } else {
	            // Disconnect
	            spotifyConnected = false;
	            spotifyUserName = null;
	            updateConnectionUI(statusLabel, connectBtn);
	        }
	    });

	    connectionCard.getChildren().addAll(statusLabel, connectBtn);
	    page.getChildren().addAll(welcome, subtitle, connectionCard);

	    return page;
	}

	private void updateConnectionUI(Label statusLabel, Button connectBtn) {
	    if (spotifyConnected) {
	        statusLabel.setText("Connected as " + spotifyUserName);
	        statusLabel.setTextFill(Color.web("#1db954"));

	        connectBtn.setText("Disconnect");
	        connectBtn.setStyle(
	            "-fx-background-color: #282828;" +
	            "-fx-text-fill: white;" +
	            "-fx-background-radius: 20px;" +
	            "-fx-padding: 12 28;" +
	            "-fx-cursor: hand;"
	        );
	    } else {
	        statusLabel.setText("Not connected to Spotify");
	        statusLabel.setTextFill(Color.web("#b3b3b3"));

	        connectBtn.setText("Connect to Spotify");
	        connectBtn.setStyle(
	            "-fx-background-color: #1db954;" +
	            "-fx-text-fill: black;" +
	            "-fx-background-radius: 20px;" +
	            "-fx-padding: 12 28;" +
	            "-fx-cursor: hand;"
	        );
	    }
	}
	
	private VBox createLibraryPage() {
	    // Temporary mock data 
	    List<Playlist> playlists = createMockPlaylists();
	    List<Station> stations = createMockStations();

	    return buildLibraryPage(playlists, stations);
	}
	
	private VBox buildLibraryPage(List<Playlist> playlists, List<Station> stations) {
	    VBox page = new VBox(35);
	    page.setPadding(new Insets(10, 0, 20, 0));

	    // Playlists section
	    VBox playlistsSection = new VBox(16);
	    
	    Label playlistsTitle = new Label("Playlists");
	    playlistsTitle.setTextFill(Color.WHITE);
	    playlistsTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
	    
	    // Horizontal Row of playlist cards
	    HBox playlistCards = new HBox(18);
	    for (Playlist playlist : playlists) {
	        playlistCards.getChildren().add(createPlaylistCard(playlist));
	    }

	    playlistsSection.getChildren().addAll(playlistsTitle, playlistCards);
	    
	    // Stations section
	    VBox stationsSection = new VBox(16);
	    
	    Label stationsTitle = new Label("Stations");
	    stationsTitle.setTextFill(Color.WHITE);
	    stationsTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
	    
	    HBox stationCards = new HBox(18);
	    for (Station station : stations) {
	        stationCards.getChildren().add(createStationCard(station));
	    }
	    
	    stationsSection.getChildren().addAll(stationsTitle, stationCards);

	    page.getChildren().addAll(playlistsSection, stationsSection);
	    return page;
	}

	private VBox createPlaylistCard(Playlist playlist) {
		VBox card = new VBox(10);
		card.setPrefWidth(160);
		card.setCursor(javafx.scene.Cursor.HAND);
		
		// Cover art placeholder
		Label cover = new Label("♪");
		cover.setPrefSize(160, 160);
		cover.setAlignment(Pos.CENTER);
		cover.setStyle(
				"-fx-background-color: #282828;" +
				"-fx-text-fill: #b3b3b3;" +
				"-fx-font-size: 42px;" +
				"-fx-background-radius: 8px;"
		);
		
		Label name = new Label(playlist.getName());
		name.setTextFill(Color.WHITE);
		name.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
		
		Label info = new Label(playlist.getTrackCount() + " songs");
		info.setTextFill(Color.WHITE);
		info.setFont(Font.font(12));
		
		card.getChildren().addAll(cover, name, info);
		
		//Hover effect
		card.setOnMouseEntered(e -> {
			cover.setStyle(
				"-fx-background-color: #333333;" +
				"-fx-text-fill: #b3b3b3;" +
				"-fx-font-size: 42px;" +
				"-fx-background-radius: 8px;"
			);
		});
		card.setOnMouseExited(e -> {
			cover.setStyle(
				"-fx-background-color: #282828;" +
				"-fx-text-fill: #b3b3b3;" +
				"-fx-font-size: 42px;" +
				"-fx-background-radius: 8px;"
			);
		});
		// When the user clicks a playlist → load it into the QueueManager
	    card.setOnMouseClicked(e -> {
	        queueManager.loadPlaylist(playlist.getTracks(), true); // true = shuffle
	        showContent("Queue");          // jump to Queue page
	        setSelectedButton(queueBtn);
	        updatePlayerBar();             // refresh song info
	    });

		return card;
	}
	
	private VBox createStationCard(Station station) {
		VBox card = new VBox(10);
		card.setPrefWidth(180);
		card.setCursor(javafx.scene.Cursor.HAND);
		
		// Station cover
		Label cover = new Label("◉");
		cover.setPrefSize(180,  120);
		cover.setAlignment(Pos.CENTER);
		cover.setStyle(
				"-fx-background-color: #1e1e2f;" +
				"-fx-text-fill: #1db954;" +
				"-fx-font-size: 36px;" +
				"-fx-background-radius: 8px;"
		);
		
		Label name = new Label(station.getName());
		name.setTextFill(Color.WHITE);
		name.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
		
		Label info = new Label(station.getDescription());
		info.setTextFill(Color.web("#b3b3b3"));
		info.setFont(Font.font(12));
		info.setWrapText(true);
		info.setMaxWidth(180);
		
		card.getChildren().addAll(cover, name, info);
		
		// Hover effect
		card.setOnMouseEntered(e -> {
			cover.setStyle(
				"-fx-background-color: #2a2a3d;" +
				"-fx-text-fill: #1db954;" +
				"-fx-font-size: 36px;" +
				"-fx-background-radius: 8px;"
			);	
		});
		card.setOnMouseExited(e ->{
			cover.setStyle(
				"-fx-background-color: #1e1e2f;" +
				"-fx-text-fill: #1db954;" +
				"-fx-font-size: 36px;" +
				"-fx-background-radius: 8px;"
			);
		});
		return card;
	}
	
	private VBox createQueuePage() {
	    VBox page = new VBox(20);
	    page.setPadding(new Insets(10, 0, 20, 0));

	    // Current song
	    Track current = queueManager.getCurrent();
	    Label nowPlaying = new Label(current != null ? "Now Playing: " + current : "Nothing playing");
	    nowPlaying.setTextFill(Color.WHITE);
	    nowPlaying.setFont(Font.font("System", FontWeight.BOLD, 18));

	    // Up Next
	    Label upNextTitle = new Label("Up Next");
	    upNextTitle.setTextFill(Color.WHITE);
	    upNextTitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
	    upNextTitle.setPadding(new Insets(20, 0, 8, 0));

	    VBox upNextList = new VBox(8);
	    List<Track> upNext = queueManager.getUpNext();

	    if (upNext.isEmpty()) {
	        Label empty = new Label("Queue is empty");
	        empty.setTextFill(Color.web("#b3b3b3"));
	        upNextList.getChildren().add(empty);
	    } else {
	        for (Track track : upNext) {
	            Label row = new Label(track.getName() + "  –  " + track.getArtist());
	            row.setTextFill(Color.web("#b3b3b3"));
	            row.setFont(Font.font(14));
	            upNextList.getChildren().add(row);
	        }
	    }

	    page.getChildren().addAll(nowPlaying, upNextTitle, upNextList);
	    return page;
	}

	private VBox createShuffleModePage() {
	    VBox page = new VBox(20);

	    Label info = new Label("Choose how Shuffly orders your music");
	    info.setTextFill(Color.web("#b3b3b3"));
	    info.setFont(Font.font(14));

	    // Shuffle mode options
	    String[] modes = {
	        "Mood-based",
	        "Truly Random",
	        "Anti-Popularity",
	        "Artist Spacing",
	        "Discovery"
	    };

	    VBox options = new VBox(10);

	    for (String mode : modes) {
	        Button modeBtn = new Button(mode);
	        modeBtn.setMaxWidth(Double.MAX_VALUE);
	        modeBtn.setAlignment(Pos.CENTER_LEFT);
	        modeBtn.setStyle(
	            "-fx-background-color: #181818;" +
	            "-fx-text-fill: white;" +
	            "-fx-font-size: 15px;" +
	            "-fx-padding: 14 18;" +
	            "-fx-background-radius: 8px;" +
	            "-fx-cursor: hand;"
	        );

	        modeBtn.setOnAction(e -> {
	            // Update the player bar pill
	            if (shuffleLabel != null) {
	                shuffleLabel.setText("Shuffle: " + mode);
	            }
	        });

	        // Hover effect
	        modeBtn.setOnMouseEntered(e -> modeBtn.setStyle(
	            "-fx-background-color: #282828;" +
	            "-fx-text-fill: white;" +
	            "-fx-font-size: 15px;" +
	            "-fx-padding: 14 18;" +
	            "-fx-background-radius: 8px;" +
	            "-fx-cursor: hand;"
	        ));
	        modeBtn.setOnMouseExited(e -> modeBtn.setStyle(
	            "-fx-background-color: #181818;" +
	            "-fx-text-fill: white;" +
	            "-fx-font-size: 15px;" +
	            "-fx-padding: 14 18;" +
	            "-fx-background-radius: 8px;" +
	            "-fx-cursor: hand;"
	        ));

	        options.getChildren().add(modeBtn);
	    }

	    page.getChildren().addAll(info, options);
	    return page;
	}

	private String getSubtitleForPage(String pageName) {
	    switch (pageName) {
	        case "Home": return "Your personalized listening experience";
	        case "Library": return "Playlists and Stations in one place";
	        case "Shuffle Mode": return "Control how your music is ordered";
	        case "Queue": return "Up next and recently played";
	        case "Settings": return "Appearance and preferences";
	        default: return "";
	    }
	}
	
	private void setSelectedButton(Button selected) {
	    Button[] allButtons = {homeBtn, libraryBtn, shuffleModeBtn, queueBtn, settingsBtn};

	    String baseStyle =
	        "-fx-background-color: transparent;" +
	        "-fx-text-fill: #b3b3b3;" +
	        "-fx-font-size: 15px;" +
	        "-fx-font-weight: bold;" +
	        "-fx-padding: 12 16;" +
	        "-fx-background-radius: 6px;" +
	        "-fx-cursor: hand;" +
	        "-fx-focus-color: transparent;" +
	        "-fx-faint-focus-color: transparent;";

	    String selectedStyle =
	        "-fx-background-color: #282828;" +
	        "-fx-text-fill: white;" +
	        "-fx-font-size: 15px;" +
	        "-fx-font-weight: bold;" +
	        "-fx-padding: 12 16;" +
	        "-fx-background-radius: 6px;" +
	        "-fx-cursor: hand;" +
	        "-fx-focus-color: transparent;" +
	        "-fx-faint-focus-color: transparent;";

	    for (Button btn : allButtons) {
	        if (btn != null) btn.setStyle(baseStyle);
	    }
	    selected.setStyle(selectedStyle);
	}
	
	private VBox createAppearanceSection() {
	    VBox section = new VBox(20);
	    section.setPadding(new Insets(10, 0, 0, 0));

	    Label sectionTitle = new Label("Accent Color");
	    sectionTitle.setTextFill(Color.WHITE);
	    sectionTitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 18));

	    HBox colorOptions = new HBox(15);
	    colorOptions.setAlignment(Pos.CENTER_LEFT);

	    // Predefined accent colors
	    String[] colors = {
	        "#1db954", // Spotify green
	        "#3b82f6", // Blue
	        "#a855f7", // Purple
	        "#ec4899", // Pink
	        "#f59e0b", // Amber
	        "#ef4444"  // Red
	    };

	    for (String hex : colors) {
	        Button colorBtn = new Button();
	        colorBtn.setPrefSize(36, 36);
	        colorBtn.setStyle(
	            "-fx-background-color: " + hex + ";" +
	            "-fx-background-radius: 50%;" +
	            "-fx-cursor: hand;" +
	            "-fx-border-color: " + (accentColor.toString().contains(hex.substring(1)) ? "white" : "transparent") + ";" +
	            "-fx-border-width: 2;" +
	            "-fx-border-radius: 50%;"
	        );

	        colorBtn.setOnAction(e -> applyAccentColor(Color.web(hex)));
	        colorOptions.getChildren().add(colorBtn);
	    }

	    section.getChildren().addAll(sectionTitle, colorOptions);
	    return section;
	}

	private void applyAccentColor(Color newColor) {
	    this.accentColor = newColor;
	    String hex = colorToHex(newColor);

	    // Update progress bar
	    if (progressBar != null) {
	        progressBar.setStyle("-fx-accent: " + hex + ";");
	    }

	    // Update shuffle label
	    if (shuffleLabel != null) {
	        shuffleLabel.setTextFill(newColor);
	    }

	    // Update play button base style
	    if (playBtn != null) {
	        // Force refresh of play button style
	        playBtn.setStyle(playBtn.getStyle().replaceAll("-fx-background-color: #[0-9a-fA-F]{6};", "-fx-background-color: white;"));
	    }

	    // Refresh settings page so the selected ring updates
	    showContent("Settings");
	    setSelectedButton(settingsBtn);
	}

	private String colorToHex(Color color) {
	    return String.format("#%02X%02X%02X",
	        (int) (color.getRed() * 255),
	        (int) (color.getGreen() * 255),
	        (int) (color.getBlue() * 255));
	}
	
	private List<Playlist> createMockPlaylists() {
	    List<Playlist> list = new ArrayList<>();

	    Playlist liked = new Playlist("1", "Liked Songs", null);
	    liked.addTrack(new Track("t1", "Song A", "Artist A", null, 200000));
	    liked.addTrack(new Track("t2", "Song B", "Artist B", null, 180000));

	    list.add(liked);
	    list.add(new Playlist("2", "Chill Mix", null));
	    list.add(new Playlist("3", "Focus Flow", null));
	    list.add(new Playlist("4", "Late Night", null));
	    list.add(new Playlist("5", "Workout", null));

	    return list;
	}

	private List<Station> createMockStations() {
	    List<Station> list = new ArrayList<>();
	    list.add(new Station("s1", "Mood: Chill", "Based on your calm listening", null));
	    list.add(new Station("s2", "Artist Radio", "Similar to your top artists", null));
	    list.add(new Station("s3", "Discovery", "New music you might like", null));
	    list.add(new Station("s4", "Energy Boost", "High energy tracks", null));
	    return list;
	}
}
