package com.shuffly.spotify;

public class SpotifyConfig {
    public static final String CLIENT_ID = "a5afe23d090a463c82d1b2c2eb5fbb08";
    public static final String CLIENT_SECRET = "c223c72f2874466f8018dad30053789c";
    public static final String REDIRECT_URI = "http://127.0.0.1:8888/callback";

    public static final String SCOPES = String.join(" ",
        "user-library-read",
        "playlist-read-private",
        "playlist-read-collaborative",
        "user-read-playback-state",
        "user-modify-playback-state",
        "user-read-currently-playing",
        "user-read-email"
    );

    public static final String AUTH_URL =
        "https://accounts.spotify.com/authorize" +
        "?client_id=" + CLIENT_ID +
        "&response_type=code" +
        "&redirect_uri=" + REDIRECT_URI +
        "&scope=" + SCOPES.replace(" ", "%20");

    public static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    public static final String API_BASE = "https://api.spotify.com/v1";
}