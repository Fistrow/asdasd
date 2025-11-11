package com.chanchopeludo.ChanchoPeludoBot.service.imp;

import com.chanchopeludo.ChanchoPeludoBot.commands.Command;
import com.chanchopeludo.ChanchoPeludoBot.service.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.chanchopeludo.ChanchoPeludoBot.util.constants.AppConstants.DEFAULT_PREFIX;

@Service
public class CommandManagerImpl implements CommandManager {

    private final Map<String, Command> slashCommandMap;

    private final Map<String, Command> textCommandMap;

    public CommandManagerImpl(List<Command> commandList) {

        this.slashCommandMap = commandList.stream()
                .collect(Collectors.toMap(Command::getName, command -> command));

        this.textCommandMap = new HashMap<>();
        for (Command command : commandList) {
            for (String name : command.getTextNames()) {
                this.textCommandMap.put(name.toLowerCase(), command);
            }
        }
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw().substring(DEFAULT_PREFIX.length()).trim();
        String[] parts = content.split("\\s+");
        String commandName = parts[0].toLowerCase();

        Command command = this.textCommandMap.get(commandName);

        if (command != null) {
            List<String> args = Arrays.asList(parts).subList(1, parts.length);
            command.executeText(event, args);
        }
    }

    @Override
    public void handleSlash(SlashCommandInteractionEvent event) {
        Command command = slashCommandMap.get(event.getName());
        if (command != null) {
            command.executeSlash(event);
        } else {
            event.reply("Comando desconocido.").setEphemeral(true).queue();
        }
    }

    public List<CommandData> getAllSlashCommandData() {
        return slashCommandMap.values().stream()
                .map(Command::getSlashCommandData)
                .collect(Collectors.toList());
    }
}