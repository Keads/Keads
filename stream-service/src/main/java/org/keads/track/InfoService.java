package org.keads.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InfoService {

    @Autowired
    private MongoRepo repo;


    public List<Info> getAllSongs() {
        return repo.findAll();
    }

    public Info getOne(String id) {
        Optional<Info> infoOptional = repo.findById(id);
        return infoOptional.orElse(null);
    }

    public List<Info> getSongsByArtist(String artist) {
        return repo.findByArtist(artist);
    }

    public List<Info> getSongsByAlbum(String album) {
        return repo.findByAlbum(album);
    }

    public List<Info> getSongsByGenre(String genre) {
        return repo.findByGenre(genre);
    }

}
