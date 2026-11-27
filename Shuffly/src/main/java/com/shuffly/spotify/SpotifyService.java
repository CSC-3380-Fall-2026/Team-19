
package com.shuffly.spotify;

import com.shuffly.AppState;
import com.shuffly.LibraryStorage;
import com.shuffly.model.Playlist;
import com.shuffly.model.Track;
import com.shuffly.shuffle.MoodTagger;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

public class SpotifyService {

    private final SpotifyApiClient api = new SpotifyApiClient();

    public void loadUserLibrary(AppState state) throws Exception {
        loadUserLibrary(state, false);
    }

    public void refreshUserLibrary(AppState state) throws Exception {
        loadUserLibrary(state, true);
    }

    private void loadUserLibrary(AppState state, boolean preserveQueue) throws Exception {
    	String selectedPlaylistId = state.selectedPlaylist == null
    	        ? "liked"
    	        : state.selectedPlaylist.getId();
    	
    	// Keep existing tracks so custom mood tags can be preserved.
    	List<Track> previousTracks = new ArrayList<>(state.likedSongs);

    	for (Playlist playlist : state.playlists) {
    	    previousTracks.addAll(playlist.getTracks());
    	}
    	
        // Load Liked Songs
    	JSONArray likedJson = api.getLikedSongs();
    	List<Track> newLikedSongs = SpotifyMapper.toTrackListFromSaved(likedJson);
    	preserveCustomizedMoodTags(newLikedSongs, previousTracks);
    	state.likedSongs = newLikedSongs;
    	assignMoodTags(state.likedSongs);

        Playlist likedPlaylist =
                new Playlist("liked", "Liked Songs", null);

        for (Track track : state.likedSongs) {
            likedPlaylist.addTrack(track);
        }

        // Load user playlists
        JSONArray playlistsJson = api.getUserPlaylists();

        List<Playlist> playlists = new ArrayList<>();
        playlists.add(likedPlaylist);

        for (int i = 0; i < playlistsJson.length(); i++) {

            JSONObject playlistJson = playlistsJson.getJSONObject(i);

            String id = playlistJson.getString("id");
            String name = playlistJson.optString("name", "Untitled");

            List<Track> tracks = new ArrayList<>();

            try {
            	JSONArray tracksJson = api.getPlaylistTracks(id);
            	tracks = SpotifyMapper.toTrackListFromPlaylist(tracksJson);
            	preserveCustomizedMoodTags(tracks, previousTracks);
            	assignMoodTags(tracks);

            } catch (Exception ex) {
                System.err.println(
                        "Skipping playlist '" + name
                                + "' (couldn't load tracks)"
                );
            }

            // Keep the playlist card even if loading tracks failed
            playlists.add(SpotifyMapper.toPlaylist(playlistJson, tracks));
        }

        state.playlists = playlists;

        // Make Liked Songs the default active playlist
        state.selectedPlaylist = playlists.stream()
                .filter(playlist -> playlist.getId().equals(selectedPlaylistId))
                .findFirst()
                .orElse(likedPlaylist);

        // Only initialize the queue if there isn't already a current song.
        if (!preserveQueue && state.queueManager.getCurrent() == null) {
            state.queueManager.loadShuffled(
                    likedPlaylist.getTracks(),
                    state.selectedMood
            );
        }
        try {
            LibraryStorage.save(state);
        } catch (Exception e) {
            System.err.println("Could not save Spotify library:");
            e.printStackTrace();
        }
    }

    public void loadPlaylistTracks(Playlist playlist) throws Exception {

        if (playlist == null) {
            return;
        }

        // Liked Songs are already loaded during login
        if ("liked".equals(playlist.getId())) {
            assignMoodTags(playlist.getTracks());
            return;
        }

        JSONArray tracksJson =
                api.getPlaylistTracks(playlist.getId());

        List<Track> tracks =
                SpotifyMapper.toTrackListFromPlaylist(tracksJson);

        playlist.getTracks().clear();
        playlist.getTracks().addAll(tracks);

        assignMoodTags(playlist.getTracks());

        System.out.println(
                "Loaded " + tracks.size()
                        + " tracks for: " + playlist.getName()
        );
    }

    /**
 * Searches Spotify and converts the results into Track objects.
 */
    public List<Track> searchTracks(String query) throws Exception {

    JSONArray results = api.searchTracks(query);

    List<Track> tracks = new ArrayList<>();

    for (int i = 0; i < results.length(); i++) {
        JSONObject trackJson = results.getJSONObject(i);

        Track track = SpotifyMapper.toTrack(trackJson);
        tracks.add(track);
    }

    assignMoodTags(tracks);

    return tracks;
}


	private void assignMoodTags(List<Track> tracks) {
	
	    for (Track track : tracks) {
	        MoodTagger.assignTags(track);
	    }
	}
	
	private void preserveCustomizedMoodTags(
	        List<Track> newTracks,
	        List<Track> previousTracks) {

	    Map<String, Track> previousTracksById = new HashMap<>();

	    for (Track track : previousTracks) {
	        if (track.areMoodTagsCustomized()) {
	            previousTracksById.put(track.getId(), track);
	        }
	    }

	    for (Track newTrack : newTracks) {
	        Track previousTrack = previousTracksById.get(newTrack.getId());

	        if (previousTrack != null) {
	            newTrack.setMoodTags(previousTrack.getMoodTags());
	            newTrack.setMoodTagsCustomized(true);
	        }
	    }
	}

}