package org.keads.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
