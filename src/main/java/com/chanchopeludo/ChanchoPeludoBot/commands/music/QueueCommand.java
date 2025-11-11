package com.chanchopeludo.ChanchoPeludoBot.commands.music;

import com.chanchopeludo.ChanchoPeludoBot.commands.Command;
import com.chanchopeludo.ChanchoPeludoBot.service.MusicService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

//@Component
public class QueueCommand implements Command {
    @Override
    public CommandData getSlashCommandData() {
        return null;
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {

    }

    @Override
    public void executeText(MessageReceivedEvent event, List<String> args) {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public List<String> getTextNames() {
        return List.of();
    }
/*

    private final MusicService musicService;

    public QueueCommand(MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        musicService.showQueue(event);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("queue");
    }
 */
}