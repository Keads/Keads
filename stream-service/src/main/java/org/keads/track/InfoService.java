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

}
