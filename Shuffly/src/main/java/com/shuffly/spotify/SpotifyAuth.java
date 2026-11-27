package com.shuffly.spotify;

import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class SpotifyAuth {

    private static String accessToken;
    private static String refreshToken;
    private static long tokenExpirationTime = 0;

    public static String getAccessToken() {
        return accessToken;
    }

    public static boolean isLoggedIn() {
        return accessToken != null && !accessToken.isBlank();
    }

    /**
     * Opens the browser for Spotify login and waits for the redirect.
     * Returns the access token, or null if login failed / was cancelled.
     */
    public static String login() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> codeRef = new AtomicReference<>();

        // Tiny local server to catch the redirect
        HttpServer server = HttpServer.create(new InetSocketAddress(8888), 0);
        server.createContext("/callback", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String code = null;

            if (query != null) {
                for (String param : query.split("&")) {
                    if (param.startsWith("code=")) {
                        code = param.substring(5);
                        break;
                    }
                }
            }

            String response = code != null
                ? "<html><body><h2>Login successful!</h2><p>You can close this tab and return to Shuffly.</p></body></html>"
                : "<html><body><h2>Login failed</h2></body></html>";

            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }

            codeRef.set(code);
            latch.countDown();
        });
        server.start();

        // Open the Spotify login page in the default browser
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(SpotifyConfig.AUTH_URL));
        } else {
            System.out.println("Open this URL in your browser:\n" + SpotifyConfig.AUTH_URL);
        }

        // Wait until the user finishes logging in
        latch.await();
        server.stop(0);

        String code = codeRef.get();
        if (code == null) {
            return null;
        }

        return exchangeCodeForTokens(code);
    }

    private static String exchangeCodeForTokens(String code) throws IOException, InterruptedException {
        String body = "grant_type=authorization_code"
            + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
            + "&redirect_uri=" + URLEncoder.encode(SpotifyConfig.REDIRECT_URI, StandardCharsets.UTF_8);

        String basicAuth = Base64.getEncoder().encodeToString(
            (SpotifyConfig.CLIENT_ID + ":" + SpotifyConfig.CLIENT_SECRET).getBytes(StandardCharsets.UTF_8)
        );

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(SpotifyConfig.TOKEN_URL))
            .header("Authorization", "Basic " + basicAuth)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("Token exchange failed: " + response.body());
            return null;
        }

        JSONObject json = new JSONObject(response.body());

        accessToken = json.getString("access_token");
        refreshToken = json.optString("refresh_token", null);
        
        // Calculate when the access token expires.
        long expiresIn = json.optLong("expires_in", 3600);
        tokenExpirationTime = System.currentTimeMillis()
                + (expiresIn * 1000L);

        TokenStorage.save(
                accessToken,
                refreshToken,
                tokenExpirationTime
        );

        return accessToken;
    }
    
    public static String refreshAccessToken() throws IOException, InterruptedException {
        if (refreshToken == null || refreshToken.isBlank()) {
            System.err.println("No refresh token available");
            return null;
        }

        String body = "grant_type=refresh_token"
            + "&refresh_token=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);

        String basicAuth = Base64.getEncoder().encodeToString(
            (SpotifyConfig.CLIENT_ID + ":" + SpotifyConfig.CLIENT_SECRET)
                .getBytes(StandardCharsets.UTF_8)
        );

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(SpotifyConfig.TOKEN_URL))
            .header("Authorization", "Basic " + basicAuth)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("Token refresh failed: " + response.body());
            accessToken = null;
            return null;
        }

        JSONObject json = new JSONObject(response.body());

        accessToken = json.getString("access_token");

        // Update the expiration time.
        long expiresIn = json.optLong("expires_in", 3600);
        tokenExpirationTime = System.currentTimeMillis()
                + (expiresIn * 1000L);

        // Spotify may return a new refresh token.
        if (json.has("refresh_token")
                && !json.isNull("refresh_token")
                && !json.getString("refresh_token").isBlank()) {
            refreshToken = json.getString("refresh_token");
        }

        TokenStorage.save(
                accessToken,
                refreshToken,
                tokenExpirationTime
        );

        System.out.println("Access token refreshed and saved.");
        return accessToken;
    }
    
    public static synchronized String getValidAccessToken() throws Exception {
        // Refresh if the token is missing or expires within the next minute.
        if (accessToken == null || accessToken.isBlank()
                || System.currentTimeMillis() >= tokenExpirationTime - 60_000L) {
            return refreshAccessToken();
        }

        return accessToken;
    }
    
    public static void loadSavedTokens() {
        try {
            JSONObject json = TokenStorage.load();

            if (json == null) {
                System.out.println("No saved Spotify authentication found.");
                return;
            }

            accessToken = json.optString("accessToken", null);
            refreshToken = json.optString("refreshToken", null);
            tokenExpirationTime = json.optLong("expirationTime", 0);

            System.out.println("Saved Spotify authentication loaded.");

        } catch (Exception e) {
            System.err.println("Could not load saved Spotify authentication:");
            e.printStackTrace();
        }
    }

    public static void logout() {
        accessToken = null;
        refreshToken = null;
        tokenExpirationTime = 0;

        try {
            TokenStorage.delete();
        } catch (Exception e) {
            System.err.println("Could not delete saved Spotify authentication:");
            e.printStackTrace();
        }
    }
}