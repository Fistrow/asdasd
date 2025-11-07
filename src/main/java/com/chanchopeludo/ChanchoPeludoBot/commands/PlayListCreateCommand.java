package com.chanchopeludo.ChanchoPeludoBot.commands;

import com.chanchopeludo.ChanchoPeludoBot.service.PlayListService;
import com.chanchopeludo.ChanchoPeludoBot.util.helpers.EmbedHelper;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.chanchopeludo.ChanchoPeludoBot.util.constants.GenericConstants.TITLE_ERROR_MISSING_ARGS;
import static com.chanchopeludo.ChanchoPeludoBot.util.constants.PlayListConstants.*;

@Component
public class PlayListCreateCommand implements Command {

    private final PlayListService playListService;

    public PlayListCreateCommand(PlayListService playListService) {
        this.playListService = playListService;
    }

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        if (args.isEmpty()) {
            MessageEmbed embed = EmbedHelper.buildErrorEmbed(
                    TITLE_ERROR_MISSING_ARGS,
                    PLAYLIST_USAGE_CREATE);
            event.getChannel().sendMessageEmbeds(embed).queue();
            return;
        }

        String playlistName = String.join(" ", args);
        String serverId = event.getGuild().getId();
        String creatorId = event.getAuthor().getId();

        try {
            playListService.createPlayList(playlistName, serverId, creatorId);

            MessageEmbed embed = EmbedHelper.buildSuccessEmbed(
                    TITLE_PLAYLIST_CREATED,
                    String.format(DESC_PLAYLIST_CREATED, playlistName)
            );
            event.getChannel().sendMessageEmbeds(embed).queue();
        } catch (Exception e) {

            MessageEmbed embed = EmbedHelper.buildErrorEmbed(TITLE_ERROR_PLAYLIST_CREATE,
                    e.getMessage());
            event.getChannel().sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("playlist-create", "pl-create");
    }
}
