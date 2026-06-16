package com.caresync.appointment.controller;

import com.caresync.appointment.dto.AppointmentRequest;
import com.caresync.appointment.dto.AppointmentResponse;
import com.caresync.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> schedule(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.scheduleAppointment(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAppointment(id));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> listByEmail(@RequestParam String email) {
        return ResponseEntity.ok(service.listAppointmentsByEmail(email));
    }
}
