package com.shuffly.spotify;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

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

        if (response.statusCode() == 403) {
            System.err.println("getPlaylistTracks forbidden for playlist: " + playlistId);
            return new JSONArray(); // empty instead of crashing
        }

        if (response.statusCode() != 200) {
            System.err.println("getPlaylistTracks failed: " + response.body());
            return new JSONArray();
        }

        return new JSONObject(response.body()).getJSONArray("items");
    }
    
    /** Get the user's active Spotify devices. */
    public JSONArray getDevices() throws Exception {
        HttpRequest request = authedRequest("/me/player/devices").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("getDevices failed: " + response.body());
            return new JSONArray();
        }
        return new JSONObject(response.body()).getJSONArray("devices");
    }

    /** Pick the first available device id, or null. */
    public String getActiveDeviceId() throws Exception {
        JSONArray devices = getDevices();
        for (int i = 0; i < devices.length(); i++) {
            JSONObject d = devices.getJSONObject(i);
            if (d.optBoolean("is_active", false)) {
                return d.getString("id");
            }
        }
        // fallback: first device
        if (devices.length() > 0) {
            return devices.getJSONObject(0).getString("id");
        }
        return null;
    }

    /** Start/resume playback of specific track URIs on a device. */
    public void playTracks(List<String> trackIds, String deviceId) throws Exception {
        JSONArray uris = new JSONArray();
        for (String id : trackIds) {
            uris.put("spotify:track:" + id);
        }

        JSONObject body = new JSONObject();
        body.put("uris", uris);

        String path = "/me/player/play";
        if (deviceId != null) {
            path += "?device_id=" + deviceId;
        }

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(SpotifyConfig.API_BASE + path))
            .header("Authorization", "Bearer " + SpotifyAuth.getAccessToken())
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204 && response.statusCode() != 200) {
            System.err.println("playTracks failed: " + response.statusCode() + " " + response.body());
        }
    }

    public void pause(String deviceId) throws Exception {
        String path = "/me/player/pause";
        if (deviceId != null) path += "?device_id=" + deviceId;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(SpotifyConfig.API_BASE + path))
            .header("Authorization", "Bearer " + SpotifyAuth.getAccessToken())
            .PUT(HttpRequest.BodyPublishers.noBody())
            .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void resume(String deviceId) throws Exception {
        String path = "/me/player/play";
        if (deviceId != null) path += "?device_id=" + deviceId;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(SpotifyConfig.API_BASE + path))
            .header("Authorization", "Bearer " + SpotifyAuth.getAccessToken())
            .PUT(HttpRequest.BodyPublishers.noBody())
            .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void next(String deviceId) throws Exception {
        String path = "/me/player/next";
        if (deviceId != null) path += "?device_id=" + deviceId;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(SpotifyConfig.API_BASE + path))
            .header("Authorization", "Bearer " + SpotifyAuth.getAccessToken())
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void previous(String deviceId) throws Exception {
        String path = "/me/player/previous";
        if (deviceId != null) path += "?device_id=" + deviceId;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(SpotifyConfig.API_BASE + path))
            .header("Authorization", "Bearer " + SpotifyAuth.getAccessToken())
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /** Seek to position in ms. */
    public void seek(long positionMs, String deviceId) throws Exception {
        String path = "/me/player/seek?position_ms=" + positionMs;
        if (deviceId != null) path += "&device_id=" + deviceId;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(SpotifyConfig.API_BASE + path))
            .header("Authorization", "Bearer " + SpotifyAuth.getAccessToken())
            .PUT(HttpRequest.BodyPublishers.noBody())
            .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Returns playback state:
     * is_playing, progress_ms, duration_ms, track name/artist/id
     * or null if nothing is playing.
     */
    public JSONObject getCurrentlyPlaying() throws Exception {
        HttpRequest request = authedRequest("/me/player/currently-playing").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 204 || response.body() == null || response.body().isBlank()) {
            return null; // nothing playing
        }
        if (response.statusCode() != 200) {
            System.err.println("getCurrentlyPlaying failed: " + response.body());
            return null;
        }
        return new JSONObject(response.body());
    }
    
    /** Fetch up to 50 artists at once: GET /v1/artists?ids=... */
    public JSONArray getArtists(List<String> artistIds) throws Exception {
        if (artistIds == null || artistIds.isEmpty()) return new JSONArray();

        // Spotify allows max 50 ids per request
        String ids = String.join(",", artistIds.stream().limit(50).toList());
        HttpRequest request = authedRequest("/artists?ids=" + ids).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("getArtists failed: " + response.body());
            return new JSONArray();
        }
        return new JSONObject(response.body()).getJSONArray("artists");
    }
}