package com.shuffly.ui.pages;

import com.shuffly.AppState;
import com.shuffly.model.Track;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class QueuePage extends VBox {

    private final AppState state;
    private final Runnable onQueueChanged;

    public QueuePage(AppState state, Runnable onQueueChanged) {
        this.state = state;
        this.onQueueChanged = onQueueChanged;

        setSpacing(18);
        setPadding(new Insets(10, 0, 20, 0));
        build();
    }

    // Keep old constructor working
    public QueuePage(AppState state) {
        this(state, null);
    }

    private void build() {
        getChildren().clear();

        Label nowPlayingTitle = new Label("Now Playing");
        nowPlayingTitle.setTextFill(Color.WHITE);
        nowPlayingTitle.setFont(Font.font("System", FontWeight.BOLD, 22));

        VBox nowPlayingSection = new VBox(10);
        Track current = state.queueManager.getCurrent();
        if (current != null) {
            nowPlayingSection.getChildren().add(createTrackRow(0, current, true));
        } else {
            Label empty = new Label("Nothing playing");
            empty.setTextFill(Color.web("#b3b3b3"));
            nowPlayingSection.getChildren().add(empty);
        }

        Label upNextTitle = new Label("Up Next");
        upNextTitle.setTextFill(Color.WHITE);
        upNextTitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 18));
        upNextTitle.setPadding(new Insets(8, 0, 0, 0));

        VBox upNextList = new VBox(6);
        List<Track> upNext = state.queueManager.getUpNext();

        if (upNext.isEmpty()) {
            Label empty = new Label("Queue is empty");
            empty.setTextFill(Color.web("#b3b3b3"));
            upNextList.getChildren().add(empty);
        } else {
            int index = 1;
            for (Track track : upNext) {
                upNextList.getChildren().add(createTrackRow(index++, track, false));
            }
        }

        ScrollPane scroll = new ScrollPane(upNextList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setPrefHeight(420);
        scroll.setPannable(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(nowPlayingTitle, nowPlayingSection, upNextTitle, scroll);
    }

    private HBox createTrackRow(int number, Track track, boolean isCurrent) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setStyle(isCurrent
            ? "-fx-background-color: #282828; -fx-background-radius: 6px;"
            : "-fx-background-radius: 6px;");
        row.setCursor(javafx.scene.Cursor.HAND);

        Label num = new Label(isCurrent ? "▶" : String.valueOf(number));
        num.setTextFill(isCurrent ? Color.web("#1db954") : Color.web("#b3b3b3"));
        num.setMinWidth(28);
        num.setFont(Font.font(13));

        VBox info = new VBox(2);
        Label name = new Label(track.getName());
        name.setTextFill(Color.WHITE);
        name.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));

        Label artist = new Label(track.getArtist());
        artist.setTextFill(Color.web("#b3b3b3"));
        artist.setFont(Font.font(12));
        info.getChildren().addAll(name, artist);

        row.getChildren().addAll(num, info);

        if (!isCurrent) {
            row.setOnMouseEntered(e ->
                row.setStyle("-fx-background-color: #282828; -fx-background-radius: 6px;")
            );
            row.setOnMouseExited(e ->
                row.setStyle("-fx-background-radius: 6px;")
            );

            // Click → play this song next, then skip to it
            row.setOnMouseClicked(e -> {
                state.queueManager.playNext(track);
                state.queueManager.next();
                state.isPlaying = true;
                build(); // refresh this page
                if (onQueueChanged != null) {
                    onQueueChanged.run(); // refresh player bar
                }
            });
        }

        return row;
    }
}