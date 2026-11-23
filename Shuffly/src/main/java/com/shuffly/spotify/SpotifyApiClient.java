
package com.shuffly.spotify;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SpotifyApiClient {

    private final HttpClient client = HttpClient.newHttpClient();

    /**
     * Sends an authenticated Spotify request.
     * If Spotify returns 401, refreshes the token and retries once.
     */
    private HttpResponse<String> sendWithAuth(
            String url,
            String method,
            HttpRequest.BodyPublisher body) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization",
                    "Bearer " + SpotifyAuth.getValidAccessToken())
            .method(method, body)
            .build();

        HttpResponse<String> response = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() == 401) {
            System.out.println("Got 401 — refreshing token...");

            if (SpotifyAuth.refreshAccessToken() == null) {
                return response;
            }

            request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization",
                        "Bearer " + SpotifyAuth.getValidAccessToken())
                .method(method, body)
                .build();

            response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );
        }

        return response;
    }

    /**
     * Simplified GET request using centralized authentication.
     */
    private HttpResponse<String> get(String path) throws Exception {
        return sendWithAuth(
            SpotifyConfig.API_BASE + path,
            "GET",
            HttpRequest.BodyPublishers.noBody()
        );
    }

    /**
     * Loads every page from a paginated Spotify endpoint.
     * Spotify provides the URL for the next page in the "next" field.
     */
    private JSONArray getAllPages(
            String initialPath,
            String operationName,
            boolean allowForbidden) throws Exception {

        JSONArray allItems = new JSONArray();
        String nextUrl = SpotifyConfig.API_BASE + initialPath;

        while (nextUrl != null && !nextUrl.isBlank()) {

            HttpResponse<String> response = sendWithAuth(
                nextUrl,
                "GET",
                HttpRequest.BodyPublishers.noBody()
            );

            if (response.statusCode() == 403 && allowForbidden) {
                System.err.println(
                    operationName + " forbidden: " + nextUrl
                );
                return allItems;
            }

            if (response.statusCode() != 200) {
                System.err.println(
                    operationName + " failed: "
                        + response.statusCode() + " "
                        + response.body()
                );
                return allItems;
            }

            JSONObject page = new JSONObject(response.body());

            JSONArray items = page.optJSONArray("items");

            if (items != null) {
                for (int i = 0; i < items.length(); i++) {
                    allItems.put(items.get(i));
                }
            }

            Object next = page.opt("next");

            if (next == null || next == JSONObject.NULL) {
                nextUrl = null;
            } else {
                nextUrl = next.toString();
            }
        }

        return allItems;
    }

    /** Returns the current user's display name, or null on failure. */
    public String getCurrentUserName() throws Exception {

        HttpResponse<String> response = get("/me");

        if (response.statusCode() != 200) {
            System.err.println(
                "getCurrentUserName failed: " + response.body()
            );
            return null;
        }

        JSONObject json = new JSONObject(response.body());

        return json.optString(
            "display_name",
            json.optString("id", "Spotify User")
        );
    }

    /**
     * Returns all playlists owned or followed by the current user.
     */
    public JSONArray getUserPlaylists() throws Exception {
        return getAllPages(
            "/me/playlists?limit=50",
            "getUserPlaylists",
            false
        );
    }

    /**
     * Returns all of the current user's Liked Songs.
     */
    public JSONArray getLikedSongs() throws Exception {
        return getAllPages(
            "/me/tracks?limit=50",
            "getLikedSongs",
            false
        );
    }

    /**
     * Returns all tracks inside a specific playlist.
     */
    public JSONArray getPlaylistTracks(String playlistId)
            throws Exception {

        return getAllPages(
            "/playlists/" + playlistId + "/tracks?limit=50",
            "getPlaylistTracks",
            true
        );
    }

    /**
     * Searches Spotify's catalog for tracks matching a query.
     */
    public JSONArray searchTracks(String query) throws Exception {

        String encodedQuery = URLEncoder.encode(
            query,
            StandardCharsets.UTF_8
        );

        String path = "/search?q=" + encodedQuery
            + "&type=track&limit=10";

        HttpResponse<String> response = get(path);

        if (response.statusCode() != 200) {
            System.err.println(
                "searchTracks failed: "
                    + response.statusCode() + " "
                    + response.body()
            );

            return new JSONArray();
        }

        JSONObject json = new JSONObject(response.body());

        return json.getJSONObject("tracks").getJSONArray("items");
    }

    /** Get the user's active Spotify devices. */
    public JSONArray getDevices() throws Exception {

        HttpResponse<String> response =
            get("/me/player/devices");

        if (response.statusCode() != 200) {
            System.err.println(
                "getDevices failed: " + response.body()
            );
            return new JSONArray();
        }

        return new JSONObject(response.body())
            .getJSONArray("devices");
    }

    /** Pick the first available device id, or null. */
    public String getActiveDeviceId() throws Exception {

        JSONArray devices = getDevices();

        for (int i = 0; i < devices.length(); i++) {
            JSONObject device = devices.getJSONObject(i);

            if (device.optBoolean("is_active", false)) {
                return device.getString("id");
            }
        }

        // Fallback to the first available Spotify device.
        if (devices.length() > 0) {
            return devices.getJSONObject(0).getString("id");
        }

        return null;
    }

    /** Start playback of specific track IDs on a Spotify device. */
    public void playTracks(
            List<String> trackIds,
            String deviceId) throws Exception {

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

        HttpResponse<String> response = sendWithAuth(
            SpotifyConfig.API_BASE + path,
            "PUT",
            HttpRequest.BodyPublishers.ofString(body.toString())
        );

        if (response.statusCode() != 204
                && response.statusCode() != 200) {

            System.err.println(
                "playTracks failed: "
                    + response.statusCode() + " "
                    + response.body()
            );
        }
    }

    public void pause(String deviceId) throws Exception {

        String path = "/me/player/pause";

        if (deviceId != null) {
            path += "?device_id=" + deviceId;
        }

        sendWithAuth(
            SpotifyConfig.API_BASE + path,
            "PUT",
            HttpRequest.BodyPublishers.noBody()
        );
    }

    public void resume(String deviceId) throws Exception {

        String path = "/me/player/play";

        if (deviceId != null) {
            path += "?device_id=" + deviceId;
        }

        sendWithAuth(
            SpotifyConfig.API_BASE + path,
            "PUT",
            HttpRequest.BodyPublishers.noBody()
        );
    }

    public void next(String deviceId) throws Exception {

        String path = "/me/player/next";

        if (deviceId != null) {
            path += "?device_id=" + deviceId;
        }

        sendWithAuth(
            SpotifyConfig.API_BASE + path,
            "POST",
            HttpRequest.BodyPublishers.noBody()
        );
    }

    public void previous(String deviceId) throws Exception {

        String path = "/me/player/previous";

        if (deviceId != null) {
            path += "?device_id=" + deviceId;
        }

        sendWithAuth(
            SpotifyConfig.API_BASE + path,
            "POST",
            HttpRequest.BodyPublishers.noBody()
        );
    }

    /** Seek to a playback position in milliseconds. */
    public void seek(
            long positionMs,
            String deviceId) throws Exception {

        String path =
            "/me/player/seek?position_ms=" + positionMs;

        if (deviceId != null) {
            path += "&device_id=" + deviceId;
        }

        sendWithAuth(
            SpotifyConfig.API_BASE + path,
            "PUT",
            HttpRequest.BodyPublishers.noBody()
        );
    }

    /**
     * Returns the current Spotify playback state,
     * or null if nothing is playing.
     */
    public JSONObject getCurrentlyPlaying() throws Exception {

        HttpResponse<String> response =
            get("/me/player/currently-playing");

        if (response.statusCode() == 204
                || response.body() == null
                || response.body().isBlank()) {

            return null;
        }

        if (response.statusCode() != 200) {
            System.err.println(
                "getCurrentlyPlaying failed: "
                    + response.statusCode() + " "
                    + response.body()
            );
            return null;
        }

        return new JSONObject(response.body());
    }
}
