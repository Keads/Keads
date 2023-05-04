package org.keads.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlist")
public class PlaylistController {

    @Autowired
    private PlaylistService service;


    @PostMapping
    public ResponseEntity<String> createPlaylist(@RequestParam String name, @RequestParam List<String> ids) {
        service.createPlaylist(name, ids);
        return ResponseEntity.ok("Playlist created");
    }

    @PostMapping("{playlistId}/songs")
    public ResponseEntity<String> addToPlaylist(
            @PathVariable String playlistId,
            @RequestBody List<String> songsIds
    ) {
        service.addSongToPlaylist(playlistId, songsIds);
        return ResponseEntity.ok("Songs added to playlists");
    }

    @PostMapping("/{playlistId}/songs/{songId}/reorder")
    public ResponseEntity<String> reorderSongInPlaylist(
            @PathVariable String playlistId,
            @PathVariable String songId,
            @RequestParam int newIndex
    ) {
        service.reorderSongInPlaylist(playlistId, songId, newIndex);
        return ResponseEntity.ok("Song reordered successfully!");
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<String> removeSongFromPlaylist(
            @PathVariable String playlistId,
            @PathVariable String songId
    ) {
        service.removeSongFromPlaylist(playlistId, songId);
        return ResponseEntity.ok("Song removed from the playlist!");
    }

    @DeleteMapping("/{playlistId}/songs")
    public ResponseEntity<String> removeSongsFromPlaylist(
            @PathVariable String playlistId,
            @RequestBody List<String> songIds
    ) {
        service.removeSongsFromPlaylist(playlistId, songIds);
        return ResponseEntity.ok("Songs removed from playlist");
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<String> deletePlaylist(
            @PathVariable String playlistId
    ) {
        service.deletePlaylist(playlistId);
        return ResponseEntity.ok("Playlist Removed!");
    }

    @DeleteMapping
    public ResponseEntity<String> removePlaylists(
            @RequestBody List<String> playlistIds
    ) {
        service.removePlaylists(playlistIds);
        return ResponseEntity.ok("Playlists removed successfully");
    }
}
