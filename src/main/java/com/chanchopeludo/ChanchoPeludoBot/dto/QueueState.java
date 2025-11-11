package com.chanchopeludo.ChanchoPeludoBot.dto;

import java.util.List;
import java.util.Optional;

public record QueueState(AudioTrackInfo nowPlaying, long nowPlayingPosition, List<AudioTrackInfo> queue) {

    public Optional<AudioTrackInfo> getNowPlaying() {
        return Optional.ofNullable(nowPlaying);
    }

    public boolean isEmpty() {
        return nowPlaying() == null && queue().isEmpty();
    }
}
