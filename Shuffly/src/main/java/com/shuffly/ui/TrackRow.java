
package com.shuffly.ui;

import com.shuffly.model.Track;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.shuffly.AppState;
import com.shuffly.LibraryStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackRow extends VBox {

    private static final List<String> MOODS = Arrays.asList(
        "Happy", "Chill", "Sad", "Energetic",
        "Focus", "Party", "Romantic", "Angry"
    );

    private final Track track;
    private final AppState state;
    private final List<CheckBox> moodCheckboxes = new ArrayList<>();
    private final FlowPane moodDisplay = new FlowPane(6, 5);
    private final VBox editor = new VBox(10);

    public TrackRow(
    		AppState state,
            Track track,
            String leftLabel,
            boolean isCurrent,
            Runnable onPlay) {

        this.track = track;
        this.state = state;

        setSpacing(8);
        setPadding(new Insets(10, 12, 10, 12));

        String normalStyle = isCurrent
            ? "-fx-background-color: #282828; -fx-background-radius: 6px;"
            : "-fx-background-radius: 6px;";

        setStyle(normalStyle);

        // Main song information
        HBox summary = new HBox(14);
        summary.setAlignment(Pos.CENTER_LEFT);
        summary.setCursor(Cursor.HAND);

        Label number = new Label(leftLabel);
        number.setTextFill(
            isCurrent ? Color.web("#1db954") : Color.web("#b3b3b3")
        );
        number.setMinWidth(28);
        number.setFont(Font.font(13));

        VBox info = new VBox(4);

        Label name = new Label(track.getName());
        name.setTextFill(Color.WHITE);
        name.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));

        Label artist = new Label(track.getArtist());
        artist.setTextFill(Color.web("#b3b3b3"));
        artist.setFont(Font.font(12));

        info.getChildren().addAll(name, artist);

        VBox.setVgrow(info, javafx.scene.layout.Priority.ALWAYS);

        moodDisplay.setAlignment(Pos.CENTER_LEFT);
        refreshMoodDisplay();

        summary.getChildren().addAll(number, info, moodDisplay);

        // Expand or collapse the mood editor
        summary.setOnMouseClicked(e -> {
            boolean opening = !editor.isVisible();

            editor.setVisible(opening);
            editor.setManaged(opening);

            e.consume();
        });

        // Mood editor
        editor.setPadding(new Insets(8, 0, 4, 42));
        editor.setVisible(false);
        editor.setManaged(false);

        Label editTitle = new Label("Edit mood tags");
        editTitle.setTextFill(Color.WHITE);
        editTitle.setFont(Font.font("System", FontWeight.BOLD, 13));

        FlowPane checkboxPane = new FlowPane(12, 8);

        for (String mood : MOODS) {
            CheckBox checkbox = new CheckBox(mood);
            checkbox.setTextFill(Color.WHITE);
            checkbox.setSelected(
                track.getMoodTags().contains(mood.toLowerCase())
            );

            moodCheckboxes.add(checkbox);
            checkboxPane.getChildren().add(checkbox);
        }

        Button saveButton = new Button("Save tags");
        saveButton.setStyle(
            "-fx-background-color: #1db954;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 16px;" +
            "-fx-cursor: hand;"
        );

        Label savedMessage = new Label();
        savedMessage.setTextFill(Color.web("#1db954"));
        savedMessage.setFont(Font.font(11));

        saveButton.setOnAction(e -> {
            List<String> selectedMoods = new ArrayList<>();

            for (CheckBox checkbox : moodCheckboxes) {
                if (checkbox.isSelected()) {
                    selectedMoods.add(
                        checkbox.getText().toLowerCase()
                    );
                }
            }

            track.setMoodTags(selectedMoods);
            track.setMoodTagsCustomized(true);

            try {
                LibraryStorage.save(state);
                savedMessage.setText("Tags saved!");
            } catch (Exception ex) {
                savedMessage.setText("Could not save tags.");
                System.err.println("Could not save updated mood tags:");
                ex.printStackTrace();
            }

            refreshMoodDisplay();

            editor.setVisible(false);
            editor.setManaged(false);
        });

        editor.getChildren().addAll(
            editTitle,
            checkboxPane,
            new HBox(10, saveButton, savedMessage)
        );

        getChildren().addAll(summary, editor);

        // Hover effect
        setOnMouseEntered(e -> {
            if (!isCurrent) {
                setStyle(
                    "-fx-background-color: #282828;" +
                    "-fx-background-radius: 6px;"
                );
            }
        });

        setOnMouseExited(e -> setStyle(normalStyle));
    }

    private void refreshMoodDisplay() {
        moodDisplay.getChildren().clear();

        if (track.getMoodTags().isEmpty()) {
            Label empty = new Label("No mood tags");
            empty.setTextFill(Color.web("#888888"));
            empty.setFont(Font.font(11));
            moodDisplay.getChildren().add(empty);
            return;
        }

        for (String mood : track.getMoodTags()) {
            Label tag = new Label(
                mood.substring(0, 1).toUpperCase() + mood.substring(1)
            );

            tag.setTextFill(Color.web("#1db954"));
            tag.setStyle(
                "-fx-background-color: #1e3a2a;" +
                "-fx-background-radius: 10px;" +
                "-fx-padding: 4px 8px;"
            );
            tag.setFont(Font.font(11));

            moodDisplay.getChildren().add(tag);
        }
    }
}
