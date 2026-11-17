package com.shuffly.spotify;

import com.shuffly.AppState;
import com.shuffly.model.Playlist;
import com.shuffly.model.Track;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	
	public void enrichTracksWithGenres(List<Track> tracks) throws Exception {
	    // Collect unique artist ids missing genres
	    Map<String, List<Track>> byArtist = new LinkedHashMap<>();
	    for (Track t : tracks) {
	        if (t.getArtistId() == null) continue;
	        if (!t.getGenres().isEmpty()) continue;
	        byArtist.computeIfAbsent(t.getArtistId(), k -> new ArrayList<>()).add(t);
	    }

	    List<String> ids = new ArrayList<>(byArtist.keySet());
	    for (int i = 0; i < ids.size(); i += 50) {
	        List<String> batch = ids.subList(i, Math.min(i + 50, ids.size()));
	        JSONArray artists = api.getArtists(batch);

	        for (int j = 0; j < artists.length(); j++) {
	            JSONObject artist = artists.optJSONObject(j);
	            if (artist == null) continue;

	            String artistId = artist.optString("id", "");
	            JSONArray genreArr = artist.optJSONArray("genres");
	            List<String> genres = new ArrayList<>();
	            if (genreArr != null) {
	                for (int g = 0; g < genreArr.length(); g++) {
	                    genres.add(genreArr.getString(g).toLowerCase());
	                }
	            }

	            List<Track> related = byArtist.getOrDefault(artistId, List.of());
	            for (Track t : related) {
	                t.setGenres(genres);
	            }
	        }
	    }
	}
}
