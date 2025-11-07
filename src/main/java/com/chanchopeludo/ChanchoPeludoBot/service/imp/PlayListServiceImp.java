package com.chanchopeludo.ChanchoPeludoBot.service.imp;

import com.chanchopeludo.ChanchoPeludoBot.model.PlayListEntity;
import com.chanchopeludo.ChanchoPeludoBot.model.PlayListItemEntity;
import com.chanchopeludo.ChanchoPeludoBot.model.ServerEntity;
import com.chanchopeludo.ChanchoPeludoBot.model.UserEntity;
import com.chanchopeludo.ChanchoPeludoBot.repository.PlayListItemRepository;
import com.chanchopeludo.ChanchoPeludoBot.repository.PlayListRepository;
import com.chanchopeludo.ChanchoPeludoBot.repository.ServerRepository;
import com.chanchopeludo.ChanchoPeludoBot.repository.UserRepository;
import com.chanchopeludo.ChanchoPeludoBot.service.PlayListService;
import com.chanchopeludo.ChanchoPeludoBot.service.SpotifyService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayListServiceImp implements PlayListService {

    private final PlayListRepository playListRepository;
    private final PlayListItemRepository playListItemRepository;
    private final UserRepository userRepository;
    private final ServerRepository serverRepository;
    private final SpotifyService spotifyService;

    public PlayListServiceImp(PlayListRepository playListRepository, PlayListItemRepository playListItemRepository, UserRepository userRepository, ServerRepository serverRepository, SpotifyService spotifyService) {
        this.playListRepository = playListRepository;
        this.playListItemRepository = playListItemRepository;
        this.userRepository = userRepository;
        this.serverRepository = serverRepository;
        this.spotifyService = spotifyService;
    }

    @Override
    @Transactional
    public void createPlayList(String name, String serverId, String creatorId) {
        ServerEntity server = serverRepository.findById(serverId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el servidor!"));

        UserEntity creator = userRepository.findById(creatorId)
                .orElseThrow(()-> new EntityNotFoundException("No se encontró el usuario creador!"));

        //TODO: Implementar una excepción personalizada mas adelante
        if(playListRepository.findByNameAndServer(name, server).isPresent()){
            throw new RuntimeException("Ya se encuentra otra playlist con el nombre que proporcionaste.");
        }

        PlayListEntity newPlayList = PlayListEntity.builder()
                .name(name)
                .is_public(true)
                .server(server)
                .creator(creator)
                .build();

        playListRepository.save(newPlayList);
    }

    @Override
    public void addTrackToPlayList(String playlistName, String serverId, String trackUrlQuery) {

    }

    @Override
    public void deletePlayList(String name, String serverId) {

    }

    @Override
    public void removeTrack(String playlistName, int trackOrder, String serverId) {

    }

    @Override
    public List<PlayListItemEntity> listPlayLists() {
        return List.of();
    }

    @Override
    public void loadPlayList(String name, MessageReceivedEvent event) {

    }
}
