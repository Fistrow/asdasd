package com.chanchopeludo.ChanchoPeludoBot.service.imp;

import com.chanchopeludo.ChanchoPeludoBot.dto.VideoInfo;
import com.chanchopeludo.ChanchoPeludoBot.service.VideoInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.exceptions.detailed.NotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

@Service
public class VideoInfoServiceImp implements VideoInfoService {

    private static final Logger logger = LoggerFactory.getLogger(VideoInfoServiceImp.class);

    @Override
    public CompletableFuture<VideoInfo> getVideoInfo(String youtubeUrl) {
        return CompletableFuture.supplyAsync(() -> {
            ProcessBuilder pb = new ProcessBuilder(
                    "yt-dlp",
                    "--no-update",
                    "-4",
                    "--print", "%(title)s\n%(url)s",
                    "-f", "bestaudio[ext=m4a]/bestaudio/best",
                    youtubeUrl
            );

            try {
                Process process = pb.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String title = reader.readLine();
                    String directUrl = reader.readLine();
                    process.waitFor();

                    if (title == null || directUrl == null) {
                        throw new NotFoundException("yt-dlp no devolvió título o URL para: " + youtubeUrl);
                    }

                    return new VideoInfo(title, directUrl);
                }
            } catch (IOException | InterruptedException | NotFoundException e) {

                throw new RuntimeException("Error al ejecutar yt-dlp", e);
            }
        });
    }
}