package com.chanchopeludo.ChanchoPeludoBot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public interface Command {

    /**
     * Define la estructura del comando de barra (/) para JDA.
     * (Descripción, opciones, etc.)
     *
     * @return Un objeto CommandData con la definición.
     */
    CommandData getSlashCommandData();

    /**
     * Lógica para ejecutar el comando cuando es invocado por un Slash Command (/).
     *
     * @param event El evento de la interacción.
     */
    void executeSlash(SlashCommandInteractionEvent event);

    /**
     * Lógica para ejecutar el comando cuando es invocado por un Mensaje de Texto (c!).
     *
     * @param event El evento del mensaje que disparó el comando.
     * @param args  La lista de argumentos que el usuario escribió después del comando.
     */
    void executeText(MessageReceivedEvent event, List<String> args);

    /**
     * Devuelve el nombre principal con el que se invoca el comando.
     * @return El nombre del comando (ej. "play", "perfil").
     */
    String getName();

    /**
     * Devuelve el nombre con el que se invoca un comando.
     * @return El nombre del comando (ej. "play", "p").
     */
    List<String> getTextNames();
}
