package org.keads.track;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import org.apache.poi.util.IOUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.audio.AudioParser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.bson.Document;
import org.bson.types.ObjectId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;


import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrackService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;



    public String addOne(MultipartFile file) throws Exception {
        DBObject metadata = new BasicDBObject();
        printMetadata(file.getInputStream());
        metadata.put("type", file.getContentType());
        metadata.put("title", file.getOriginalFilename());
        ObjectId id = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                metadata);
        String returner = "Song id: " + id.toString();
        return returner;
    }



        private void printMetadata(InputStream inputStream) {
        Tika tika = new Tika();
        Metadata metadata = new Metadata();
        ParseContext parseContext = new ParseContext();
        Parser parser = new Mp3Parser();
        try {
            parser.parse(inputStream, new BodyContentHandler(), metadata, parseContext);
        } catch (IOException | SAXException | TikaException e) {
            // Handle exception as necessary
        }
        if (metadata.names().length > 0) {
            for (String name : metadata.names()) {
                System.out.println(name + ": " + metadata.get(name));
            }
        } else {
            System.out.println("(-) no metadata found!!!!");;
        }
    }

    private String extractAndStoreArtworkInGridFS(InputStream inputStream) {
        try {
            // Write the input stream to a temporary file
            File tempFile = File.createTempFile("temp", ".mp3");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();

            // Create the Mp3File object from the temporary file
            Mp3File mp3file = new Mp3File(tempFile.getPath());
            if (mp3file.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                byte[] imageData = id3v2Tag.getAlbumImage();
                if (imageData != null) {
                    String mimeType = id3v2Tag.getAlbumImageMimeType();
                    String extension = mimeType.split("/")[1];
                    GridFSUploadOptions options = new GridFSUploadOptions()
                            .metadata(new Document("contentType", mimeType));
                    ObjectId objectId = gridFsTemplate.store(new ByteArrayInputStream(imageData), "artwork." + extension, options);
                    return objectId.toHexString();
                }
            }

            // Delete the temporary file
            tempFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> saveTracks(List<MultipartFile> tracks) throws IOException {
        List<String> trackIds = new ArrayList<>();
        for (MultipartFile track : tracks) {
            DBObject metadata = new BasicDBObject();
            printMetadata(track.getInputStream());
            metadata.put("filename", track.getOriginalFilename());
            metadata.put("contentType", track.getContentType());
            ObjectId trackId = gridFsTemplate.store(track.getInputStream(), track.getOriginalFilename(), metadata);
            trackIds.add("track id: " +trackId.toString());
        }
        return trackIds;
    }
}