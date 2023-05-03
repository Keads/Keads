package org.keads.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InfoService {

    @Autowired
    private MongoRepo repo;


    public List<Info> getAllSongs() {
        return repo.findAll();
    }

}
