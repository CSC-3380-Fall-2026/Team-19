package com.shuffly;
// Jakobe Humphrey

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.shuffly.model.Track;
import com.shuffly.shuffle.ShuffleMode;

public class QueueManager<T> {
    private final List<T> upNext = new ArrayList<>();
    private final List<T> history = new ArrayList<>();
    private T current;
    
    // Defaults to Mood-based so it matches AppState's default label (still a random placeholder for now)
    private ShuffleMode shuffleMode = ShuffleMode.MOOD_BASED;

    public void setShuffleMode(ShuffleMode mode) { this.shuffleMode = mode; }
    public ShuffleMode getShuffleMode()          { return shuffleMode; }

    // Load a playlist into the queue. If shuffle is true, the selected shuffle mode orders it.
    @SuppressWarnings("unchecked")
    public void loadPlaylist(List<T> songs, boolean shuffle) {
    	List<T> source;
        if (shuffle) {
            // grab history first, because it gets cleared below
            List<Track> recent = new ArrayList<>((List<Track>) (List<?>) history);
            List<Track> ordered = shuffleMode.getStrategy().shuffle((List<Track>) (List<?>) songs, recent);
            source = (List<T>) (List<?>) ordered;
        } else {
            source = new ArrayList<>(songs);
        }
        upNext.clear();
        history.clear();
        upNext.addAll(source);
        current = upNext.isEmpty() ? null : upNext.remove(0);
    }
    
    // Kept so earlier code still compiles; same as loadPlaylist(songs, true)
    @SuppressWarnings("unchecked")
    public void loadShuffled(List<Track> songs) {
        loadPlaylist((List<T>) (List<?>) songs, true);
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
    public List<T> getUpNext()                { return Collections.unmodifiableList(upNext); }
    public List<T> getHistory()               { return Collections.unmodifiableList(history); }
}