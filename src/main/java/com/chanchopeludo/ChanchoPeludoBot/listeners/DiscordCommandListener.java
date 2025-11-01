package com.chanchopeludo.ChanchoPeludoBot.listeners;

import com.chanchopeludo.ChanchoPeludoBot.service.CommandManager;
import com.chanchopeludo.ChanchoPeludoBot.service.MusicService;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import static com.chanchopeludo.ChanchoPeludoBot.util.constants.AppConstants.DEFAULT_PREFIX;

@Component
public class DiscordCommandListener extends ListenerAdapter {

    private final JDA jda;
    private final CommandManager commandManager;
    private final MusicService musicService;

    public DiscordCommandListener(JDA jda, CommandManager commandManager, MusicService musicService) {
        this.jda = jda;
        this.commandManager = commandManager;
        this.musicService = musicService;
    }

    @PostConstruct
    public void register() {
        jda.addEventListener(this);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || !event.getMessage().getContentRaw().startsWith(DEFAULT_PREFIX)) {
            return;
        }

        commandManager.handle(event);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String componentId = event.getComponentId();

        if (componentId.startsWith("queue:")) {
            musicService.handleQueueButton(event);
        }
    }
}