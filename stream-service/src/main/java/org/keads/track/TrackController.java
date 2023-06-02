package org.keads.track;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.apache.poi.util.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/track")
public class TrackController {


    @Autowired
    private TrackService service;

    @Autowired
    private PlaylistRepo playlistRepo;


    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private MongoRepo repo;


    @PostMapping("/add")
    public String addOne(@RequestParam("file") MultipartFile file) throws Exception {
        String id = service.addOne(file);
        return id;
    }

    // upload using the key "file" and value of file
    @GetMapping("/{id}")
    public ResponseEntity<ResourceRegion> stream(@PathVariable String id, @RequestHeader HttpHeaders headers) {
        try {
            GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(id))));
            if (file == null) {
                return ResponseEntity.notFound().build();
            }
            GridFsResource resource = gridFsTemplate.getResource(file);
            ResourceRegion region = resourceRegion(resource, headers);
            HttpStatus status = HttpStatus.PARTIAL_CONTENT;
            headers.add("Content-Type", "audio/mpeg");
            return ResponseEntity.status(status).headers(headers).body(region);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ResourceRegion resourceRegion(GridFsResource resource, HttpHeaders headers) throws IOException {
        long contentLength = resource.contentLength();
        long rangeLength;
        HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().get(0);
        if (range != null) {
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            rangeLength = Math.min(256 * 1024L, end - start + 1);
            return new ResourceRegion(resource, start, rangeLength);
        } else {
            rangeLength = Math.min(256 * 1024L, contentLength);
            return new ResourceRegion(resource, 0, rangeLength);
        }
    }

    // upload using key of "files" and the value of data
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadTracks(@RequestParam("files") MultipartFile[] files) throws IOException {
        service.saveTracks(List.of(files));
        return ResponseEntity.ok().build();
    }




    @GetMapping("/mp3/{id}/artwork")
    public ResponseEntity<byte[]> getMp3Artwork(@PathVariable("id") ObjectId id) throws IOException {
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        GridFsResource resource = gridFsTemplate.getResource(file);
        InputStream inputStream = resource.getInputStream();
        try {
            File tempFile = File.createTempFile("temp", ".mp3");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();

            // Create the Mp3File object from the temporary file
            Mp3File mp3file = new Mp3File(tempFile.getPath());

            if (mp3file.hasId3v2Tag() && mp3file.getId3v2Tag().getAlbumImage() != null) {
                byte[] imageData = mp3file.getId3v2Tag().getAlbumImage();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(mp3file.getId3v2Tag().getAlbumImageMimeType()));
                headers.setContentLength(imageData.length);
                return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (InvalidDataException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedTagException e) {
            throw new RuntimeException(e);
        }
    }


    @DeleteMapping("/{ids}")
    public ResponseEntity<String> deleteFiles(@PathVariable("ids") String ids) {
        List<String> idList = Arrays.asList(ids.split(","));

        // Create a list to store the songs that need to be deleted from playlists
        List<Info> songsToDeleteFromPlaylists = new ArrayList<>();

        for (String id : idList) {
            Info info = repo.findBySong(id);

            if (info != null) {
                // Delete the song from the repository
                repo.delete(info);

                // Delete the song from gridfs
                gridFsTemplate.delete(new Query(Criteria.where("_id").is(new ObjectId(id))));

                // Add the song to the list of songs to be deleted from playlists
                songsToDeleteFromPlaylists.add(info);
            }
        }

        // Delete the songs from playlists
        for (Info song : songsToDeleteFromPlaylists) {
            deleteSongFromPlaylists(song);
        }

        return ResponseEntity.ok().build();
    }

    private void deleteSongFromPlaylists(Info song) {
        List<Playlist> playlists = playlistRepo.findBySongsContaining(song);

        for (Playlist playlist : playlists) {
            playlist.getSongs().remove(song);
            playlistRepo.save(playlist);
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
