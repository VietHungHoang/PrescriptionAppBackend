package com.vhh.PrescriptionAppBackend.service.schedule;

import com.vhh.PrescriptionAppBackend.exception.UnauthorizedException;
import com.vhh.PrescriptionAppBackend.model.entity.Schedule;
import com.vhh.PrescriptionAppBackend.model.request.ScheduleRequest;
import com.vhh.PrescriptionAppBackend.model.request.StatusUpdateRequest;
import com.vhh.PrescriptionAppBackend.model.response.DrugResponse;
import com.vhh.PrescriptionAppBackend.model.response.ScheduleResponse;
import com.vhh.PrescriptionAppBackend.model.response.StatusUpdateResponse;
import com.vhh.PrescriptionAppBackend.model.response.TimeDosageResponse;
import com.vhh.PrescriptionAppBackend.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    // API lấy lịch uống thuốc theo ngày
    @Transactional
    public ScheduleResponse getScheduleByDate(ScheduleRequest scheduleRequest) {
        Long userId = Long.valueOf(scheduleRequest.getUserId());
        java.sql.Date sqlDate = (java.sql.Date) scheduleRequest.getDate();

        // Lấy ngày hiện tại (không có giờ, phút, giây)
        java.sql.Date today = java.sql.Date.valueOf(LocalDate.now());

        // Nếu ngày request < ngày hiện tại, trả về null hoặc response rỗng
        if (sqlDate.before(today)) {
            // Có thể trả về null hoặc ScheduleResponse với danh sách rỗng

            // hoặc
             ScheduleResponse emptyResponse = new ScheduleResponse();
             emptyResponse.setDate(sqlDate.toString());
             emptyResponse.setTimeDosages(Collections.emptyList());
             return emptyResponse;
        }

        List<Schedule> schedules = scheduleRepository.findByUserIdAndDate(userId, sqlDate);

        List<TimeDosageResponse> timeDosageResponses = schedules.stream()
                .map(schedule -> {
                    TimeDosageResponse tdr = new TimeDosageResponse();
                    tdr.setId(schedule.getId());
                    tdr.setTime(schedule.getDate().toLocalTime().toString());

                    DrugResponse drugResponse = new DrugResponse();
                    drugResponse.setName(schedule.getDrugInPrescription().getDrug().getName());
                    drugResponse.setDosage(schedule.getTimeDosage().getDosage());
                    drugResponse.setUnit(schedule.getDrugInPrescription().getUnit().getName());

                    tdr.setDrugs(List.of(drugResponse));
                    tdr.setStatus(schedule.getStatus());
                    tdr.setEditted(schedule.isEditted());
                    return tdr;
                })
                .collect(Collectors.toList());

        ScheduleResponse response = new ScheduleResponse();
        response.setDate(sqlDate.toString());
        response.setTimeDosages(timeDosageResponses);

        return response;
    }



    // API cập nhật trạng thái thuốc
    @Transactional
    public StatusUpdateResponse updateMedicineStatus(StatusUpdateRequest request, Long userId) {
        if (request.getDefaultTime() == null || request.getSelectedTime() == null) {
            return new StatusUpdateResponse(0, "Invalid times provided. Please check the provided times.");
        }

        LocalDateTime defaultTime = LocalDateTime.parse(request.getDefaultTime());
        LocalDateTime selectedTime = LocalDateTime.parse(request.getSelectedTime());
        int status = request.getStatus();
        boolean editted = request.isEditted();
        if (status == 0) {
            status = 0;
            editted = true;
            java.sql.Date sqlDate = java.sql.Date.valueOf(defaultTime.toLocalDate());
            List<Schedule> schedulesToUpdate = scheduleRepository.findByUserIdAndDate(userId, sqlDate);

            if (schedulesToUpdate.isEmpty()) {
                return new StatusUpdateResponse(0, "No matching schedules found for the given date.");
            }

            for (Schedule schedule : schedulesToUpdate) {
                if (schedule.getDate().equals(defaultTime)) {
                    schedule.setStatus(status);
                    schedule.setEditted(editted);
                    scheduleRepository.save(schedule);
                }
            }
            return new StatusUpdateResponse(status, "No action needed. Status is already 0.");
        }

        if (status == 2) {
            long minutesDifference = ChronoUnit.MINUTES.between(selectedTime, defaultTime);
            if (minutesDifference > 10) {
                status = 1; // Dùng muộn
            }
            editted = true;
        }

        java.sql.Date sqlDate = java.sql.Date.valueOf(defaultTime.toLocalDate());
        List<Schedule> schedulesToUpdate = scheduleRepository.findByUserIdAndDate(userId, sqlDate);

        if (schedulesToUpdate.isEmpty()) {
            return new StatusUpdateResponse(0, "No matching schedules found for the given date.");
        }

        for (Schedule schedule : schedulesToUpdate) {
            if (schedule.getDate().equals(defaultTime)) {
                schedule.setStatus(status);
                schedule.setEditted(editted);
                scheduleRepository.save(schedule);
            }
        }

        return new StatusUpdateResponse(status, "Status updated successfully for matching schedules");
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getHistoryByUserId(Long userId) {
        // Lấy danh sách lịch theo userId, theo ngày tăng dần
        List<Schedule> schedules = scheduleRepository.findAllByUserIdBeforeOrEqualTodayAndEdittedTrueOrderByDateDesc(userId);

        // Sắp xếp lại danh sách schedules theo ngày giảm dần
        schedules.sort((s1, s2) -> s2.getDate().compareTo(s1.getDate()));

        // Nhóm theo ngày, giữ thứ tự theo danh sách schedules đã sắp xếp
        Map<java.sql.Date, List<Schedule>> groupedByDate = schedules.stream()
                .collect(Collectors.groupingBy(
                        s -> java.sql.Date.valueOf(s.getDate().toLocalDate()),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<ScheduleResponse> responses = new ArrayList<>();

        for (Map.Entry<java.sql.Date, List<Schedule>> entry : groupedByDate.entrySet()) {
            java.sql.Date date = entry.getKey();
            List<Schedule> schedulesForDate = entry.getValue();

            List<TimeDosageResponse> timeDosages = schedulesForDate.stream()
                    .map(schedule -> {
                        TimeDosageResponse tdr = new TimeDosageResponse();
                        tdr.setId(schedule.getId());
                        tdr.setTime(schedule.getDate().toLocalTime().toString());

                        DrugResponse drugResponse = new DrugResponse();
                        drugResponse.setName(schedule.getDrugInPrescription().getDrug().getName());
                        drugResponse.setDosage(schedule.getTimeDosage().getDosage());
                        drugResponse.setUnit(schedule.getDrugInPrescription().getUnit().getName());

                        tdr.setDrugs(List.of(drugResponse));
                        tdr.setStatus(schedule.getStatus());
                        tdr.setEditted(schedule.isEditted());
                        return tdr;
                    })
                    .sorted((tdr1, tdr2) -> tdr2.getTime().compareTo(tdr1.getTime()))  // Sắp xếp giảm dần theo giờ
                    .collect(Collectors.toList());


            ScheduleResponse scheduleResponse = new ScheduleResponse();
            scheduleResponse.setDate(date.toString());
            scheduleResponse.setTimeDosages(timeDosages);

            responses.add(scheduleResponse);
        }

        return responses;
    }


}
