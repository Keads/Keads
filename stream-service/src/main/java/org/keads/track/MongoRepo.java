package org.keads.track;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoRepo extends MongoRepository<Info, String> {
    Info findBySong(String song_id);
}
