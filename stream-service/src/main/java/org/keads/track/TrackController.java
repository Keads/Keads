package org.keads.track;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.util.IOUtils;
import org.apache.tika.Tika;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/track")
public class TrackController {


    @Autowired
    private TrackService service;

    @Autowired
    private GridFsTemplate gridFsTemplate;



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


    @GetMapping("/image/{id}")
    public void getImage(@PathVariable String id, HttpServletResponse response) {
        try {
            ObjectId objectId = new ObjectId(id);
            GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(objectId)));
            if (file != null) {
                GridFsResource resource = gridFsTemplate.getResource(file);
                InputStream inputStream = resource.getInputStream();
                Object contentTypeObj = file.getMetadata().get("contentType");
                if (contentTypeObj != null) {
                    String contentType = contentTypeObj.toString();
                    response.setContentType(contentType);
                }
                response.setCharacterEncoding("UTF-8");
                StreamUtils.copy(inputStream, response.getOutputStream());
                response.flushBuffer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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


}
