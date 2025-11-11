package com.chanchopeludo.ChanchoPeludoBot.util.helpers;

import java.net.URI;
import java.net.URISyntaxException;

public class ValidationHelper {

    private ValidationHelper() {}

    /**
     * Verifica si una cadena de texto es una URL.
     *
     * @param url La cadena a verificar.
     * @return true si es una URL válida, false en caso contrario.
     */
    public static boolean isUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private static boolean isSpotifyUrl(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }
        return input.contains("spotify.com");
    }

    /**
     * Verifica si es una URL de un track de Spotify.
     *
     * @param input La url
     * @return true si es una URL de track de Spotify.
     */
    public static boolean isSpotifyTrack(String input) {
        return isSpotifyUrl(input) && input.contains("/track/");
    }

    /**
     * Verifica si es una URL de una playlist de Spotify.
     *
     * @param input La url
     * @return true si es una URL de playlist de Spotify.
     */
    public static boolean isSpotifyPlaylist(String input) {
        return isSpotifyUrl(input) && input.contains("/playlist/");
    }

    /**
     * Verifica si es un URL de youtube
     *
     * @param input La url
     * @return true si es una URL de youtube.
     */
    public static boolean isYoutubeUrl(String input) {
        return isUrl(input) && (input.contains("youtube.com") || input.contains("youtu.be"));
    }
}
