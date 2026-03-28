package com.seatbooking.controller;

import com.seatbooking.dto.DeskDto;
import com.seatbooking.dto.FloorDto;
import com.seatbooking.dto.RoomDto;
import com.seatbooking.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Floor endpoints
    @PostMapping("/floors")
    public ResponseEntity<FloorDto.Summary> createFloor(@RequestBody FloorDto.CreateRequest request) {
        return ResponseEntity.ok(adminService.createFloor(request));
    }

    @GetMapping("/floors")
    public ResponseEntity<List<FloorDto.Summary>> getAllFloors() {
        return ResponseEntity.ok(adminService.getAllFloors());
    }

    @GetMapping("/floors/{id}")
    public ResponseEntity<FloorDto.Response> getFloorDetails(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getFloorDetails(id));
    }

    @DeleteMapping("/floors/{id}")
    public ResponseEntity<Void> deleteFloor(@PathVariable Long id) {
        adminService.deleteFloor(id);
        return ResponseEntity.noContent().build();
    }

    // Room endpoints
    @PostMapping("/rooms")
    public ResponseEntity<RoomDto.Response> createRoom(@RequestBody RoomDto.CreateRequest request) {
        return ResponseEntity.ok(adminService.createRoom(request));
    }

    @GetMapping("/floors/{floorId}/rooms")
    public ResponseEntity<List<RoomDto.Response>> getRoomsByFloor(@PathVariable Long floorId) {
        return ResponseEntity.ok(adminService.getRoomsByFloor(floorId));
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<RoomDto.Response> getRoomDetails(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getRoomDetails(id));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        adminService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    // Desk endpoints
    @PostMapping("/desks")
    public ResponseEntity<DeskDto.Response> createDesk(@RequestBody DeskDto.CreateRequest request) {
        return ResponseEntity.ok(adminService.createDesk(request));
    }

    @PatchMapping("/desks/{id}")
    public ResponseEntity<DeskDto.Response> updateDesk(@PathVariable Long id, @RequestBody DeskDto.UpdateRequest request) {
        return ResponseEntity.ok(adminService.updateDeskGeometry(id, request));
    }

    @DeleteMapping("/desks/{id}")
    public ResponseEntity<Void> deleteDesk(@PathVariable Long id) {
        adminService.deleteDesk(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/rooms/{id}/walls")
    public ResponseEntity<RoomDto.Response> updateRoomWalls(@PathVariable Long id, @RequestBody RoomDto.UpdateWallsRequest request) {
        return ResponseEntity.ok(adminService.updateRoomWalls(id, request));
    }
}