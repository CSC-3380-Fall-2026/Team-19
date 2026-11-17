package com.shuffly.ui;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.shuffly.AppState;
import com.shuffly.model.Track;
import com.shuffly.spotify.SpotifyApiClient;
import com.shuffly.spotify.SpotifyAuth;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PlayerBar extends HBox {

    private final AppState state;
    private final Label songTitleLabel;
    private final Label artistNameLabel;
    private final Label shuffleLabel;
    private final Button playBtn;
    private Slider progressSlider;
    private Label currentTimeLabel;
    private Label totalTimeLabel;
    private javafx.animation.Timeline progressTimeline;
    private boolean isSeeking = false;

    public PlayerBar(AppState state) {
        this.state = state;

        setPrefHeight(90);
        setSpacing(20);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(0, 20, 0, 20));
        setStyle("-fx-background-color: #181818; -fx-border-color: #282828; -fx-border-width: 1 0 0 0;");

        // Left: album art + song info
        HBox left = new HBox(15);
        left.setAlignment(Pos.CENTER_LEFT);
        left.setPrefWidth(320);

        Label albumArt = new Label("♪");
        albumArt.setPrefSize(56, 56);
        albumArt.setAlignment(Pos.CENTER);
        albumArt.setStyle("-fx-background-color: #333333; -fx-text-fill: #b3b3b3; -fx-font-size: 24px; -fx-background-radius: 4px;");

        songTitleLabel = new Label("No song playing");
        songTitleLabel.setTextFill(Color.WHITE);
        songTitleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));

        artistNameLabel = new Label("—");
        artistNameLabel.setTextFill(Color.web("#b3b3b3"));
        artistNameLabel.setFont(Font.font(12));

        VBox songInfo = new VBox(3, songTitleLabel, artistNameLabel);
        left.getChildren().addAll(albumArt, songInfo);

        // Center: controls + progress
        VBox center = new VBox(8);
        center.setAlignment(Pos.CENTER);
        HBox.setHgrow(center, Priority.ALWAYS);

        Button prevBtn = controlButton("⏮");
        playBtn = playButton();
        Button nextBtn = controlButton("⏭");

        prevBtn.setOnAction(e -> {
            Track track = state.queueManager.previous();
            new Thread(() -> {
                try {
                    SpotifyApiClient api = new SpotifyApiClient();
                    String deviceId = api.getActiveDeviceId();
                    if (track != null) {
                        api.playTracks(List.of(track.getId()), deviceId);
                    } else {
                        api.previous(deviceId);
                    }
                    Platform.runLater(this::refresh);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
        nextBtn.setOnAction(e -> {
            Track track = state.queueManager.next();
            new Thread(() -> {
                try {
                    SpotifyApiClient api = new SpotifyApiClient();
                    String deviceId = api.getActiveDeviceId();
                    if (track != null) {
                        api.playTracks(List.of(track.getId()), deviceId);
                    } else {
                        api.next(deviceId);
                    }
                    Platform.runLater(this::refresh);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
        playBtn.setOnAction(e -> {
            new Thread(() -> {
                try {
                    SpotifyApiClient api = new SpotifyApiClient();
                    String deviceId = api.getActiveDeviceId();

                    if (state.isPlaying) {
                        // Pause — keep current position
                        api.pause(deviceId);
                        state.isPlaying = false;
                    } else {
                        // Resume from current position (do NOT call playTracks here)
                        api.resume(deviceId);
                        state.isPlaying = true;
                    }

                    Platform.runLater(this::refresh);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        HBox controls = new HBox(20, prevBtn, playBtn, nextBtn);
        controls.setAlignment(Pos.CENTER);

        currentTimeLabel = new Label("0:00");
        currentTimeLabel.setTextFill(Color.web("#b3b3b3"));
        currentTimeLabel.setFont(Font.font(11));
        currentTimeLabel.setMinWidth(35);

        progressSlider = new Slider(0, 100, 0);
        progressSlider.setPrefWidth(400);
        progressSlider.setStyle("-fx-control-inner-background: #4d4d4d;");

        totalTimeLabel = new Label("0:00");
        totalTimeLabel.setTextFill(Color.web("#b3b3b3"));
        totalTimeLabel.setFont(Font.font(11));
        totalTimeLabel.setMinWidth(35);

        // User dragging seek bar
        progressSlider.setOnMousePressed(e -> isSeeking = true);
        progressSlider.setOnMouseReleased(e -> {
            isSeeking = false;
            seekToSliderPosition();
        });

        HBox progress = new HBox(10, currentTimeLabel, progressSlider, totalTimeLabel);
        progress.setAlignment(Pos.CENTER);

        center.getChildren().addAll(controls, progress);

        // Right: shuffle pill
        shuffleLabel = new Label("Shuffle: " + state.currentShuffleMode);
        shuffleLabel.setTextFill(state.accentColor);
        shuffleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        shuffleLabel.setStyle("-fx-padding: 6 12; -fx-background-color: #282828; -fx-background-radius: 20;");

        HBox right = new HBox(shuffleLabel);
        right.setAlignment(Pos.CENTER_RIGHT);
        right.setPrefWidth(220);

        getChildren().addAll(left, center, right);
        startProgressUpdates();
    }

    public void refresh() {
        Track current = state.queueManager.getCurrent();
        if (current != null) {
            songTitleLabel.setText(current.getName());
            artistNameLabel.setText(current.getArtist());
        } else {
            songTitleLabel.setText("No song playing");
            artistNameLabel.setText("—");
            state.isPlaying = false;
        }

        playBtn.setText(state.isPlaying ? "⏸" : "▶");
        shuffleLabel.setText("Shuffle: " + state.currentShuffleMode);
        shuffleLabel.setTextFill(state.accentColor);
    }

    private Button playButton() {
        Button btn = new Button("▶");
        String style =
            "-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px; " +
            "-fx-cursor: hand; -fx-background-radius: 50%; " +
            "-fx-min-width: 48px; -fx-min-height: 48px; -fx-max-width: 48px; -fx-max-height: 48px; " +
            "-fx-focus-color: transparent; -fx-faint-focus-color: transparent;";
        btn.setStyle(style);
        return btn;
    }

    private Button controlButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #b3b3b3; -fx-font-size: 16px; " +
            "-fx-cursor: hand; -fx-padding: 8; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; " +
            "-fx-cursor: hand; -fx-padding: 8; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #b3b3b3; -fx-font-size: 16px; " +
            "-fx-cursor: hand; -fx-padding: 8; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;"
        ));
        return btn;
    }
    
    private String formatTime(long ms) {
        long totalSec = ms / 1000;
        long min = totalSec / 60;
        long sec = totalSec % 60;
        return String.format("%d:%02d", min, sec);
    }

    private void seekToSliderPosition() {
        if (!SpotifyAuth.isLoggedIn()) return;

        new Thread(() -> {
            try {
                SpotifyApiClient api = new SpotifyApiClient();
                JSONObject playing = api.getCurrentlyPlaying();
                if (playing == null) return;

                JSONObject item = playing.optJSONObject("item");
                if (item == null) return;

                long duration = item.optLong("duration_ms", 0);
                long position = (long) (progressSlider.getValue() / 100.0 * duration);

                String deviceId = api.getActiveDeviceId();
                api.seek(position, deviceId);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public void startProgressUpdates() {
        if (progressTimeline != null) {
            progressTimeline.stop();
        }

        progressTimeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(
                javafx.util.Duration.seconds(1),
                e -> pollPlaybackState()
            )
        );
        progressTimeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        progressTimeline.play();
    }

    private void pollPlaybackState() {
        if (!SpotifyAuth.isLoggedIn() || isSeeking) return;

        new Thread(() -> {
            try {
                SpotifyApiClient api = new SpotifyApiClient();
                JSONObject playing = api.getCurrentlyPlaying();

                Platform.runLater(() -> {
                    if (playing == null) {
                        currentTimeLabel.setText("0:00");
                        totalTimeLabel.setText("0:00");
                        progressSlider.setValue(0);
                        return;
                    }

                    boolean isPlayingNow = playing.optBoolean("is_playing", false);
                    long progressMs = playing.optLong("progress_ms", 0);

                    JSONObject item = playing.optJSONObject("item");
                    if (item != null) {
                        long durationMs = item.optLong("duration_ms", 1);
                        String name = item.optString("name", "Unknown");
                        String artist = "Unknown";
                        JSONArray artists = item.optJSONArray("artists");
                        if (artists != null && artists.length() > 0) {
                            artist = artists.getJSONObject(0).optString("name", artist);
                        }

                        songTitleLabel.setText(name);
                        artistNameLabel.setText(artist);
                        currentTimeLabel.setText(formatTime(progressMs));
                        totalTimeLabel.setText(formatTime(durationMs));
                        progressSlider.setValue(100.0 * progressMs / durationMs);

                        state.isPlaying = isPlayingNow;
                        playBtn.setText(isPlayingNow ? "⏸" : "▶");
                    }
                });
            } catch (Exception ex) {
                // avoid spamming console every second
            }
        }).start();
    }
}