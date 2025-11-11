package com.chanchopeludo.ChanchoPeludoBot.service.imp;

import com.chanchopeludo.ChanchoPeludoBot.dto.PlayResult;
import com.chanchopeludo.ChanchoPeludoBot.music.GuildMusicManager;
import com.chanchopeludo.ChanchoPeludoBot.dto.VideoInfo;
import com.chanchopeludo.ChanchoPeludoBot.music.TrackScheduler;
import com.chanchopeludo.ChanchoPeludoBot.service.MusicService;
import com.chanchopeludo.ChanchoPeludoBot.service.VideoInfoService;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.chanchopeludo.ChanchoPeludoBot.util.constants.MusicConstants.*;
import static com.chanchopeludo.ChanchoPeludoBot.util.helpers.EmbedHelper.buildNowPlayingEmbed;
import static com.chanchopeludo.ChanchoPeludoBot.util.helpers.EmbedHelper.buildQueueEmbed;

@Service
public class MusicServiceImp implements MusicService {

    private AudioPlayerManager playerManager;
    private final VideoInfoService videoInfoService;
    private final JDA jda;
    private final Map<Long, GuildMusicManager> musicManagers;
    private static final Logger logger = LoggerFactory.getLogger(MusicServiceImp.class);


    public MusicServiceImp(VideoInfoService videoInfoService, JDA jda) {
        this.musicManagers = new HashMap<>();
        this.videoInfoService = videoInfoService;
        this.jda = jda;
    }

