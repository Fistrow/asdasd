package com.chanchopeludo.ChanchoPeludoBot.service;

import com.chanchopeludo.ChanchoPeludoBot.dto.PlayResult;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.CompletableFuture;

public interface MusicService {

    /**
     * Carga y reproduce una canción o playlist
     *
     * @param guildId        ID del servidor (Guild).
     * @param voiceChannelId ID del canal de voz al que conectarse.
     * @param trackUrl       La URL de la canción a reproducir.
     * @return Un CompletableFuture que nos retorna un PlayResult con el estado y mensaje
     */
    CompletableFuture<PlayResult> loadAndPlay(long guildId, long voiceChannelId, String trackUrl);

    /**
     * Saltea la canción que se encuentra reproduciendo y comienza la siguiente en la cola.
     *
     * @param guildId ID del servidor (Guild).
     * @return Un PlayResult con el estado y mensaje
     */
    PlayResult skipTrack(long guildId);

    /**
     * Detiene la reproducción de la música por completo, limpia la cola de canciones y desconecta al bot del canal de voz
     *
     * @param guildId ID del servidor (Guild).
     * @return Un PlayResult con el estado y mensaje
     */
    PlayResult stop(long guildId);

    /**
     * Pausa la reproducción de la cancion actual.
     *
     * @param guildId ID del servidor (Guild).
     * @return Un PlayResult con el estado y mensaje
     */
    PlayResult pause(long guildId);

    /**
     * Reanuda la reproducción de la canción que estaba en pausa.
     *
     * @param guildId ID del servidor (Guild).
     * @return Un PlayResult con el estado y mensaje
     */
    PlayResult resume(long guildId);

    /**
     * Muestra la lista de reproducciones de canciones.
     *
     * @param event El evento del mensaje que inició la acción (ej: "c!queue").
     */
    void showQueue(MessageReceivedEvent event);

    /**
     * Busca y añade una canción a la cola de forma silenciosa (sin enviar mensajes al canal).
     * procesar las canciones de una playlist en segundo plano sin generar spam.
     *
     * @param guildId  ID del servidor (Guild).
     * @param trackUrl La URL de la cancion.
     * @return Un CompletableFuture que nos retorna un PlayResult con el estado y mensaje
     */
    CompletableFuture<PlayResult> queueTrack(long guildId, String trackUrl);

    /**
     * Inicia la reproducción de una canción de forma silenciosa (sin enviar mensajes al canal).
     * Si ya hay una canción en reproducción, la añade a la cola. Usado para la primera canción de una playlist.
     *
     * @param guildId        ID del servidor (Guild).
     * @param voiceChannelId ID del canal de voz al que conectarse.
     * @param trackUrl       La URL de la cancion.
     * @return Un CompletableFuture que nos retorna un PlayResult con el estado y mensaje
     */
    CompletableFuture<PlayResult> playTrackSilently(long guildId, long voiceChannelId, String trackUrl);

    /**
     * Ajusta el volumen de reproducción del bot de música.
     *
     * @param guildId     ID del servidor (Guild).
     * @param valueVolume El nuevo nivel de volumen, expresado como un porcentaje (0 a 100).
     *                    Un valor de 100 representa el volumen máximo, mientras que 0 silencia la reproducción.
     */
    PlayResult volume(long guildId, int valueVolume);

    /**
     * Mezcla la cola de reproducción actual.
     *
     * @param guildId ID del servidor (Guild).
     * @return Un PlayResult con el estado y mensaje
     */
    PlayResult shuffle(long guildId);

    /**
     * Muestra que canción se encuentra reproduciendo en este momento.
     *
     * @param event El evento del mensaje que inició la acción (ej: "c!nowplaying" o "c!np").
     */
    void nowPlaying(MessageReceivedEvent event);

    void handleQueueButton(ButtonInteractionEvent event);
}
