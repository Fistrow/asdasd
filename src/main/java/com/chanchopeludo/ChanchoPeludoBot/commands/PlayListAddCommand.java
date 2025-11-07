package com.chanchopeludo.ChanchoPeludoBot.commands;

import com.chanchopeludo.ChanchoPeludoBot.dto.SpotifyTrack;
import com.chanchopeludo.ChanchoPeludoBot.music.PlayerManager;
import com.chanchopeludo.ChanchoPeludoBot.service.PlayListService;
import com.chanchopeludo.ChanchoPeludoBot.service.SpotifyService;
import com.chanchopeludo.ChanchoPeludoBot.util.helpers.ValidationHelper;
import jakarta.persistence.EntityNotFoundException;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.chanchopeludo.ChanchoPeludoBot.util.constants.GenericConstants.TITLE_ERROR_MISSING_ARGS;
import static com.chanchopeludo.ChanchoPeludoBot.util.constants.PlayListConstants.*;
import static com.chanchopeludo.ChanchoPeludoBot.util.helpers.EmbedHelper.buildErrorEmbed;
import static com.chanchopeludo.ChanchoPeludoBot.util.helpers.EmbedHelper.buildSuccessEmbed;

@Component
public class PlayListAddCommand implements Command {

    private final PlayListService playListService;
    private final SpotifyService spotifyService;

    public PlayListAddCommand(PlayListService playListService, SpotifyService spotifyService) {
        this.playListService = playListService;
        this.spotifyService = spotifyService;
    }


    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        if (args.size() < 2) {
            MessageEmbed embed = buildErrorEmbed(TITLE_ERROR_MISSING_ARGS, PLAYLIST_USAGE_ADD);
            event.getChannel().sendMessageEmbeds(embed).queue();
            return;
        }

        String playlistName = args.get(0);
        String trackQuery = args.subList(1, args.size())
                .stream()
                .collect(Collectors.joining(" "));

        String serverId = event.getGuild().getId();

        String title;
        String trackIdentifier;

        try {
            if (trackQuery.contains("spotify.com") && trackQuery.contains("/track/")) {

                Optional<SpotifyTrack> optTrack = spotifyService.getTrackFromUrlAsync(trackQuery).join();

                SpotifyTrack track = optTrack.orElseThrow(
                        () -> new EntityNotFoundException("No se pudo encontrar la canción en Spotify con esa URL.")
                );

                title = track.name();
                trackIdentifier = track.toYoutubeSearchQuery();


            } else if (trackQuery.contains("spotify.com") && trackQuery.contains("/playlist/")) {
                throw new IllegalArgumentException("No puedes añadir una playlist de Spotify entera. Añade las canciones una por una.");
            } else {
                title = trackQuery;
                trackIdentifier = "ytsearch:" + trackQuery;
            }

            playListService.addTrackToPlayList(playlistName, serverId, title, trackIdentifier);

            MessageEmbed embed = buildSuccessEmbed(
                    TITLE_TRACK_ADDED,
                    String.format(DESC_TRACK_ADDED, title, playlistName)
            );
            event.getChannel().sendMessageEmbeds(embed).queue();
        } catch (Exception e) {
            MessageEmbed embed = buildErrorEmbed(TITLE_ERROR_PLAYLIST_ADD, e.getMessage());
            event.getChannel().sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("playlist-add", "pl-add");
    }
}
