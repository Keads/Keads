package org.keads.track;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.apache.poi.util.IOUtils;
import org.bson.types.ObjectId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrackService {

    private static final String TOPIC_NAME = "song";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

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
        //RestTemplate restTemplate = new RestTemplate();
        //org.springframework.http.HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);
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
            //kafkaTemplate.send("song", trackId.toString());
            extractAndPrint(track.getInputStream());

            //MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            //body.add("trackId", trackId.toString());
            //HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
            //ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:5000/features", HttpMethod.POST, requestEntity, String.class);
            //String response = responseEntity.getBody();
            // Handle the response as needed

        }
        return trackIds;
    }

    public void extractAndPrint(InputStream filer) {
        try {
            File tempFile = File.createTempFile("temp", ".mp3");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            IOUtils.copy(filer, outputStream);
            outputStream.close();
            Mp3File mp3File = new Mp3File(tempFile);
            if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                System.out.println("Title: " + id3v2Tag.getTitle());
                System.out.println("Artist: " + id3v2Tag.getArtist());
                System.out.println("Album: " + id3v2Tag.getAlbum());
                System.out.println("Genre: " + id3v2Tag.getGenreDescription());
                System.out.println("Date: " + id3v2Tag.getDate());
                // Print more metadata fields if needed
            } else {
                System.out.println("No ID3v2 tag found in the audio file.");
            }
        } catch (UnsupportedTagException | InvalidDataException | IOException e) {
            e.printStackTrace();
        }
    }
}