package com.shuffly.shuffle;

import com.shuffly.model.Station;
import com.shuffly.model.Track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StationPlayer {

    private final List<Track> stationTracks = new ArrayList<>();
    private final List<Track> history = new ArrayList<>();
    private final List<Track> forwardTracks = new ArrayList<>();

    private int currentIndex = 0;
    private Track currentTrack = null;

    public void loadStation(Station station, List<Track> library) {

        stationTracks.clear();
        history.clear();
        forwardTracks.clear();

        currentIndex = 0;
        currentTrack = null;

        if (station == null || library == null) {
            return;
        }

        Set<String> stationMoods =
            new HashSet<>(station.getMatchingMoods());

        if (stationMoods.isEmpty()) {
            return;
        }

        for (Track track : library) {

            if (track == null) {
                continue;
            }

            for (String mood : track.getMoodTags()) {

                if (stationMoods.contains(mood.toLowerCase())) {
                    stationTracks.add(track);
                    break;
                }
            }
        }

        Collections.shuffle(stationTracks);
    }

    public Track getNextTrack() {

        if (!forwardTracks.isEmpty()) {

            if (currentTrack != null) {
                history.add(currentTrack);
            }

            currentTrack =
                forwardTracks.remove(forwardTracks.size() - 1);

            return currentTrack;
        }

        if (stationTracks.isEmpty()) {
            return null;
        }

        if (currentTrack != null) {
            history.add(currentTrack);
        }

        if (currentIndex >= stationTracks.size()) {
            Collections.shuffle(stationTracks);
            currentIndex = 0;
        }

        currentTrack = stationTracks.get(currentIndex++);

        return currentTrack;
    }

    public Track getPreviousTrack() {

        if (history.isEmpty()) {
            return currentTrack;
        }

        if (currentTrack != null) {
            forwardTracks.add(currentTrack);
        }

        currentTrack = history.remove(history.size() - 1);

        return currentTrack;
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public int getMatchingTrackCount() {
        return stationTracks.size();
    }

    public void clear() {
        stationTracks.clear();
        history.clear();
        forwardTracks.clear();

        currentIndex = 0;
        currentTrack = null;
    }
}