package exercisetracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class Status {
    private int statusCode;
    private String message;

    public Status(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}

@RestController
public class StatusController {

    @GetMapping("/status")
    public ResponseEntity<Status> getStatus() {
        return ResponseEntity.ok(new Status(200, "OK"));
    }
}
