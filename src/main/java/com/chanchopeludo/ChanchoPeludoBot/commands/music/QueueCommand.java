package com.chanchopeludo.ChanchoPeludoBot.commands.music;

import com.chanchopeludo.ChanchoPeludoBot.commands.Command;
import com.chanchopeludo.ChanchoPeludoBot.dto.QueueState;
import com.chanchopeludo.ChanchoPeludoBot.service.MusicService;
import com.chanchopeludo.ChanchoPeludoBot.util.helpers.EmbedHelper;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.chanchopeludo.ChanchoPeludoBot.util.constants.MusicConstants.MSG_NOT_IN_VOICE_CHANNEL;
import static com.chanchopeludo.ChanchoPeludoBot.util.constants.MusicConstants.MSG_QUEUE_EMPTY;

@Component
public class QueueCommand implements Command {

    private final MusicService musicService;

    public QueueCommand(MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public CommandData getSlashCommandData() {
        return Commands.slash(getName(), "Muestra la cola de reproducción");
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {
        if (event.getMember().getVoiceState().getChannel() == null) {
            event.reply(MSG_NOT_IN_VOICE_CHANNEL).setEphemeral(true).queue();
            return;
        }

        QueueState state = musicService.getQueueState(event.getGuild().getIdLong());

        if (state.isEmpty()) {
            event.reply(MSG_QUEUE_EMPTY).setEphemeral(true).queue();
            return;
        }

        int itemsPerPage = 10;
        int totalPages = (int) Math.ceil((double) state.queue().size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1;

        MessageEmbed embed = EmbedHelper.buildQueueEmbed(state, 1, itemsPerPage);

        Button prevButton = Button.primary("queue:prev:1", "Anterior").withDisabled(true);
        Button nextButton = Button.primary("queue:next:1", "Siguiente").withDisabled(totalPages <= 1);

        event.replyEmbeds(embed).setActionRow(prevButton, nextButton).queue();
    }

    @Override
    public void executeText(MessageReceivedEvent event, List<String> args) {
        if (event.getMember().getVoiceState().getChannel() == null) {
            event.getChannel().sendMessage(MSG_NOT_IN_VOICE_CHANNEL).queue();
            return;
        }

        QueueState state = musicService.getQueueState(event.getGuild().getIdLong());

        if (state.isEmpty()) {
            event.getChannel().sendMessage(MSG_QUEUE_EMPTY).queue();
            return;
        }

        int itemsPerPage = 10;
        int totalPages = (int) Math.ceil((double) state.queue().size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1;

        MessageEmbed embed = EmbedHelper.buildQueueEmbed(state, 1, itemsPerPage);

        Button prevButton = Button.primary("queue:prev:1", "Anterior").withDisabled(true);
        Button nextButton = Button.primary("queue:next:1", "Siguiente").withDisabled(totalPages <= 1);

        event.getChannel().sendMessageEmbeds(embed).setActionRow(prevButton, nextButton).queue();
    }

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public List<String> getTextNames() {
        return Arrays.asList("queue", "q");
    }
}