    @PostConstruct
    private void init() {
        this.playerManager = new DefaultAudioPlayerManager();
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        GuildMusicManager musicManager = musicManagers.get(guild.getIdLong());
        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guild.getIdLong(), musicManager);
        }
        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        return musicManager;
    }

    @Override
    public CompletableFuture<PlayResult> loadAndPlay(long guildId, long voiceChannelId, String trackUrl) {
        CompletableFuture<PlayResult> futureResult = new CompletableFuture<>();

        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            futureResult.complete(new PlayResult(false, "Error: No se encontró el servidor."));
            return futureResult;
        }
        AudioChannel voiceChannel = guild.getChannelById(AudioChannel.class, voiceChannelId);
        if (voiceChannel == null) {
            futureResult.complete(new PlayResult(false, "Error: No se encontró el canal de voz."));
            return futureResult;
        }

        final GuildMusicManager musicManager = getGuildAudioPlayer(guild);

        videoInfoService.getVideoInfo(trackUrl)
                .thenAccept(info -> {
                    playerManager.loadItemOrdered(musicManager, info.url(), new AudioLoadResultHandler() {
                        @Override
                        public void trackLoaded(AudioTrack track) {
                            track.setUserData(info);
                            play(guild, musicManager, track, voiceChannel);
                            futureResult.complete(new PlayResult(true, MSG_TRACK_ADDED + info.title() + "**"));
                        }
                        @Override
                        public void playlistLoaded(AudioPlaylist playlist) {
                            play(guild, musicManager, playlist.getTracks().get(0), voiceChannel);
                            futureResult.complete(new PlayResult(true, MSG_PLAYLIST_ADDED + playlist.getName() + "**"));
                        }
                        @Override
                        public void noMatches() {
                            futureResult.complete(new PlayResult(false, MSG_NO_MATCHES_URL));
                        }
                        @Override
                        public void loadFailed(FriendlyException exception) {
                            futureResult.complete(new PlayResult(false, MSG_LOAD_FAILED + exception.getMessage()));
                        }
                    });
                })
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause();
                    logger.error("Error al buscar video", cause != null ? cause : ex);
                    futureResult.complete(new PlayResult(false, MSG_YOUTUBE_ERROR + (cause != null ? cause.getMessage() : ex.getMessage())));
                    return null;
                });

        return futureResult;
    }

    @Override
    public void skipTrack(MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        if (musicManager.getPlayer().getPlayingTrack() == null) {
            event.getChannel().sendMessage(MSG_SKIP_FAIL).queue();
            return;
        }

        musicManager.getScheduler().nextTrack();
        event.getChannel().sendMessage(MSG_SKIP_MUSIC).queue();
    }

    @Override
    public void stop(MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        musicManager.getScheduler().getQueue().clear();
        musicManager.getPlayer().stopTrack();
        event.getGuild().getAudioManager().closeAudioConnection();
        event.getChannel().sendMessage(MSG_STOP_MUSIC).queue();
    }

    @Override
    public void pause(MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        if (musicManager.getPlayer().isPaused()) {
            event.getChannel().sendMessage(MSG_ALREADY_PAUSED).queue();
        }

        musicManager.getPlayer().setPaused(true);
        event.getChannel().sendMessage(MSG_PAUSE_MUSIC).queue();
    }

    @Override
    public void resume(MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        if (!musicManager.getPlayer().isPaused()) {
            event.getChannel().sendMessage(MSG_NOT_PAUSED).queue();
            return;
        }

        musicManager.getPlayer().setPaused(false);
        event.getChannel().sendMessage(MSG_RESUME_MUSIC).queue();
    }

    @Override
    public void volume(MessageReceivedEvent event, int valueVolume) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        if (valueVolume < 0 || valueVolume > 100) {
            event.getChannel().sendMessage(MSG_INVALID_VALUE_VOLUME).queue();
            return;
        }

        musicManager.getPlayer().setVolume(valueVolume);
        event.getChannel().sendMessage(String.format(MSG_VOLUME_MUSIC, valueVolume)).queue();
    }

    @Override
    public void shuffle(MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        TrackScheduler scheduler = musicManager.getScheduler();

        if (scheduler.getQueue().isEmpty()) {
            event.getChannel().sendMessage(MSG_SHUFFLE_FAILED).queue();
            return;
        }

        scheduler.shuffle();

        event.getChannel().sendMessage(MSG_SHUFFLE_PLAYLIST).queue();
    }

    @Override
    public void nowPlaying(MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        AudioTrack currentTrack = musicManager.getPlayer().getPlayingTrack();

        if (currentTrack == null) {
            event.getChannel().sendMessage(MSG_NOTHING_PLAYING).queue();
            return;
        }

        MessageEmbed embed = buildNowPlayingEmbed(currentTrack);

        event.getChannel().sendMessageEmbeds(embed).queue();
    }

    @Override
    public void showQueue(MessageReceivedEvent event) {
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        TrackScheduler scheduler = musicManager.getScheduler();
        AudioTrack currentTrack = musicManager.getPlayer().getPlayingTrack();

        if (scheduler.getQueue().isEmpty() && currentTrack == null) {
            event.getChannel().sendMessage(MSG_QUEUE_EMPTY).queue();
            return;
        }

        List<AudioTrack> queueList = new ArrayList<>(scheduler.getQueue());

        int itemsPerPage = 10;
        int totalPages = (int) Math.ceil((double) queueList.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1;

        MessageEmbed embed = buildQueueEmbed(currentTrack, queueList, 1, totalPages, itemsPerPage);

        Button prevButton = Button.primary("queue:prev:1", "Anterior").withDisabled(true);
        Button nextButton = Button.primary("queue:next:1", "Siguiente").withDisabled(totalPages <= 1);

        event.getChannel().sendMessageEmbeds(embed)
                .setActionRow(prevButton, nextButton)
                .queue();
    }

    @Override
    public void handleQueueButton(ButtonInteractionEvent event) {
        String[] idParts = event.getComponentId().split(":");

        String action = idParts[1]; // "prev" o "next"
        int currentPage = Integer.parseInt(idParts[2]);

        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
        TrackScheduler scheduler = musicManager.getScheduler();
        List<AudioTrack> queueList = new ArrayList<>(scheduler.getQueue());
        AudioTrack playingTrack = musicManager.getPlayer().getPlayingTrack();

        int itemsPerPage = 10;
        int totalPages = (int) Math.ceil((double) queueList.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1;

        int newPage = currentPage;
        if (action.equals("next")) {
            newPage = Math.min(currentPage + 1, totalPages);
        } else if (action.equals("prev")) {
            newPage = Math.max(currentPage - 1, 1);
        }

        MessageEmbed newEmbed = buildQueueEmbed(playingTrack, queueList, newPage, totalPages, itemsPerPage);

        Button newPrevButton = Button.primary("queue:prev:" + newPage, "Anterior")
                .withDisabled(newPage == 1);
        Button newNextButton = Button.primary("queue:next:" + newPage, "Siguiente")
                .withDisabled(newPage >= totalPages);

        event.editMessageEmbeds(newEmbed)
                .setActionRow(newPrevButton, newNextButton)
                .queue();
    }

    @Override
    public void queueTrack(Guild guild, String trackUrl) {
        final GuildMusicManager musicManager = getGuildAudioPlayer(guild);

        videoInfoService.getVideoInfo(trackUrl)
                .thenAccept(info -> {
                    playerManager.loadItemOrdered(musicManager, info.url(), new AudioLoadResultHandler() {
                        @Override
                        public void trackLoaded(AudioTrack track) {
                            track.setUserData(info);
                            musicManager.getScheduler().queue(track);
                        }

                        @Override public void playlistLoaded(AudioPlaylist audioPlaylist) {}
                        @Override public void noMatches() { logger.warn("queueTrack no encontró coincidencias para: {}", trackUrl); }
                        @Override public void loadFailed(FriendlyException e) { logger.error("Fallo al cargar la canción en queueTrack: {}", info.url(), e); }
                    });
                })
                .exceptionally(ex -> {
                    logger.error("Error en queueTrack con yt-dlp para: '{}'", trackUrl, ex);
                    if (ex.getCause() instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                    return null;
                });
    }

    @Override
    public void playTrackSilently(MessageReceivedEvent event, String trackUrl) {
        final Guild guild = event.getGuild();
        final GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        final AudioChannel voiceChannel = event.getMember().getVoiceState().getChannel();

        videoInfoService.getVideoInfo(trackUrl)
                .thenAccept(info -> {
                    playerManager.loadItemOrdered(musicManager, info.url(), new AudioLoadResultHandler() {
                        @Override
                        public void trackLoaded(AudioTrack track) {
                            track.setUserData(info);
                            play(guild, musicManager, track, voiceChannel);
                        }

                        @Override
                        public void playlistLoaded(AudioPlaylist playlist) {
                            if (!playlist.getTracks().isEmpty()) {
                                play(guild, musicManager, playlist.getTracks().get(0), voiceChannel);
                            }
                        }

                        @Override public void noMatches() { logger.warn("playTrackSilently no encontró coincidencias para: {}", trackUrl); }
                        @Override public void loadFailed(FriendlyException exception) { logger.error("playTrackSilently falló al cargar: {}", trackUrl, exception); }
                    });
                })
                .exceptionally(ex -> {
                    logger.error("Error en playTrackSilently con yt-dlp para: '{}'", trackUrl, ex);
                    if (ex.getCause() instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                    return null;
                });
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, AudioChannel voiceChannel) {
        if (voiceChannel == null) {
            logger.warn("El usuario no estaba en un canal de voz al intentar reproducir.");
            return;
        }

        guild.getAudioManager().openAudioConnection(voiceChannel);
        guild.getAudioManager().setSelfDeafened(true);
        musicManager.getScheduler().queue(track);
    }
}