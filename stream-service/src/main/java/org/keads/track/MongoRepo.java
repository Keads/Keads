package org.keads.track;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoRepo extends MongoRepository<Info, String> {
    Info findBySong(String song_id);
    List<Info> findByArtist(String artist);

    List<Info> findByAlbum(String album);

    List<Info> findByGenre(String genre);

    //List<Info> findAll(Query query);
}
