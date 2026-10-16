package com.shuffly.ui;

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
	        default:
	            // Placeholder for Home / Queue
	            VBox placeholder = new VBox(12);
	            placeholder.setAlignment(Pos.CENTER);
	            placeholder.setPadding(new Insets(60, 20, 60, 20));
	            placeholder.setStyle("-fx-background-color: #181818; -fx-background-radius: 12px;");
	            VBox.setVgrow(placeholder, Priority.ALWAYS);

	            Label icon = new Label("♪");
	            icon.setTextFill(Color.web("#535353"));
	            icon.setFont(Font.font(48));

	            Label text = new Label("Content for " + pageName + " will appear here");
	            text.setTextFill(Color.web("#b3b3b3"));
	            text.setFont(Font.font(15));

	            placeholder.getChildren().addAll(icon, text);
	            contentArea.getChildren().add(placeholder);
	            break;
	    }
	}
	
	private VBox createLibraryPage() {
	    VBox page = new VBox(30);

	    // Playlists section
	    VBox playlistsSection = createSection("Playlists", "Your collections and mixes");
	    
	    // Stations section
	    VBox stationsSection = createSection("Stations", "Mood and artist based radio");

	    page.getChildren().addAll(playlistsSection, stationsSection);
	    return page;
	}

	private VBox createSection(String title, String description) {
	    VBox section = new VBox(12);

	    Label sectionTitle = new Label(title);
	    sectionTitle.setTextFill(Color.WHITE);
	    sectionTitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 20));

	    Label sectionDesc = new Label(description);
	    sectionDesc.setTextFill(Color.web("#b3b3b3"));
	    sectionDesc.setFont(Font.font(13));

	    // Placeholder card
	    HBox card = new HBox();
	    card.setPrefHeight(100);
	    card.setStyle("-fx-background-color: #181818; -fx-background-radius: 10px;");
	    card.setAlignment(Pos.CENTER);

	    Label placeholder = new Label("Items will appear here");
	    placeholder.setTextFill(Color.web("#535353"));

	    card.getChildren().add(placeholder);

	    section.getChildren().addAll(sectionTitle, sectionDesc, card);
	    return section;
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
}
