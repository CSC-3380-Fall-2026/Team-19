package com.shuffly.spotify;

import com.shuffly.model.Playlist;
import com.shuffly.model.Track;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SpotifyMapper {

    public static Track toTrack(JSONObject trackJson) {
        if (trackJson == null || trackJson == JSONObject.NULL) {
            return null;
        }

        String id = trackJson.optString("id", "");
        String name = trackJson.optString("name", "Unknown");
        int durationMs = trackJson.optInt("duration_ms", 0);

        // Artist
        String artist = "Unknown Artist";
        JSONArray artists = trackJson.optJSONArray("artists");
        if (artists != null && artists.length() > 0) {
            artist = artists.getJSONObject(0).optString("name", artist);
        }

        // Album art
        String imageUrl = null;
        JSONObject album = trackJson.optJSONObject("album");
        if (album != null) {
            JSONArray images = album.optJSONArray("images");
            if (images != null && images.length() > 0) {
                imageUrl = images.getJSONObject(0).optString("url", null);
            }
        }

        return new Track(id, name, artist, imageUrl, durationMs);
    }

    public static List<Track> toTrackListFromSaved(JSONArray items) {
        List<Track> tracks = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            JSONObject trackJson = item.optJSONObject("track");
            Track track = toTrack(trackJson);
            if (track != null) {
                tracks.add(track);
            }
        }
        return tracks;
    }

    public static List<Track> toTrackListFromPlaylist(JSONArray items) {
        List<Track> tracks = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            JSONObject trackJson = item.optJSONObject("track");
            Track track = toTrack(trackJson);
            if (track != null) {
                tracks.add(track);
            }
        }
        return tracks;
    }

    public static Playlist toPlaylist(JSONObject playlistJson, List<Track> tracks) {
        String id = playlistJson.optString("id", "");
        String name = playlistJson.optString("name", "Untitled");

        String imageUrl = null;
        JSONArray images = playlistJson.optJSONArray("images");
        if (images != null && images.length() > 0) {
            imageUrl = images.getJSONObject(0).optString("url", null);
        }

        Playlist playlist = new Playlist(id, name, imageUrl);
        for (Track t : tracks) {
            playlist.addTrack(t);
        }
        return playlist;
    }
}