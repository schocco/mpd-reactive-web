package com.isgr8.mpdclient;

public enum Tag {
    ARTIST,
    ARTISTSORT,
    ALBUM,
    ALBUMSORT,
    ALBUMARTIST,
    ALNUMARTISTSORT,
    TITLE,
    TRACK,
    NAME,
    GENRE,
    DATE,
    COMPOSER,
    PERFORMER,
    COMMENT,
    DISC,
    MUSICBRAINZ_ARTISTID,
    MUSICBRAINZ_ALBUMID,
    MUSICBRAINZ_TRACKID,
    MUSICBRAINZ_RELEASETRACKID;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
