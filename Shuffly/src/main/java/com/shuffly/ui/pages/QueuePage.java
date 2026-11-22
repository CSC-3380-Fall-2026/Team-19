package com.shuffly.ui.pages;

import com.shuffly.AppState;
import com.shuffly.model.Track;
import com.shuffly.ui.TrackRow;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueuePage extends VBox {

    private final AppState state;
    private final Runnable onQueueChanged;
    private final VBox content = new VBox(18);
    private Timeline refreshTimeline;
    private String lastSignature = "";

    public QueuePage(AppState state, Runnable onQueueChanged) {
        this.state = state;
        this.onQueueChanged = onQueueChanged;

        setSpacing(0);
        setPadding(new Insets(10, 0, 20, 0));

        content.setSpacing(18);
        getChildren().add(content);

        rebuild();
        startAutoRefresh();
    }

    public QueuePage(AppState state) {
        this(state, null);
    }

    private void startAutoRefresh() {
        refreshTimeline = new Timeline(
            new KeyFrame(Duration.millis(800), e -> {
                String signature = buildSignature();
                if (!signature.equals(lastSignature)) {
                    lastSignature = signature;
                    rebuild();
                }
            })
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    private String buildSignature() {

        StringBuilder sb = new StringBuilder();

        sb.append(state.stationMode).append("|");

        if (state.stationMode) {

            sb.append(state.selectedStation == null
                    ? "none"
                    : state.selectedStation.getId());

            sb.append("|");

            Track current = state.stationPlayer.getCurrentTrack();

            sb.append(current == null
                    ? "none"
                    : current.getId() + current.getMoodTags());

            sb.append("|").append(state.isPlaying);

            return sb.toString();
        }

        Track current = state.queueManager.getCurrent();
        List<Track> upNext = state.queueManager.getUpNext();
        List<Track> history = state.queueManager.getHistory();

        sb.append(current == null
                ? "none"
                : current.getId() + current.getMoodTags())
          .append("|");

        sb.append(state.isPlaying).append("|");

        for (Track t : upNext) {
            sb.append(t.getId())
              .append(t.getMoodTags())
              .append(",");
        }

        sb.append("|");

        for (Track t : history) {
            sb.append(t.getId())
              .append(t.getMoodTags())
              .append(",");
        }

        return sb.toString();
    }

    private void rebuild() {
        content.getChildren().clear();
        
        // Station Mode: show only the station and current song.
        if (state.stationMode) {

            Label stationTitle = new Label("Now Playing: " +
                    (state.selectedStation == null
                            ? "Station"
                            : state.selectedStation.getName()));

            stationTitle.setTextFill(Color.WHITE);
            stationTitle.setFont(Font.font("System", FontWeight.BOLD, 22));

            VBox nowSection = new VBox(8);

            Track current = state.stationPlayer.getCurrentTrack();

            if (current != null) {
                nowSection.getChildren().add(
                        createTrackRow(current, "▶", true, false)
                );
            } else {
                Label empty = new Label("Nothing playing");
                empty.setTextFill(Color.web("#b3b3b3"));
                nowSection.getChildren().add(empty);
            }

            Label description = new Label(
                    "Your station plays randomized songs matching its description."
            );
            description.setTextFill(Color.web("#b3b3b3"));
            description.setWrapText(true);

            content.getChildren().addAll(
                    stationTitle,
                    nowSection,
                    description
            );

            return;
        }

        // ===== NOW PLAYING =====
        Label nowTitle = new Label("Now Playing");
        nowTitle.setTextFill(Color.WHITE);
        nowTitle.setFont(Font.font("System", FontWeight.BOLD, 22));

        VBox nowSection = new VBox(8);
        Track current = state.queueManager.getCurrent();
        if (current != null) {
            nowSection.getChildren().add(createTrackRow(current, "▶", true, false));
        } else {
            Label empty = new Label("Nothing playing");
            empty.setTextFill(Color.web("#b3b3b3"));
            nowSection.getChildren().add(empty);
        }

        // ===== UP NEXT =====
        Label upNextTitle = new Label("Up Next");
        upNextTitle.setTextFill(Color.WHITE);
        upNextTitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 18));
        upNextTitle.setPadding(new Insets(6, 0, 0, 0));

        VBox upNextList = new VBox(6);
        List<Track> upNext = state.queueManager.getUpNext();
        if (upNext.isEmpty()) {
            Label empty = new Label("Queue is empty");
            empty.setTextFill(Color.web("#b3b3b3"));
            upNextList.getChildren().add(empty);
        } else {
            int i = 1;
            for (Track track : upNext) {
                upNextList.getChildren().add(createTrackRow(track, String.valueOf(i++), false, true));
            }
        }

        // ===== RECENTLY PLAYED =====
        Label historyTitle = new Label("Recently Played");
        historyTitle.setTextFill(Color.WHITE);
        historyTitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 18));
        historyTitle.setPadding(new Insets(6, 0, 0, 0));

        VBox historyList = new VBox(6);
        List<Track> history = new ArrayList<>(state.queueManager.getHistory());
        Collections.reverse(history); // newest first

        if (history.isEmpty()) {
            Label empty = new Label("No recently played songs");
            empty.setTextFill(Color.web("#b3b3b3"));
            historyList.getChildren().add(empty);
        } else {
            for (Track track : history) {
                historyList.getChildren().add(createTrackRow(track, "↺", false, false));
            }
        }

        VBox allLists = new VBox(14, upNextList, historyTitle, historyList);

        ScrollPane scroll = new ScrollPane(allLists);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setPrefHeight(430);
        scroll.setPannable(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(nowTitle, nowSection, upNextTitle, scroll);
    }


	private TrackRow createTrackRow(Track track, String leftLabel, boolean isCurrent, boolean isUpNext) {
			return new TrackRow(
		        track,
		        leftLabel,
		        isCurrent,
		        () -> {
		            if (!isCurrent) {
		                // Play this song next, whether it came from
		                // Up Next or Recently Played.
		                state.queueManager.playNext(track);
		                state.queueManager.next();
	            }
	
	            state.isPlaying = true;
	
	            if (onQueueChanged != null) {
	                onQueueChanged.run();
	            }
	        }
	    );
	}
}