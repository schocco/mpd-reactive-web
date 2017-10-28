package com.isgr8.mpdclient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/songs")
public class SongController {

    private final MpdClient mpdClient;

    public SongController(MpdService mpdService) {
        this.mpdClient = mpdService.getMpdClient();
    }

    @GetMapping
    public Flux<Song> songs() {
        return mpdClient.playlistInfo(0, 50);
    }

    @GetMapping("current")
    public Mono<Song> getCurrent() {
        return mpdClient.currentSong().timeout(Duration.ofSeconds(10));
    }

    @PostMapping("next")
    public Mono<Song> playNext() {
        return mpdClient.next();
    }

    @PostMapping("previous")
    public Mono<Song> playPrevious() {
        return mpdClient.previous();
    }

    @PostMapping("pause")
    public  Mono<Void> pause() {
        return mpdClient.pause();
    }


}
