package com.shuffly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.shuffly.model.Track;
import com.shuffly.shuffle.*;

public class QueueManager<T> {
    private final List<T> upNext = new ArrayList<>();
    private final List<T> history = new ArrayList<>();
    private T current;

    // Load a playlist into the queue
    @SuppressWarnings("unchecked")
    public void loadPlaylist(List<T> songs, boolean shuffle) {
        List<T> source;

        if (shuffle) {
            List<Track> recent = new ArrayList<>();

            for (T item : history) {
                if (item instanceof Track) {
                    recent.add((Track) item);
                }
            }

            if (songs.stream().allMatch(Track.class::isInstance)) {
                List<Track> tracks = (List<Track>) (List<?>) songs;
                List<Track> ordered =
                        shuffleMode.getStrategy().shuffle(tracks, recent);

                source = (List<T>) (List<?>) ordered;
            } else {
                source = ShuffleAlgorithm.shuffle(songs);
            }
        } else {
            source = new ArrayList<>(songs);
        }

        upNext.clear();
        history.clear();
        upNext.addAll(source);
        current = upNext.isEmpty() ? null : upNext.remove(0);
    }

    public void addToQueue(T song)            { upNext.add(song); }
    public void playNext(T song)              { upNext.add(0, song); }
    public void removeFromQueue(int index)    { upNext.remove(index); }

    public void move(int from, int to) {
        T song = upNext.remove(from);
        upNext.add(to, song);
    }

    public T next() {
        if (current != null) history.add(current);
        current = upNext.isEmpty() ? null : upNext.remove(0);
        return current;
    }

    public T previous() {
        if (history.isEmpty()) return current;
        if (current != null) upNext.add(0, current);
        current = history.remove(history.size() - 1);
        return current;
    }

    public void clear()                       { upNext.clear(); }
    public T getCurrent()                     { return current; }
    public void setCurrent(T track) {
        if (track == null) {
            return;
        }

        // Don't update anything if this is already the current song.
        if (current instanceof Track && track instanceof Track
                && ((Track) current).getId().equals(((Track) track).getId())) {
            return;
        }

        // Move the previous song into history.
        if (current != null) {
            history.add(current);
        }

        // Remove the new current song from Up Next, if it's there.
        if (track instanceof Track) {
            String trackId = ((Track) track).getId();

            upNext.removeIf(item ->
                item instanceof Track
                    && ((Track) item).getId().equals(trackId)
            );
        }

        current = track;
    }
    public List<T> getUpNext()                { return Collections.unmodifiableList(upNext); }
    public List<T> getHistory()               { return Collections.unmodifiableList(history); }
    
    // --- Shuffle mode support ---
    private ShuffleMode shuffleMode = ShuffleMode.TRULY_RANDOM;

    public void setShuffleMode(ShuffleMode mode) { this.shuffleMode = mode; }
    public ShuffleMode getShuffleMode()          { return shuffleMode; }

    // Loads a playlist of Tracks, ordered by the currently selected shuffle mode
    @SuppressWarnings("unchecked")
    public void loadShuffled(List<Track> songs, String mood) {
        List<Track> recent = new ArrayList<>((List<Track>) (List<?>) history);

        ShuffleStrategy strategy = (shuffleMode == ShuffleMode.MOOD_BASED)
            ? new MoodBasedShuffle(mood)
            : shuffleMode.getStrategy();

        List<Track> ordered = strategy.shuffle(songs, recent);
        loadPlaylist((List<T>) (List<?>) ordered, false);
    }
    
    @SuppressWarnings("unchecked")
    public void reshuffleUpcoming(List<Track> songs, String mood) {
        if (songs == null || songs.isEmpty()) {
            return;
        }

        List<Track> recent = new ArrayList<>();

        for (T item : history) {
            if (item instanceof Track) {
                recent.add((Track) item);
            }
        }

        List<Track> remaining = new ArrayList<>(songs);

        // Don't include the currently playing song in the upcoming queue.
        if (current instanceof Track) {
            remaining.removeIf(track ->
                track.getId().equals(((Track) current).getId())
            );
        }

        ShuffleStrategy strategy = (shuffleMode == ShuffleMode.MOOD_BASED)
            ? new MoodBasedShuffle(mood)
            : shuffleMode.getStrategy();

        List<Track> ordered = strategy.shuffle(remaining, recent);

        upNext.clear();
        upNext.addAll((List<T>) (List<?>) ordered);
    }
}