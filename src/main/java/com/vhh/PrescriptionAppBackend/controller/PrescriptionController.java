package com.vhh.PrescriptionAppBackend.controller;

import com.vhh.PrescriptionAppBackend.exception.UnauthorizedException;
import com.vhh.PrescriptionAppBackend.model.response.PrescriptionResponse;
import com.vhh.PrescriptionAppBackend.service.prescription.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    /**
     * API lấy danh sách đơn thuốc theo trạng thái (0 hoặc 1)
     * @param status trạng thái đơn thuốc
     * @return danh sách PrescriptionResponse
     */
    @GetMapping("/getByStatus")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionsByStatus(@RequestParam int status) {
        // Validate status đầu vào
        if (status != 0 && status != 1) {
            return ResponseEntity.badRequest().build();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = 1L; // default userId cho test nếu chưa đăng nhập

        if (authentication != null && !authentication.getName().equals("anonymousUser")) {
            try {
                userId = Long.valueOf(authentication.getName());
            } catch (NumberFormatException e) {
                throw new UnauthorizedException("User ID không hợp lệ.");
            }
        }

        List<PrescriptionResponse> prescriptions = prescriptionService.getPrescriptionsByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(prescriptions);
    }
}
