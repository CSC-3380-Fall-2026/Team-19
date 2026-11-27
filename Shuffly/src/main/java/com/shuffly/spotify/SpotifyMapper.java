
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

        // Get artist name
        String artist = "Unknown Artist";

        JSONArray artists = trackJson.optJSONArray("artists");

        if (artists != null && artists.length() > 0) {
            JSONObject firstArtist = artists.optJSONObject(0);

            if (firstArtist != null) {
                artist = firstArtist.optString("name", artist);
            }
        }

        // Get album artwork
        String imageUrl = null;

        JSONObject album = trackJson.optJSONObject("album");

        if (album != null) {

            JSONArray images = album.optJSONArray("images");

            if (images != null && images.length() > 0) {
                JSONObject firstImage = images.optJSONObject(0);

                if (firstImage != null) {
                    imageUrl = firstImage.optString("url", null);
                }
            }
        }

        return new Track(
                id,
                name,
                artist,
                imageUrl,
                durationMs
        );
    }

    public static List<Track> toTrackListFromSaved(JSONArray items) {

        List<Track> tracks = new ArrayList<>();

        for (int i = 0; i < items.length(); i++) {

            JSONObject item = items.optJSONObject(i);

            if (item == null) {
                continue;
            }

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
	
	        JSONObject item = items.optJSONObject(i);
	
	        if (item == null) {
	            continue;
	        }
	
	        // New Spotify response format uses "item".
	        // Fall back to "track" for older responses.
	        JSONObject trackJson = item.optJSONObject("item");
	
	        if (trackJson == null) {
	            trackJson = item.optJSONObject("track");
	        }
	
	        Track track = toTrack(trackJson);
	
	        if (track != null && !track.getId().isEmpty()) {
	            tracks.add(track);
	        }
	    }
	
	    return tracks;
	}


    public static Playlist toPlaylist(
            JSONObject playlistJson,
            List<Track> tracks) {

        String id = playlistJson.optString("id", "");
        String name = playlistJson.optString("name", "Untitled");

        String imageUrl = null;

        JSONArray images = playlistJson.optJSONArray("images");

        if (images != null && images.length() > 0) {

            JSONObject firstImage = images.optJSONObject(0);

            if (firstImage != null) {
                imageUrl = firstImage.optString("url", null);
            }
        }

        Playlist playlist = new Playlist(id, name, imageUrl);

        for (Track track : tracks) {
            playlist.addTrack(track);
        }

        return playlist;
    }
}
