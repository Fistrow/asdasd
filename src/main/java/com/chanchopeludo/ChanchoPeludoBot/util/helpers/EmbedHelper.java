package com.chanchopeludo.ChanchoPeludoBot.util.helpers;

import com.chanchopeludo.ChanchoPeludoBot.dto.AudioTrackInfo;
import com.chanchopeludo.ChanchoPeludoBot.dto.QueueState;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;

import java.awt.*;
import java.util.List;

import static com.chanchopeludo.ChanchoPeludoBot.util.constants.CommandConstants.MSG_HELP_FOOTER;
import static com.chanchopeludo.ChanchoPeludoBot.util.constants.MusicConstants.*;

public class EmbedHelper {

    public static MessageEmbed buildQueueEmbed(QueueState state, int page, int itemsPerPage) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(MSG_QUEUE_TITLE);

        AudioTrackInfo playingTrack = state.getNowPlaying().orElse(null);
        List<AudioTrackInfo> queueList = state.queue();

        if (playingTrack != null) {
            eb.addField(MSG_NOW_PLAYING, String.format("`%s`", playingTrack.title()), false);
        }

        if (queueList.isEmpty()) {
            eb.setDescription(MSG_QUEUE_EMPTY);
        } else {
            int start = (page - 1) * itemsPerPage;

            if (start >= queueList.size()) {
                eb.setDescription("\nNo hay más canciones en esta página.");
            } else {
                int end = Math.min(start + itemsPerPage, queueList.size());
                StringBuilder queueString = new StringBuilder();

                for (int i = start; i < end; i++) {
                    AudioTrackInfo track = queueList.get(i);
                    queueString.append(String.format("`%d.` %s\n", (i + 1), track.title()));
                }
                eb.addField(MSG_QUEUE_NEXT_UP, queueString.toString(), false);
            }
        }

        int totalPages = (int) Math.ceil((double) queueList.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1;

        eb.setFooter(String.format(MSG_QUEUE_FOOTER, page, totalPages, queueList.size()));
        eb.setColor(0x1DB954);

        return eb.build();
    }

    private static String formatDuration(long milliseconds) {
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long hours = (milliseconds / (1000 * 60 * 60)) % 24;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    public static MessageEmbed buildNowPlayingEmbed(AudioTrackInfo currentTrack, long currentPosition) {
        String title = currentTrack.title();
        String duration = formatDuration(currentTrack.durationMs());
        String currentPosStr = formatDuration(currentPosition);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(MSG_NOW_PLAYING);
        eb.setDescription(String.format("**[%s](%s)**", title, currentTrack.url()));
        eb.setColor(0x1ED760);
        eb.addField("Progreso", String.format("%s / %s", currentPosStr, duration), true);

        return eb.build();
    }

    public static MessageEmbed buildHelpEmbed(SelfUser botUser, int page, int totalPages) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("📜 Lista de comandos");
        eb.setDescription("El prefijo para utilizar el bot es `c!`.");

        eb.setThumbnail(botUser.getEffectiveAvatarUrl());
        eb.setColor(0x3498DB);

        switch (page) {
            case 1:
                eb.setDescription("** Comandos de música **");
                eb.addField("🎶 Comandos de Música",
                        "`play (o p)`: Reproduce o añade una canción.\n" +
                                "`skip`: Salta a la siguiente canción.\n" +
                                "`stop`: Detiene la música y limpia la cola.\n" +
                                "`pause`: Pausa la reproducción.\n" +
                                "`resume`: Reanuda la reproducción.\n" +
                                "`queue`: Muestra la cola de canciones.\n" +
                                "`nowplaying (o np)`: Muestra la canción actual.\n" +
                                "`shuffle`: Mezcla la cola.\n" +
                                "`volume`: Ajusta el volumen (ej. `c!volume 50`).",
                        false);
                break;
        }

        eb.setFooter(String.format(MSG_HELP_FOOTER, page, totalPages), botUser.getEffectiveAvatarUrl());

        return eb.build();
    }

    /**
     * Crea un Embed de éxito genérico.
     *
     * @param title   El título del embed.
     * @param message El mensaje de descripción.
     * @return El MessageEmbed construido.
     */
    public static MessageEmbed buildSuccessEmbed(String title, String message) {
        return new EmbedBuilder()
                .setTitle("✅ " + title)
                .setDescription(message)
                .setColor(new Color(0x4CAF50))
                .build();
    }

    /**
     * Crea un Embed de error genérico.
     *
     * @param title   El título del error.
     * @param message El mensaje de descripción (ej. la excepción).
     * @return El MessageEmbed construido.
     */
    public static MessageEmbed buildErrorEmbed(String title, String message) {
        return new EmbedBuilder()
                .setTitle("⚠️ " + title)
                .setDescription(message)
                .setColor(Color.RED)
                .build();
    }
}
