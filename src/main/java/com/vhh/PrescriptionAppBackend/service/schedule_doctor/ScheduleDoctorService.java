package com.vhh.PrescriptionAppBackend.service.schedule_doctor;

import com.vhh.PrescriptionAppBackend.model.entity.ScheduleDoctor;
import com.vhh.PrescriptionAppBackend.model.request.ScheduleDoctorRequest;
import com.vhh.PrescriptionAppBackend.model.response.ScheduleDoctorResponse;
import com.vhh.PrescriptionAppBackend.repository.ScheduleDoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleDoctorService {

    private final ScheduleDoctorRepository repository;

    // Lấy tất cả lịch hẹn và chuyển đổi thành DTO
    public List<ScheduleDoctorResponse> getAllSchedules() {
        return repository.findAll().stream()
                .map(ScheduleDoctorResponse::fromScheduleDoctor)
                .collect(Collectors.toList());
    }

    // Lấy lịch hẹn theo ID và chuyển đổi thành DTO
    public Optional<ScheduleDoctorResponse> getScheduleById(Long id) {
        return repository.findById(id)
                .map(ScheduleDoctorResponse::fromScheduleDoctor);
    }

    // Tạo lịch hẹn mới và chuyển đổi thành DTO
    public ScheduleDoctorResponse createSchedule(ScheduleDoctorRequest request) {
        ScheduleDoctor schedule = new ScheduleDoctor();
        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());

        ScheduleDoctor savedSchedule = repository.save(schedule);
        return ScheduleDoctorResponse.fromScheduleDoctor(savedSchedule);
    }

    // Cập nhật lịch hẹn và trả về DTO
    public ScheduleDoctorResponse updateSchedule(Long id, ScheduleDoctorRequest updatedRequest) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setTitle(updatedRequest.getTitle());
                    existing.setDescription(updatedRequest.getDescription());
                    existing.setStartTime(updatedRequest.getStartTime());
                    existing.setEndTime(updatedRequest.getEndTime());
                    ScheduleDoctor updatedSchedule = repository.save(existing);
                    return ScheduleDoctorResponse.fromScheduleDoctor(updatedSchedule);
                })
                .orElseThrow(() -> new RuntimeException("Schedule not found with id " + id));
    }

    // Xóa lịch hẹn
    public void deleteSchedule(Long id) {
        repository.deleteById(id);
    }

    // Lấy lịch hẹn theo khoảng thời gian và chuyển thành DTO
    public List<ScheduleDoctorResponse> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findByDateRange(startDate, endDate).stream()
                .map(ScheduleDoctorResponse::fromScheduleDoctor)
                .collect(Collectors.toList());
    }
}
