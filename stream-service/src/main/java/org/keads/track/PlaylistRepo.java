package org.keads.track;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepo extends MongoRepository<Playlist, String> {
    List<Playlist> findBySongsContaining(Info song);
}
