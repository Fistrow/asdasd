package com.chanchopeludo.ChanchoPeludoBot.listeners;

import com.chanchopeludo.ChanchoPeludoBot.service.CommandManager;
import com.chanchopeludo.ChanchoPeludoBot.service.MusicService;
import com.chanchopeludo.ChanchoPeludoBot.service.UserService;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Component;

import static com.chanchopeludo.ChanchoPeludoBot.util.constants.AppConstants.DEFAULT_PREFIX;
import static com.chanchopeludo.ChanchoPeludoBot.util.constants.XpConstants.XP_PER_MESSAGE;
import static com.chanchopeludo.ChanchoPeludoBot.util.helpers.EmbedHelper.buildHelpEmbed;

@Component
public class DiscordCommandListener extends ListenerAdapter {

    private final JDA jda;
    private final CommandManager commandManager;
    private final MusicService musicService;
    private final UserService userService;

    public DiscordCommandListener(JDA jda, CommandManager commandManager, MusicService musicService, UserService userService) {
        this.jda = jda;
        this.commandManager = commandManager;
        this.musicService = musicService;
        this.userService = userService;
    }

    @PostConstruct
    public void register() {
        jda.addEventListener(this);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        commandManager.handleSlash(event);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || !event.getMessage().getContentRaw().startsWith(DEFAULT_PREFIX)) {
            return;
        }

        String userId = event.getAuthor().getId();
        String serverId = event.getGuild().getId();

        userService.addExp(userId, serverId, XP_PER_MESSAGE);

        commandManager.handle(event);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String componentId = event.getComponentId();

        if (componentId.startsWith("queue:")) {
            musicService.handleQueueButton(event);

        } else if (componentId.startsWith("help:")) {
            String[] idParts = componentId.split(":");
            String action = idParts[1];
            int currentPage = Integer.parseInt(idParts[2]);

            int totalPages = 3;

            int newPage = currentPage;
            if (action.equals("next")) {
                newPage = Math.min(currentPage + 1, totalPages);
            } else if (action.equals("prev")) {
                newPage = Math.max(currentPage - 1, 1);
            }

            MessageEmbed newEmbed = buildHelpEmbed(
                    event.getJDA().getSelfUser(),
                    newPage,
                    totalPages
            );

            Button newPrevButton = Button.primary("help:prev:" + newPage, "Anterior")
                    .withDisabled(newPage == 1);
            Button newNextButton = Button.primary("help:next:" + newPage, "Siguiente")
                    .withDisabled(newPage >= totalPages);

            event.editMessageEmbeds(newEmbed)
                    .setActionRow(newPrevButton, newNextButton)
                    .queue();
        }
    }
}