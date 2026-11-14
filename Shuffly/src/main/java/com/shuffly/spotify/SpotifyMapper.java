package com.shuffly.spotify;

import com.shuffly.model.Playlist;
import com.shuffly.model.Track;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SpotifyMapper {
	
	private int popularity = 0;
	private String artistId = null;
	private final List<String> genres = new ArrayList<>();

	public int getPopularity() { return popularity; }
	public void setPopularity(int popularity) { this.popularity = popularity; }

	public String getArtistId() { return artistId; }
	public void setArtistId(String artistId) { this.artistId = artistId; }

	public List<String> getGenres() { return genres; }
	public void setGenres(List<String> genres) {
	    this.genres.clear();
	    if (genres != null) this.genres.addAll(genres);
	}

	public static Track toTrack(JSONObject trackJson) {
	    if (trackJson == null || trackJson == JSONObject.NULL) {
	        return null;
	    }

	    String id = trackJson.optString("id", "");
	    String name = trackJson.optString("name", "Unknown");
	    int durationMs = trackJson.optInt("duration_ms", 0);

	    // Artist name + artist id
	    String artist = "Unknown Artist";
	    String artistId = null;
	    JSONArray artists = trackJson.optJSONArray("artists");
	    if (artists != null && artists.length() > 0) {
	        JSONObject firstArtist = artists.getJSONObject(0);
	        artist = firstArtist.optString("name", artist);
	        artistId = firstArtist.optString("id", null);
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

	    Track track = new Track(id, name, artist, imageUrl, durationMs);

	    // Extra mood-related data
	    track.setPopularity(trackJson.optInt("popularity", 0));
	    track.setArtistId(artistId);

	    return track;
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