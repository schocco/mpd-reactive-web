package com.isgr8.mpdclient;

import com.isgr8.mpdclient.io.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.stream.Collectors;

public class MpdClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MpdClient.class);

    private static final String OK = "OK";

    private static final String ACK = "ACK";

    private Flux<String> inboundMessages;

    private Connection connection;

    MpdClient(Connection connection) throws InterruptedException {
        this.connection = connection;
        if (!connection.isConnected()) {
            connection.connect().subscribe(v -> inboundMessages = connection.getInboundMessages());
        } else {
            inboundMessages = connection.getInboundMessages();
        }
    }

    public Mono<MpdStatus> status() {
        connection.send("status\n");
        return inboundMessages.takeWhile(s -> !s.endsWith(OK))
                .collect(Collectors.joining("\n"))
                .map(MpdStatus::fromResponse);
    }

    public Mono<Void> setVolume(int volume) {
        connection.send(String.format("setvol %d\n", volume));
        return inboundMessages.takeWhile(s -> !s.endsWith(OK)).then();
    }

    public Mono<Song> currentSong() {
        connection.send("currentsong\n");
        return inboundMessages.takeWhile(s -> !s.endsWith(OK))
                .collect(Collectors.joining("\n"))
                .map(Song::fromResponse);
    }

    public Mono<Song> next() {
        connection.send("next\n");
        return inboundMessages.takeWhile(s -> !s.endsWith(OK)).then().zipWith(currentSong())
                .map(Tuple2::getT2);
    }

    public Mono<Song> previous() {
        connection.send("previous\n");
        return inboundMessages.takeWhile(s -> !s.endsWith(OK)).then().zipWith(currentSong())
                .map(Tuple2::getT2);
    }

    public Mono<Void> pause() {
        connection.send("pause\n");
        return inboundMessages.takeWhile(s -> !s.endsWith(OK)).then();
    }

    public Mono<Void> play() {
        connection.send("play\n");
        return inboundMessages.takeWhile(s -> !s.endsWith(OK)).then();
    }

    public Mono<Void> stop() {
        connection.send("stop\n");
        return inboundMessages.takeWhile(s -> !s.endsWith(OK)).then();
    }

    public Flux<Song> playlistInfo(int offset, int limit) {
        connection.send(String.format("playlistinfo %d:%d\n", offset, limit));
        return ResponseParser.splitResponse(inboundMessages
                .takeWhile(s -> !s.endsWith(OK)), "file")
                .map(Song::fromResponse);
    }

    public Mono<Song> playlistInfo(long songPosition) {
        connection.send(String.format("playlistinfo %d\n", songPosition));
        return inboundMessages.takeWhile(s -> !s.endsWith(OK))
                .collect(Collectors.joining("\n"))
                .map(Song::fromResponse);
    }

    public Flux<Playlist> playlists() {
        connection.send("listplaylists\n");
        return ResponseParser.splitResponse(inboundMessages.takeWhile(s -> !s.endsWith(OK)), "playlist")
                .map(Playlist::fromResponse);
    }

    public Mono<MpdStatistics> statistics() {
        connection.send("stats\n");
        return inboundMessages.takeWhile(s -> !s.endsWith(OK))
                .collect(Collectors.joining("\n"))
                .map(MpdStatistics::fromResponse);
    }

    public Mono<Void> loadPlaylist(String playlistName) {
        connection.send(String.format("load %s\n", playlistName));
        return inboundMessages.next().map(s -> {
            if (s.endsWith(OK)) {
                return Mono.empty();
            }
            throw new NotFoundException("no such playlist " + playlistName);
        }).ofType(Void.class);
    }
}
