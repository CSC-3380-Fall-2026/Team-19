package com.shuffly.spotify;

import com.shuffly.AppState;
import com.shuffly.model.Playlist;
import com.shuffly.model.Track;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SpotifyService {

	private final SpotifyApiClient api = new SpotifyApiClient();
	
	public void loadUserLibrary(AppState state) throws Exception {
	    // Liked Songs
	    JSONArray likedJson = api.getLikedSongs();
	    state.likedSongs = SpotifyMapper.toTrackListFromSaved(likedJson);

	    Playlist likedPlaylist = new Playlist("liked", "Liked Songs", null);
	    for (Track t : state.likedSongs) {
	        likedPlaylist.addTrack(t);
	    }

	    // User playlists
	    JSONArray playlistsJson = api.getUserPlaylists();
	    List<Playlist> playlists = new ArrayList<>();
	    playlists.add(likedPlaylist);

	    for (int i = 0; i < playlistsJson.length(); i++) {
	        JSONObject p = playlistsJson.getJSONObject(i);
	        String id = p.getString("id");
	        String name = p.optString("name", "Untitled");

	        List<Track> tracks = new ArrayList<>();
	        try {
	            JSONArray tracksJson = api.getPlaylistTracks(id);
	            tracks = SpotifyMapper.toTrackListFromPlaylist(tracksJson);
	        } catch (Exception ex) {
	            System.err.println("Skipping playlist '" + name + "' (couldn't load tracks)");
	        }

	        // Even if tracks failed, still show the playlist card
	        playlists.add(SpotifyMapper.toPlaylist(p, tracks));
	    }

	    state.playlists = playlists;
	}
	
	public void loadPlaylistTracks(Playlist playlist) throws Exception {
	    if (playlist == null) return;

	    // Liked Songs is already filled during login
	    if ("liked".equals(playlist.getId())) {
	        return;
	    }

	    JSONArray tracksJson = api.getPlaylistTracks(playlist.getId());
	    List<Track> tracks = SpotifyMapper.toTrackListFromPlaylist(tracksJson);

	    playlist.getTracks().clear();
	    for (Track t : tracks) {
	        playlist.addTrack(t);
	    }

	    System.out.println("Loaded " + tracks.size() + " tracks for: " + playlist.getName());
	}
}
