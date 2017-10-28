package com.isgr8.mpdclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class MpdStatus {

    private static final Logger LOGGER = LoggerFactory.getLogger(MpdStatus.class);
    private int volume;
    private boolean repeat;
    private boolean random;
    private boolean single;
    private boolean consume;
    private int playlist;
    private int playlistLength;
    private PlayerState playerState;

    MpdStatus(Builder builder) {
        this.volume = builder.volume;
        this.repeat = builder.repeat;
        this.random = builder.random;
        this.single = builder.single;
        this.consume = builder.consume;
        this.playlist = builder.playlist;
        this.playlistLength = builder.playlistLength;
        this.playerState = builder.playerState;
    }

    static MpdStatus fromResponse(String mpdResponse) {
        Map<String, String> map = Arrays.stream(mpdResponse.split("\n"))
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
        return new Builder()
                .setVolume(Integer.valueOf(map.get("volume")))
                .setRepeat(map.get("repeat").equals("1"))
                .setRandom(map.get("random").equals("1"))
                .setSingle(map.get("single").equals("1"))
                .setConsume(map.get("consume").equals("1"))
                .setPlaylist(Integer.valueOf(map.get("playlist")))
                .setPlaylistLength(Integer.valueOf(map.get("playlistlength")))
                .setPlayerState(PlayerState.valueOf(map.get("state").toUpperCase()))
                .build();
    }

    public int getVolume() {
        return volume;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public boolean isRandom() {
        return random;
    }

    public boolean isSingle() {
        return single;
    }

    public boolean isConsume() {
        return consume;
    }

    public int getPlaylist() {
        return playlist;
    }

    public int getPlaylistLength() {
        return playlistLength;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    static class Builder {

        private int volume;
        private boolean repeat;
        private boolean random;
        private boolean single;
        private boolean consume;
        private int playlist;
        private int playlistLength;
        private PlayerState playerState;

        Builder setVolume(int volume) {
            this.volume = volume;
            return this;
        }

        Builder setRepeat(boolean repeat) {
            this.repeat = repeat;
            return this;
        }

        Builder setRandom(boolean random) {
            this.random = random;
            return this;
        }

        Builder setSingle(boolean single) {
            this.single = single;
            return this;
        }

        Builder setConsume(boolean consume) {
            this.consume = consume;
            return this;
        }

        Builder setPlaylist(int playlist) {
            this.playlist = playlist;
            return this;
        }

        Builder setPlaylistLength(int playlistLength) {
            this.playlistLength = playlistLength;
            return this;
        }

        Builder setPlayerState(PlayerState playerState) {
            this.playerState = playerState;
            return this;
        }

        MpdStatus build() {
            return new MpdStatus(this);
        }
    }
}
