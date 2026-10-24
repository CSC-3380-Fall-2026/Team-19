package com.shuffly.ui.pages;

import com.shuffly.AppState;
import com.shuffly.model.Track;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class QueuePage extends VBox {

    public QueuePage(AppState state) {
        setSpacing(20);
        setPadding(new Insets(10, 0, 20, 0));

        Track current = state.queueManager.getCurrent();
        Label nowPlaying = new Label(
            current != null ? "Now Playing: " + current.getName() + " – " + current.getArtist()
                            : "Nothing playing"
        );
        nowPlaying.setTextFill(Color.WHITE);
        nowPlaying.setFont(Font.font("System", FontWeight.BOLD, 18));

        Label upNextTitle = new Label("Up Next");
        upNextTitle.setTextFill(Color.WHITE);
        upNextTitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
        upNextTitle.setPadding(new Insets(20, 0, 8, 0));

        VBox upNextList = new VBox(8);
        List<Track> upNext = state.queueManager.getUpNext();

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

        getChildren().addAll(nowPlaying, upNextTitle, upNextList);
    }
}