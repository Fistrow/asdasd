package com.chanchopeludo.ChanchoPeludoBot.util.helpers;

import com.chanchopeludo.ChanchoPeludoBot.dto.VideoInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;

import static com.chanchopeludo.ChanchoPeludoBot.util.constants.MusicConstants.*;

public class EmbedHelper {

    public static MessageEmbed buildQueueEmbed(AudioTrack playingTrack, List<AudioTrack> queueList, int page, int totalPages, int itemsPerPage) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(MSG_QUEUE_TITLE);

        if (playingTrack != null && playingTrack.getUserData() instanceof VideoInfo) {
            VideoInfo info = (VideoInfo) playingTrack.getUserData();
            eb.addField(MSG_NOW_PLAYING, String.format("`%s`", info.title()), false);
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
                    AudioTrack track = queueList.get(i);
                    String title;
                    if (track.getUserData() instanceof VideoInfo) {
                        title = ((VideoInfo) track.getUserData()).title();
                    } else {
                        title = track.getInfo().title;
                    }
                    queueString.append(String.format("`%d.` %s\n", (i + 1), title));
                }
                eb.addField(MSG_QUEUE_NEXT_UP, queueString.toString(), false);
            }
        }

        eb.setFooter(String.format(MSG_QUEUE_FOOTER, page, totalPages, queueList.size()));
        eb.setColor(0x1DB954);

        return eb.build();
    }
}
