package org.keads.track;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "info")
public class Info {
    @Id
    private ObjectId id;

    private String song;

    private String filename;

    private String title;

    private String artist;

    private String album;

    private String genre;

    private Date release_date;

    private String label;

    private float acousticness;

    private float danceability;

    private float duration;

    private float energy;

    private float instrumentalness;

    private int key;

    private float liveness;

    private float loudness;

    private int mode;

    private float speechiness;

    private float tempo;

    private float valence;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Date getRelease_date() {
        return release_date;
    }

    public void setRelease_date(Date release_date) {
        this.release_date = release_date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getAcousticness() {
        return acousticness;
    }

    public void setAcousticness(float acousticness) {
        this.acousticness = acousticness;
    }

    public float getDanceability() {
        return danceability;
    }

    public void setDanceability(float danceability) {
        this.danceability = danceability;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public float getInstrumentalness() {
        return instrumentalness;
    }

    public void setInstrumentalness(float instrumentalness) {
        this.instrumentalness = instrumentalness;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public float getLiveness() {
        return liveness;
    }

    public void setLiveness(float liveness) {
        this.liveness = liveness;
    }

    public float getLoudness() {
        return loudness;
    }

    public void setLoudness(float loudness) {
        this.loudness = loudness;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public float getSpeechiness() {
        return speechiness;
    }

    public void setSpeechiness(float speechiness) {
        this.speechiness = speechiness;
    }

    public float getTempo() {
        return tempo;
    }

    public void setTempo(float tempo) {
        this.tempo = tempo;
    }

    public float getValence() {
        return valence;
    }

    public void setValence(float valence) {
        this.valence = valence;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song_id) {
        this.song = song_id;
    }
}
