package com.vhh.PrescriptionAppBackend.controller.kiet;

import com.vhh.PrescriptionAppBackend.exception.UnauthorizedException;
import com.vhh.PrescriptionAppBackend.model.request.kiet.ScheduleRequest;
import com.vhh.PrescriptionAppBackend.model.request.kiet.StatusUpdateRequest;
import com.vhh.PrescriptionAppBackend.model.response.ScheduleResponseKiet;
import com.vhh.PrescriptionAppBackend.model.response.StatusUpdateResponseKiet;
import com.vhh.PrescriptionAppBackend.service.schedule.ScheduleServiceKiet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleServiceKiet scheduleServiceKiet;

    // API lấy lịch uống thuốc theo ngày
    @GetMapping("/getScheduleByDate")
    public ScheduleResponseKiet getScheduleByDate(@RequestParam String date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = 10L; // default userId for testing

        if (authentication != null && !authentication.getName().equals("anonymousUser")) {
            try {
                userId = Long.valueOf(authentication.getName());
            } catch (NumberFormatException e) {
                throw new UnauthorizedException("User ID không hợp lệ.");
            }
        }

        date = date.trim();
        if (date.isEmpty()) {
            throw new UnauthorizedException("Ngày không hợp lệ hoặc dữ liệu không đúng.");
        }

        LocalDate localDate = LocalDate.parse(date);
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

        ScheduleRequest scheduleRequest = new ScheduleRequest();
        scheduleRequest.setDate(sqlDate);
        scheduleRequest.setUserId(userId);  // Gán userId vào đây

        return scheduleServiceKiet.getScheduleByDate(scheduleRequest);
    }




    @PostMapping("/update-status")
    public ResponseEntity<StatusUpdateResponseKiet> updateMedicineStatus(@RequestBody StatusUpdateRequest request) {
        // Lấy thông tin người dùng từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = 10L; // Đặt giá trị mặc định cho userId là 1 nếu người dùng chưa đăng nhập

        // Kiểm tra nếu người dùng không đăng nhập (anonymousUser)
        if (authentication != null && !authentication.getName().equals("anonymousUser")) {
            try {
                userId = Long.valueOf(authentication.getName());  // Trích xuất userId từ JWT (hoặc lấy userId từ claims)
            } catch (NumberFormatException e) {
                // Xử lý lỗi nếu userId không phải là số hợp lệ
                throw new UnauthorizedException("User ID không hợp lệ.");
            }
        }

        // Log dữ liệu nhận được từ frontend
        System.out.println("Received request with defaultTime: " + request.getId()
                + ", selectedTime: " + request.getSelectedTime()
                + ", status: " + request.getStatus());

        // Tiến hành xử lý và gọi service với dữ liệu đã được xác thực
        try {
            StatusUpdateResponseKiet response = scheduleServiceKiet.updateMedicineStatus(request, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log lỗi chi tiết
            return ResponseEntity.badRequest().body(new StatusUpdateResponseKiet(0, "Error: " + e.getMessage()));
        }
    }
    @GetMapping("/getHistory")
    public ResponseEntity<List<ScheduleResponseKiet>> getHistoryDates() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = 10L; // default userId for testing

        if (authentication != null && !authentication.getName().equals("anonymousUser")) {
            try {
                userId = Long.valueOf(authentication.getName());
            } catch (NumberFormatException e) {
                throw new UnauthorizedException("User ID không hợp lệ.");
            }
        }

        List<ScheduleResponseKiet> history = scheduleServiceKiet.getHistoryByUserId(userId);
        return ResponseEntity.ok(history);
    }
}
