package com.isgr8.mpdclient;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/playlists")
public class PlaylistController {

    private final MpdClient mpdClient;

    @Autowired
    public PlaylistController(MpdService mpdService) {
        this.mpdClient = mpdService.getMpdClient();
    }

    @GetMapping
    public Flux<Playlist> getPlaylists() {
        return mpdClient.playlists();
    }


    @PutMapping("current")
    public Mono<Void> loadPlaylist(@RequestBody Playlist playlist) {
        return mpdClient.loadPlaylist(playlist.getName());
    }

}
