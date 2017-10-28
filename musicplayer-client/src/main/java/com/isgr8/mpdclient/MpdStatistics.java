package com.isgr8.mpdclient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MpdStatistics {

    private static final String ARTISTS = "artists";
    private static final String ALBUMS = "albums";
    private static final String SONGS = "songs";
    private static final String UPTIME = "uptime";
    private static final String DB_PLAYTIME = "db_playtime";
    private static final String DB_UPDATE = "db_update";
    private static final String PLAYTIME = "playtime";


    private int artists;
    private int albums;
    private int songs;
    private Duration uptime;
    private Duration playtime;
    private LocalDateTime lastDBUpdate;

    MpdStatistics(int artists, int albums, int songs, Duration uptime, Duration playtime, LocalDateTime lastDBUpdate) {
        this.artists = artists;
        this.albums = albums;
        this.songs = songs;
        this.uptime = uptime;
        this.playtime = playtime;
        this.lastDBUpdate = lastDBUpdate;
    }

    static MpdStatistics fromResponse(String mpdResponse) {
        Map<String, String> map = Arrays.stream(mpdResponse.split("\n"))
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
        return new MpdStatisticsBuilder()
                .setArtists(Integer.valueOf(map.get(ARTISTS)))
                .setAlbums(Integer.valueOf(map.get(ALBUMS)))
                .setSongs(Integer.valueOf(map.get(SONGS)))
                .setUptime(Duration.ofSeconds(Integer.valueOf(map.get(UPTIME))))
                .setPlaytime(Duration.ofSeconds(Integer.valueOf(map.get(PLAYTIME))))
                .setLastDBUpdate(LocalDateTime.now())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MpdStatistics that = (MpdStatistics) o;
        return artists == that.artists &&
                albums == that.albums &&
                songs == that.songs &&
                Objects.equals(uptime, that.uptime) &&
                Objects.equals(playtime, that.playtime) &&
                Objects.equals(lastDBUpdate, that.lastDBUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artists, albums, songs, uptime, playtime, lastDBUpdate);
    }

    static class MpdStatisticsBuilder {
        private int artists;
        private int albums;
        private int songs;
        private Duration uptime;
        private Duration playtime;
        private LocalDateTime lastDBUpdate;

        MpdStatisticsBuilder setArtists(int artists) {
            this.artists = artists;
            return this;
        }

        MpdStatisticsBuilder setAlbums(int albums) {
            this.albums = albums;
            return this;
        }

        MpdStatisticsBuilder setSongs(int songs) {
            this.songs = songs;
            return this;
        }

        MpdStatisticsBuilder setUptime(Duration uptime) {
            this.uptime = uptime;
            return this;
        }

        MpdStatisticsBuilder setPlaytime(Duration playtime) {
            this.playtime = playtime;
            return this;
        }

        MpdStatisticsBuilder setLastDBUpdate(LocalDateTime lastDBUpdate) {
            this.lastDBUpdate = lastDBUpdate;
            return this;
        }

        MpdStatistics build() {
            return new MpdStatistics(artists, albums, songs, uptime, playtime, lastDBUpdate);
        }
    }
}
