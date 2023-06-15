package org.keads.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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

    //public List<Info> searchSongs(String searchQuery) {
        // Create a case-insensitive regex pattern for the search query
   //     Pattern pattern = Pattern.compile(Pattern.quote(searchQuery), Pattern.CASE_INSENSITIVE);

        // Create a criteria to match the song field with the search query pattern
    //    Criteria criteria = Criteria.where("song").regex(pattern);

        // Create a query with the criteria
     //   Query query = new Query(criteria);

        // Execute the query and return the matching songs
      //  return repo.findAll(query);
   // }

}
