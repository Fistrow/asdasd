package com.chanchopeludo.ChanchoPeludoBot.service;

import com.chanchopeludo.ChanchoPeludoBot.model.PlayListItemEntity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public interface PlayListService {

    void createPlayList(String name, String serverId, String creatorId);

    void addTrackToPlayList(String playlistName, String serverId, String trackUrlQuery);

    void deletePlayList(String name, String serverId);

    void removeTrack(String playlistName, int trackOrder, String serverId);

    List<PlayListItemEntity> listPlayLists();

    void loadPlayList(String name, MessageReceivedEvent event);

}
