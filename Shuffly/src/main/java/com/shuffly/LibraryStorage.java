
package com.shuffly;

import com.shuffly.model.Playlist;
import com.shuffly.model.Track;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LibraryStorage {

    private static final Path STORAGE_DIRECTORY =
            Path.of(System.getProperty("user.home"), ".shuffly");

    private static final Path LIBRARY_FILE =
            STORAGE_DIRECTORY.resolve("library.json");

    /**
     * Saves the user's Liked Songs and playlists locally.
     */
    public static void save(AppState state) throws IOException {

        Files.createDirectories(STORAGE_DIRECTORY);

        JSONObject library = new JSONObject();

        JSONArray likedSongs = new JSONArray();

        for (Track track : state.likedSongs) {
            likedSongs.put(trackToJson(track));
        }

        library.put("likedSongs", likedSongs);

        JSONArray playlists = new JSONArray();

        for (Playlist playlist : state.playlists) {

            // Liked Songs are stored separately.
            if ("liked".equals(playlist.getId())) {
                continue;
            }

            JSONObject playlistJson = new JSONObject();

            playlistJson.put("id", playlist.getId());
            playlistJson.put("name", playlist.getName());
            playlistJson.put("imageUrl", playlist.getImageUrl());

            JSONArray tracks = new JSONArray();

            for (Track track : playlist.getTracks()) {
                tracks.put(trackToJson(track));
            }

            playlistJson.put("tracks", tracks);
            playlists.put(playlistJson);
        }

        library.put("playlists", playlists);

        Files.writeString(
                LIBRARY_FILE,
                library.toString(2)
        );

        System.out.println("Library saved to: " + LIBRARY_FILE);
    }

    /**
     * Loads the saved library into AppState.
     *
     * Returns false if no saved library exists.
     */
    public static boolean load(AppState state) throws IOException {

        if (!Files.exists(LIBRARY_FILE)) {
            System.out.println("No saved library found.");
            return false;
        }

        String contents = Files.readString(LIBRARY_FILE);

        JSONObject library = new JSONObject(contents);

        state.likedSongs.clear();
        state.playlists.clear();

        JSONArray likedJson = library.optJSONArray("likedSongs");

        if (likedJson != null) {
            for (int i = 0; i < likedJson.length(); i++) {
                state.likedSongs.add(
                        trackFromJson(likedJson.getJSONObject(i))
                );
            }
        }

        // Reconstruct the Liked Songs playlist using
        // the same Track objects as state.likedSongs.
        Playlist likedPlaylist =
                new Playlist("liked", "Liked Songs", null);

        for (Track track : state.likedSongs) {
            likedPlaylist.addTrack(track);
        }

        state.playlists.add(likedPlaylist);
        state.selectedPlaylist = likedPlaylist;

        JSONArray playlistsJson =
                library.optJSONArray("playlists");

        if (playlistsJson != null) {
            for (int i = 0; i < playlistsJson.length(); i++) {

                JSONObject playlistJson =
                        playlistsJson.getJSONObject(i);

                Playlist playlist = new Playlist(
                        playlistJson.getString("id"),
                        playlistJson.optString("name", "Untitled"),
                        playlistJson.isNull("imageUrl")
                                ? null
                                : playlistJson.optString("imageUrl")
                );

                JSONArray tracksJson =
                        playlistJson.optJSONArray("tracks");

                if (tracksJson != null) {
                    for (int j = 0; j < tracksJson.length(); j++) {
                        playlist.addTrack(
                                trackFromJson(
                                        tracksJson.getJSONObject(j)
                                )
                        );
                    }
                }

                state.playlists.add(playlist);
            }
        }

        state.queueManager.loadShuffled(
                state.likedSongs,
                state.selectedMood
        );

        System.out.println(
                "Loaded saved library: "
                        + state.likedSongs.size()
                        + " liked songs and "
                        + (state.playlists.size() - 1)
                        + " playlists."
        );

        return true;
    }

    private static JSONObject trackToJson(Track track) {

        JSONObject json = new JSONObject();

        json.put("id", track.getId());
        json.put("name", track.getName());
        json.put("artist", track.getArtist());
        json.put("albumArtUrl", track.getAlbumArtUrl());
        json.put("durationMs", track.getDurationMs());
        json.put("moodTags", new JSONArray(track.getMoodTags()));
        json.put(
                "moodTagsCustomized",
                track.areMoodTagsCustomized()
        );

        return json;
    }

    private static Track trackFromJson(JSONObject json) {

        Track track = new Track(
                json.getString("id"),
                json.optString("name", "Unknown"),
                json.optString("artist", "Unknown"),
                json.isNull("albumArtUrl")
                        ? null
                        : json.optString("albumArtUrl"),
                json.optInt("durationMs", 0)
        );

        JSONArray tags = json.optJSONArray("moodTags");

        if (tags != null) {
            java.util.List<String> moodTags =
                    new java.util.ArrayList<>();

            for (int i = 0; i < tags.length(); i++) {
                moodTags.add(tags.getString(i));
            }

            track.setMoodTags(moodTags);
        }

        track.setMoodTagsCustomized(
                json.optBoolean("moodTagsCustomized", false)
        );

        return track;
    }
}
