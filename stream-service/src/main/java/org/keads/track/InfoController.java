package org.keads.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class InfoController {

    @Autowired
    private InfoService service;


    @GetMapping("/all")
    public ResponseEntity<List<Info>> getAll() {
        List<Info> list = service.getAllSongs();
        return ResponseEntity.ok(list);
    }

    public ResponseEntity<Info> getOne(@PathVariable String id) {
        Info info = service.getOne(id);
        if (info != null) {
            return ResponseEntity.ok(info);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/artist/{artist}")
    public ResponseEntity<List<Info>> getSongsByArtist(@PathVariable("artist") String artist) {
        List<Info> songs = service.getSongsByArtist(artist);
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/album/{album}")
    public ResponseEntity<List<Info>> getSongsByAlbum(@PathVariable("album") String album) {
        List<Info> songs = service.getSongsByAlbum(album);
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Info>> getSongsByGenre(@PathVariable("genre") String genre) {
        List<Info> songs = service.getSongsByGenre(genre);
        return ResponseEntity.ok(songs);
    }

    //@GetMapping("/search")
    //public ResponseEntity<List<Info>> searchSongs(@RequestParam("q") String query) {
     //   List<Info> songs = service.searchSongs(query);
     //   return ResponseEntity.ok(songs);
    //}

}
