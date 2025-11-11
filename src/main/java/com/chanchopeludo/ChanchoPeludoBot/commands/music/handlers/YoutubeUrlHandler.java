package com.chanchopeludo.ChanchoPeludoBot.commands.music.handlers;

import com.chanchopeludo.ChanchoPeludoBot.service.MusicService;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

// ¡Importa tus constantes!
import static com.chanchopeludo.ChanchoPeludoBot.util.constants.MusicConstants.*;
import static com.chanchopeludo.ChanchoPeludoBot.util.helpers.ValidationHelper.isUrl;

@Component
public class YoutubeUrlHandler implements InputHandler{

    private final MusicService musicService;

    public YoutubeUrlHandler(MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public boolean canHandle(String input) {
        return isUrl(input) && (input.contains("youtube.com") || input.contains("youtu.be"));
    }

    @Override
    public void handle(MessageReceivedEvent event, String input) {

        if (event.getMember().getVoiceState().getChannel() == null) {
            return;
        }

        long guildId = event.getGuild().getIdLong();
        long channelId = event.getMember().getVoiceState().getChannel().getIdLong();

        event.getChannel().sendMessage(MSG_SEARCH_MUSIC).queue();

        musicService.loadAndPlay(guildId, channelId, input)
                .thenAccept(result -> {
                    event.getChannel().sendMessage(result.message()).queue();
                });
    }
}