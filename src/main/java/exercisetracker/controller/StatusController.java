package exercisetracker.controller;

import exercisetracker.model.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/status")
    public ResponseEntity<Status> getStatus() {
        return ResponseEntity.ok(new Status(200, "OK"));
    }
}
