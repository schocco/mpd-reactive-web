package com.isgr8.mpdclient;

import com.isgr8.mpdclient.io.Connection;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Year;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MpdClientTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private MpdClient mpdClient;
    private Connection connection;

    @Before
    public void setUp() throws Exception {
        connection = new Connection("localhost", 6600);
        assertThat(connection.connect().block()).isEqualTo("0.19.0");
        mpdClient = new MpdClient(connection);
        mpdClient.play().block(TIMEOUT);
    }

    @After
    public void tearDown() {
        mpdClient.pause().block(TIMEOUT);
        connection.shutdown().block();
    }

    @Test
    public void testChangeVolume() {
        mpdClient.statistics().block(TIMEOUT);
        mpdClient.setVolume(30).block(TIMEOUT);
        MpdStatus status = mpdClient.status().block(TIMEOUT);
        assertThat(status.getVolume()).isEqualTo(30);
        mpdClient.setVolume(90).block(TIMEOUT);
        status = mpdClient.status().block(TIMEOUT);
        assertThat(status.getVolume()).isEqualTo(90);
    }

    @Test
    public void testCurrentSong() {
        Song song = mpdClient.currentSong().block(TIMEOUT);
        assertThat(song).isNotNull();
        assertThat(song.getId()).isGreaterThan(0);
        assertThat(song.getTitle()).isNotBlank();
        assertThat(song.getAlbum()).isNotBlank();
        assertThat(song.getFile()).isNotNull();
        assertThat(song.getLastModified()).isBefore(ZonedDateTime.now());
        assertThat(Duration.ofSeconds(song.getLength())).isBetween(Duration.ofSeconds(1), Duration.ofHours(2));
        assertThat(song.getYear()).isLessThan(Year.now().plusYears(1).getValue());
    }

    @Test
    public void testNext() {
        Song current = mpdClient.currentSong().block(TIMEOUT);
        Song next = mpdClient.next().block(TIMEOUT);
        assertThat(next).isNotEqualTo(current);
    }

    @Test
    public void testStop() {
        mpdClient.stop().block(TIMEOUT);
        MpdStatus status = mpdClient.status().block(TIMEOUT);
        assertThat(status.getPlayerState()).isEqualTo(PlayerState.STOP);
    }

    @Test
    public void testPausePlay() {
        mpdClient.pause().block(TIMEOUT);
        MpdStatus status = mpdClient.status().block(TIMEOUT);
        assertThat(status.getPlayerState()).isEqualTo(PlayerState.PAUSE);
        mpdClient.play().block(TIMEOUT);
        assertThat(status.getPlayerState()).isNotEqualTo(PlayerState.PLAY);
    }

    @Test
    public void testPrevious() {
        Song current = mpdClient.currentSong().block(TIMEOUT);
        Song previous = mpdClient.previous().block(TIMEOUT);
        assertThat(previous).isNotEqualTo(current);
    }

    @Test
    public void testPlaylists() {
        List<Playlist> playlists = mpdClient.playlists().collectList().block(TIMEOUT);
        Assertions.assertThat(playlists).isNotEmpty();
    }

    @Test
    public void testPlaylistInfoRange() {
        List<Song> songs = mpdClient.playlistInfo(0, 10).collectList().block(TIMEOUT);
        Assertions.assertThat(songs).isNotNull().hasSize(10);
        assertThat(songs.get(0).getId()).isEqualTo(1);
    }

    @Test
    public void testPlaylistInfoSingleSong() {
        mpdClient.pause().block(TIMEOUT);
        Song current = mpdClient.currentSong().block(TIMEOUT);
        Song song = mpdClient.playlistInfo(current.getPosition()).block(TIMEOUT);
        assertThat(song).isEqualTo(current);
    }

}