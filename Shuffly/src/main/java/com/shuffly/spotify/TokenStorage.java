
package com.shuffly.spotify;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TokenStorage {

    private static final Path DIRECTORY =
            Path.of(System.getProperty("user.home"), ".shuffly");

    private static final Path TOKEN_FILE =
            DIRECTORY.resolve("tokens.json");

    public static void save(String accessToken, String refreshToken,
                            long expirationTime) throws IOException {

        Files.createDirectories(DIRECTORY);

        JSONObject json = new JSONObject();

        json.put("accessToken", accessToken);
        json.put("refreshToken", refreshToken);
        json.put("expirationTime", expirationTime);

        Files.writeString(TOKEN_FILE, json.toString(2));

        System.out.println("Spotify authentication saved.");
    }

    public static JSONObject load() throws IOException {

        if (!Files.exists(TOKEN_FILE)) {
            return null;
        }

        return new JSONObject(Files.readString(TOKEN_FILE));
    }

    public static void delete() throws IOException {

        Files.deleteIfExists(TOKEN_FILE);

        System.out.println("Spotify authentication deleted.");
    }
    
    public static void saveUsername(String username) throws IOException {
        JSONObject json = load();

        if (json == null) {
            json = new JSONObject();
        }

        json.put("username", username);

        Files.createDirectories(DIRECTORY);
        Files.writeString(TOKEN_FILE, json.toString(2));
    }

    public static String loadUsername() throws IOException {
        JSONObject json = load();

        if (json == null) {
            return null;
        }

        return json.optString("username", null);
    }
}
