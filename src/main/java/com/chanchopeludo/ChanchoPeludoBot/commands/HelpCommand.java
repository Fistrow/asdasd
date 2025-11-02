package com.chanchopeludo.ChanchoPeludoBot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.chanchopeludo.ChanchoPeludoBot.util.helpers.EmbedHelper.buildHelpEmbed;

@Component
public class HelpCommand implements Command{

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
}
