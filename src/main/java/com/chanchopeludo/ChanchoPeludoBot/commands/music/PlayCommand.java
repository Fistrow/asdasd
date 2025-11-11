package com.chanchopeludo.ChanchoPeludoBot.commands.music;

import com.chanchopeludo.ChanchoPeludoBot.commands.Command;
import com.chanchopeludo.ChanchoPeludoBot.commands.music.handlers.InputHandler;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.chanchopeludo.ChanchoPeludoBot.util.constants.MusicConstants.MSG_NOT_IN_VOICE_CHANNEL;
import static com.chanchopeludo.ChanchoPeludoBot.util.constants.MusicConstants.MSG_PLAY_USAGE;

@Component
public class PlayCommand implements Command {

    private final List<InputHandler> handlers;

    public PlayCommand(List<InputHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public CommandData getSlashCommandData() {
        OptionData option = new OptionData(OptionType.STRING, "cancion", "La URL o nombre de la canción", true);
        return Commands.slash(getName(), "Reproduce una canción o búsqueda")
                .addOptions(option);
    }

    @Override
    public void executeSlash(SlashCommandInteractionEvent event) {

        //TODO: Por ahora, no funciona
        event.reply(MSG_PLAY_USAGE).setEphemeral(true).queue();
    }

    @Override
    public void executeText(MessageReceivedEvent event, List<String> args) {

        if(args.isEmpty()){
            event.getChannel().sendMessage(MSG_PLAY_USAGE).queue();
            return;
        }

        final AudioChannel userChannel = event.getMember().getVoiceState().getChannel();
        if(userChannel == null){
            event.getChannel().sendMessage(MSG_NOT_IN_VOICE_CHANNEL).queue();
            return;
        }

        String input = String.join(" ", args);

        for (InputHandler handler : handlers) {
            if (handler.canHandle(input)) {
                handler.handle(event, input);
                return;
            }
        }
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public List<String> getTextNames() {
        return Arrays.asList("play", "p");
    }
}