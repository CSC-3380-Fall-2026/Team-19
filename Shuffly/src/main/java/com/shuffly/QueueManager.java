package com.shuffly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueueManager<T> {
    private final List<T> upNext = new ArrayList<>();
    private final List<T> history = new ArrayList<>();
    private T current;

    // Load a playlist into the queue (optionally shuffled)
    public void loadPlaylist(List<T> songs, boolean shuffle) {
        upNext.clear();
        history.clear();
        List<T> source = shuffle ? ShuffleAlgorithm.shuffle(songs) : new ArrayList<>(songs);
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
    public List<T> getUpNext()                { return Collections.unmodifiableList(upNext); }
    public List<T> getHistory()               { return Collections.unmodifiableList(history); }
}
