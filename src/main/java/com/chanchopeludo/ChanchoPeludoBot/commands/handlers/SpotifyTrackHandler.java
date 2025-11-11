package com.chanchopeludo.ChanchoPeludoBot.commands.handlers;

import com.chanchopeludo.ChanchoPeludoBot.service.MusicService;
import com.chanchopeludo.ChanchoPeludoBot.service.SpotifyService;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.chanchopeludo.ChanchoPeludoBot.util.constants.MusicConstants.MSG_SPOTIFY_FAILURE;
import static com.chanchopeludo.ChanchoPeludoBot.util.constants.MusicConstants.MSG_SPOTIFY_PROCESSING;

@Component
public class SpotifyTrackHandler implements InputHandler {

    private final MusicService musicService;
    private final SpotifyService spotifyService;
    private static final Logger logger = LoggerFactory.getLogger(SpotifyTrackHandler.class);

    public SpotifyTrackHandler(MusicService musicService, SpotifyService spotifyService) {
        this.musicService = musicService;
        this.spotifyService = spotifyService;
    }

    @Override
    public boolean canHandle(String input) {
        return input.contains("spotify.com") && input.contains("/track/");
    }

    @Override
    public void handle(MessageReceivedEvent event, String input) {
        logger.info("Procesando URL de track de Spotify para el servidor '{}': {}", event.getGuild().getId(), input);

        if (event.getMember().getVoiceState().getChannel() == null) {
            return;
        }

        event.getChannel().sendMessage(MSG_SPOTIFY_PROCESSING).queue();

        long guildId = event.getGuild().getIdLong();
        long channelId = event.getMember().getVoiceState().getChannel().getIdLong();

        spotifyService.getTrackFromUrlAsync(input).thenAccept(trackOptional -> {
            trackOptional.ifPresentOrElse(
                    track -> {
                        String finalInput = track.toYoutubeSearchQuery();
                        logger.info("Servidor '{}': URL de Spotify buscada: {}", event.getGuild().getId(), finalInput);

                        musicService.loadAndPlay(guildId, channelId, finalInput)
                                .thenAccept(playResult -> {
                                    event.getChannel().sendMessage(playResult.message()).queue();
                                });
                    },
                    () -> {
                        event.getChannel().sendMessage(MSG_SPOTIFY_FAILURE).queue();
                    }
            );
        }).exceptionally(ex -> {
            logger.error("Ocurrió una excepción al obtener la cancion para la URL: {}", input, ex);
            event.getChannel().sendMessage("Error al procesar el track de Spotify: " + ex.getMessage()).queue();
            return null;
        });
    }
}