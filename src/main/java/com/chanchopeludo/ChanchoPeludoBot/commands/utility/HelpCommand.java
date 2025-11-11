package com.chanchopeludo.ChanchoPeludoBot.commands.utility;

import com.chanchopeludo.ChanchoPeludoBot.commands.Command;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.chanchopeludo.ChanchoPeludoBot.util.helpers.EmbedHelper.buildHelpEmbed;

//@Component
public class HelpCommand implements Command {
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

    private final int TOTAL_PAGES = 3;

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        int currentPage = 1;

        MessageEmbed helpEmbed = buildHelpEmbed(
                event.getJDA().getSelfUser(),
                currentPage,
                TOTAL_PAGES
        );

        Button prevButton = Button.primary("help:prev:" + currentPage, "Anterior").withDisabled(true);
        Button nextButton = Button.primary("help:next:" + currentPage, "Siguiente").withDisabled(false);

        event.getChannel().sendMessageEmbeds(helpEmbed)
                .setActionRow(prevButton, nextButton)
                .queue();
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("help");
    }


 */
}
