package com.chanchopeludo.ChanchoPeludoBot.commands.handlers;

import com.chanchopeludo.ChanchoPeludoBot.service.MusicService;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import static com.chanchopeludo.ChanchoPeludoBot.util.constants.MusicConstants.*;
import static com.chanchopeludo.ChanchoPeludoBot.util.helpers.ValidationHelper.isUrl;

@Component
public class SearchHandler implements InputHandler {
    private final MusicService musicService;

    public SearchHandler(MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public boolean canHandle(String input) {
        return !isUrl(input);
    }

    @Override
    public void handle(MessageReceivedEvent event, String input) {

        if (event.getMember().getVoiceState().getChannel() == null) {
            return;
        }

        String search = "ytsearch:" + input;

        long guildId = event.getGuild().getIdLong();
        long channelId = event.getMember().getVoiceState().getChannel().getIdLong();

        event.getChannel().sendMessage(MSG_SEARCH_MUSIC).queue();

        musicService.loadAndPlay(guildId, channelId, search)
                .thenAccept(result -> {
                    event.getChannel().sendMessage(result.message()).queue();
                });
    }
}