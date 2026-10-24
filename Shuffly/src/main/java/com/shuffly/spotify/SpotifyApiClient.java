package com.shuffly.spotify;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SpotifyApiClient {

    private final HttpClient client = HttpClient.newHttpClient();

    private HttpRequest.Builder authedRequest(String path) {
        return HttpRequest.newBuilder()
            .uri(URI.create(SpotifyConfig.API_BASE + path))
            .header("Authorization", "Bearer " + SpotifyAuth.getAccessToken());
    }

    /** Returns the current user's display name, or null on failure. */
    public String getCurrentUserName() throws Exception {
        HttpRequest request = authedRequest("/me").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("getCurrentUserName failed: " + response.body());
            return null;
        }

        JSONObject json = new JSONObject(response.body());
        return json.optString("display_name", json.optString("id", "Spotify User"));
    }

    /** Returns the raw JSON for the user's playlists. */
    public JSONArray getUserPlaylists() throws Exception {
        HttpRequest request = authedRequest("/me/playlists?limit=50").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("getUserPlaylists failed: " + response.body());
            return new JSONArray();
        }

        return new JSONObject(response.body()).getJSONArray("items");
    }

    /** Returns the raw JSON for the user's Liked Songs (first 50). */
    public JSONArray getLikedSongs() throws Exception {
        HttpRequest request = authedRequest("/me/tracks?limit=50").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("getLikedSongs failed: " + response.body());
            return new JSONArray();
        }

        return new JSONObject(response.body()).getJSONArray("items");
    }

    /** Returns the tracks inside a specific playlist. */
    public JSONArray getPlaylistTracks(String playlistId) throws Exception {
        HttpRequest request = authedRequest("/playlists/" + playlistId + "/tracks?limit=100").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("getPlaylistTracks failed: " + response.body());
            return new JSONArray();
        }

        return new JSONObject(response.body()).getJSONArray("items");
    }
}