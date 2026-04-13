package com.example.EmployeeManagementSystem.Controller;

import com.example.EmployeeManagementSystem.DTO.ActionDTO;
import com.example.EmployeeManagementSystem.DTO.LeaveRequestDTO;
import com.example.EmployeeManagementSystem.DTO.LeaveResponseDTO;
import com.example.EmployeeManagementSystem.Service.LeaveRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("leave_requests")
public class LeaveRequestController {
    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<LeaveResponseDTO>> getAllLeaveRequest() {
        return ResponseEntity.ok(leaveRequestService.getAllTheLeaveRequest());
    }

    @PostMapping("/{EmployeeId}")
    @PreAuthorize("hasAnyRole('MANAGER','EMPLOYEE')")
    public ResponseEntity<?> addLeaveRequest(@PathVariable long EmployeeId,@RequestBody LeaveRequestDTO dto){
        return leaveRequestService.createRequest(EmployeeId,dto);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<LeaveResponseDTO>> getAllTheLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllThePendingLeaveRequests());
    }

    @PutMapping("/approval")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateLeaveRequestStatus(@RequestBody ActionDTO actionDTO){
      return leaveRequestService.updateLeaveRequestStatus(actionDTO);
    }

    @PutMapping("/cancel/{leaveId}")
    @PreAuthorize("hasAuthority('LEAVE_CANCEL')")
    public ResponseEntity<?> cancelLeaveRequest(@RequestParam String email,@PathVariable long leaveId){
      return leaveRequestService.cancelLeaveRequest(email, leaveId);
    }

    @GetMapping("/employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> getEmployeeLeaves(Authentication authentication) {
        return leaveRequestService.getLeaveRequestsByEmployee(authentication);
    }

}
