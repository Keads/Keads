package org.keads.track;

import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/track")
public class TrackController {


    @Autowired
    private TrackService service;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @PostMapping("/add")
    public String addOne(@RequestParam("file") MultipartFile file) throws IOException {
        String id = service.addOne(file);
        return "the id is : " + id;
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

}
