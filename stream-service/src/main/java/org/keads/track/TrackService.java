package org.keads.track;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrackService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoRepo repo;



    public String addOne(MultipartFile file) throws Exception {
        DBObject metadata = new BasicDBObject();
        metadata.put("type", file.getContentType());
        metadata.put("title", file.getOriginalFilename());
        ObjectId id = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                metadata);
        String returner = "Song id: " + id.toString();
        Info infodoc = new Info();
        infodoc.setSong(id.toString());
        infodoc.setFilename(file.getOriginalFilename());
        repo.insert(infodoc);
        return returner;
    }

    public List<String> saveTracks(List<MultipartFile> tracks) throws IOException {
        List<String> trackIds = new ArrayList<>();
        for (MultipartFile track : tracks) {
            DBObject metadata = new BasicDBObject();
            metadata.put("filename", track.getOriginalFilename());
            metadata.put("contentType", track.getContentType());
            ObjectId trackId = gridFsTemplate.store(track.getInputStream(), track.getOriginalFilename(), metadata);
            Info infodoc = new Info();
            infodoc.setFilename(track.getOriginalFilename());
            infodoc.setSong(trackId.toString());
            repo.insert(infodoc);
            trackIds.add("track id: " +trackId.toString());
        }
        return trackIds;
    }
}