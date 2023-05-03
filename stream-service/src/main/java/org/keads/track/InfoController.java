package org.keads.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class InfoController {

    @Autowired
    private InfoService service;


    @GetMapping("/all")
    public ResponseEntity<List<Info>> getAll() {
        List<Info> list = service.getAllSongs();
        return ResponseEntity.ok(list);
    }

    public ResponseEntity<Info> getOne(@PathVariable String id) {
        Info info = service.getOne(id);
        if (info != null) {
            return ResponseEntity.ok(info);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
