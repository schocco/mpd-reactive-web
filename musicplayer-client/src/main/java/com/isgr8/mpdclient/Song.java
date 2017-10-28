package com.isgr8.mpdclient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;

public class Song {
    private final long id;
    private final long position;
    private final Path file;
    private final ZonedDateTime lastModified;
    private final String title;
    private final String artist;
    private final String album;
    private final String trackNumber;
    private final int year;
    private final String genre;
    private final int length;

    private Song(Builder builder) {
        this.id = builder.id;
        this.position = builder.position;
        this.file = builder.file;
        this.lastModified = builder.lastModified;
        this.artist = builder.artist;
        this.title = builder.title;
        this.album = builder.album;
        this.trackNumber = builder.trackNumber;
        this.year = builder.year;
        this.genre = builder.genre;
        this.length = builder.length;
    }

    static Song fromResponse(String mpdResponse) {
        Map<String, String> map = ResponseParser.toMap(mpdResponse);
        return new Builder()
                .setFile(Paths.get(map.get("file")))
                .setId(Long.parseLong(map.get("Id")))
                .setPosition(Long.valueOf(map.get("Pos")))
                .setLastModified(ZonedDateTime.parse(map.get("Last-Modified")))
                .setArtist(map.get("Artist"))
                .setTitle(map.get("Title"))
                .setAlbum(map.get("Album"))
                .setTrackNumber(map.get("Track"))
                .setYear(Integer.parseInt(map.getOrDefault("Date", "0")))
                .setGenre(map.get("Genre"))
                .setLength(Integer.valueOf(map.get("Time")))
                .build();
    }

    public long getId() {
        return id;
    }

    public long getPosition() {
        return position;
    }

    public String getArtist() {
        return artist;
    }

    public Path getFile() {
        return file;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getTrackNumber() {
        return trackNumber;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public int getLength() {
        return length;
    }

    public static class Builder {
        private long id;
        private long position;
        private Path file;
        private ZonedDateTime lastModified;
        private String title;
        private String artist;
        private String album;
        private String trackNumber;
        private int year;
        private String genre;
        private int length;

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setPosition(long position) {
            this.position = position;
            return this;
        }

        public Builder setFile(Path file) {
            this.file = file;
            return this;
        }

        public Builder setLastModified(ZonedDateTime lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setAlbum(String album) {
            this.album = album;
            return this;
        }

        public Builder setTrackNumber(String trackNumber) {
            this.trackNumber = trackNumber;
            return this;
        }

        public Builder setYear(int year) {
            this.year = year;
            return this;
        }

        public Builder setGenre(String genre) {
            this.genre = genre;
            return this;
        }

        public Builder setLength(int length) {
            this.length = length;
            return this;
        }

        public Builder setArtist(String artist) {
            this.artist = artist;
            return this;
        }

        public Song build() {
            return new Song(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id == song.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                '}';
    }
}
