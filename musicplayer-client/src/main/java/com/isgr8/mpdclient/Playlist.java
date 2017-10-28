package com.isgr8.mpdclient;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;

public class Playlist {

    private String name;

    private LocalDateTime lastModified;

    protected Playlist() {

    }

    private Playlist(String name, LocalDateTime lastModified) {
        this.name = name;
        this.lastModified = lastModified;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playlist playlist = (Playlist) o;
        return Objects.equals(name, playlist.name) &&
                Objects.equals(lastModified, playlist.lastModified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lastModified);
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "name='" + name + '\'' +
                ", lastModified=" + lastModified +
                '}';
    }

    static Playlist fromResponse(String response) {
        Map<String, String> map = ResponseParser.toMap(response);
        return new Playlist(map.get("playlist"), ZonedDateTime.parse(map.get("Last-Modified")).toLocalDateTime());
    }
}
