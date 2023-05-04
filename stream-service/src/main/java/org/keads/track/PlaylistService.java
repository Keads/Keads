package org.keads.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlaylistService {

    @Autowired
    private MongoRepo infoRepo;

    private PlaylistRepo playlistRepo;


    public void createPlaylist(String name, List<String> ids) {
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setSongs(new ArrayList<>());

        List<Info> infoList = infoRepo.findAllById(ids);
        if (infoList.isEmpty()) {
            throw new IllegalArgumentException("No Songs where found!!");
        }

        playlist.getSongs().addAll(infoList);
        playlistRepo.save(playlist);
    }

    public void addSongToPlaylist(String playlistId, List<String> songsIds) {
        Playlist playlist = playlistRepo.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found"));
        List<Info> existingSongs = playlist.getSongs();
        List<Info> newSongs = infoRepo.findAllById(songsIds);
        if (newSongs.isEmpty()) {
            throw new IllegalArgumentException("no songs where found");
        }

        for (Info newSong : newSongs) {
            boolean isAlreadyAdded = false;
            for (Info existingSong : existingSongs) {
                if (existingSong.getId().equals(newSong.getId())) {
                    isAlreadyAdded = true;
                    break;
                }
            }
            if (!isAlreadyAdded) {
                existingSongs.add(newSong);
            }
        }
        playlistRepo.save(playlist);
    }

    public void reorderSongInPlaylist(String playlistId, String songId, int newIndex) {
        Playlist playlist = playlistRepo.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found!"));
        List<Info> songs = playlist.getSongs();
        Info songToReorder = null;
        int currentIndex = -1;

        for (int i = 0; i < songs.size(); i++) {
            Info song = songs.get(i);
            if (song.getId().equals(songId)) {
                songToReorder = song;
                currentIndex = i;
                break;
            }
        }
        if (songToReorder == null) {
            throw new IllegalArgumentException("Song not found in the playlist!");
        }

        songs.remove(currentIndex);

        int adjustIndex = Math.max(0, Math.min(newIndex, songs.size()));
        songs.add(adjustIndex, songToReorder);
        playlistRepo.save(playlist);
    }

    public void removeSongFromPlaylist(String playlistId, String songId) {
        Playlist playlist = playlistRepo.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found!"));
        List<Info> songs = playlist.getSongs();

        boolean removed = songs.removeIf(song -> song.getId().equals(songId));
        if (!removed) {
            throw new IllegalArgumentException("Song not found in the playlist");
        }

        playlistRepo.save(playlist);
    }

    public void removeSongsFromPlaylist(String playlistId, List<String> songIds) {
        Playlist playlist = playlistRepo.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found!"));
        List<Info> songs = playlist.getSongs();
        boolean removed = songs.removeIf(song -> songIds.contains(song.getId()));
        if (!removed) {
            throw new IllegalArgumentException("Songs not found in the playlist");
        }
        playlistRepo.save(playlist);
    }

    public void deletePlaylist(String playlistId) {
        Playlist playlist = playlistRepo.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found!"));
        playlistRepo.delete(playlist);
    }

    public void removePlaylists(List<String> playlistIds) {
        List<Playlist> playlists = playlistRepo.findAllById(playlistIds);
        playlistRepo.deleteAll(playlists);
    }



}
