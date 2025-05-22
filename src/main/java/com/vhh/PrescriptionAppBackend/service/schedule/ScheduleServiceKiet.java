package com.vhh.PrescriptionAppBackend.service.schedule;

import com.vhh.PrescriptionAppBackend.model.entity.Schedule;
import com.vhh.PrescriptionAppBackend.model.request.kiet.ScheduleRequest;
import com.vhh.PrescriptionAppBackend.model.request.kiet.StatusUpdateRequest;
import com.vhh.PrescriptionAppBackend.model.response.DrugResponseKiet;
import com.vhh.PrescriptionAppBackend.model.response.ScheduleResponseKiet;
import com.vhh.PrescriptionAppBackend.model.response.StatusUpdateResponseKiet;
import com.vhh.PrescriptionAppBackend.model.response.TimeDosageResponseKiet;
import com.vhh.PrescriptionAppBackend.repository.ScheduleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceKiet {

    @Autowired
    private ScheduleRepository scheduleRepository;

    // API lấy lịch uống thuốc theo ngày
    @Transactional
    public ScheduleResponseKiet getScheduleByDate(ScheduleRequest scheduleRequest) {
        Long userId = Long.valueOf(scheduleRequest.getUserId());
        java.sql.Date sqlDate = (java.sql.Date) scheduleRequest.getDate();

        java.sql.Date today = java.sql.Date.valueOf(LocalDate.now());

        if (sqlDate.before(today)) {
            ScheduleResponseKiet emptyResponse = new ScheduleResponseKiet();
            emptyResponse.setDate(sqlDate.toString());
            emptyResponse.setTimeDosages(Collections.emptyList());
            return emptyResponse;
        }

        List<Schedule> schedules = scheduleRepository.findByUserIdAndDate(userId, sqlDate);

        List<Schedule> filteredSchedules = schedules.stream()
                .filter(schedule -> schedule.getStatus() == 0)
                .collect(Collectors.toList());

        List<TimeDosageResponseKiet> timeDosageResponsKiets = filteredSchedules.stream()
                .map(schedule -> {
                    TimeDosageResponseKiet tdr = new TimeDosageResponseKiet();
                    tdr.setId(schedule.getId());
                    tdr.setTime(schedule.getDate().toLocalTime().toString());

                    DrugResponseKiet drugResponseKiet = new DrugResponseKiet();
                    drugResponseKiet.setName(schedule.getDrugInPrescription().getDrug().getName());
                    drugResponseKiet.setDosage(schedule.getTimeDosage().getDosage());
                    drugResponseKiet.setUnit(schedule.getDrugInPrescription().getUnit().getName());

                    tdr.setDrugs(List.of(drugResponseKiet));
                    tdr.setStatus(schedule.getStatus());
                    tdr.setEditted(schedule.isEditted());
                    return tdr;
                })
                .collect(Collectors.toList());

        ScheduleResponseKiet response = new ScheduleResponseKiet();
        response.setDate(sqlDate.toString());
        response.setTimeDosages(timeDosageResponsKiets);

        return response;
    }




    // API cập nhật trạng thái thuốc
    @Transactional
    public StatusUpdateResponseKiet updateMedicineStatus(StatusUpdateRequest request, Long userId) {
        if (request.getId() == null || request.getSelectedTime() == null) {
            return new StatusUpdateResponseKiet(0, "Invalid id or selected time provided.");
        }

        // Lấy schedule theo id
        Long scheduleId = request.getId();
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null) {
            return new StatusUpdateResponseKiet(0, "No schedule found with the provided id.");
        }

        LocalDateTime defaultTime = schedule.getDate();

        LocalDateTime selectedTime;
        try {
            selectedTime = LocalDateTime.parse(request.getSelectedTime());
        } catch (Exception e) {
            return new StatusUpdateResponseKiet(0, "Invalid selected time format.");
        }

        int status = request.getStatus();
        boolean editted = request.isEditted();

        if (status == 0) {
            // Bỏ qua thuốc
            schedule.setStatus(0);
            schedule.setEditted(true);
            scheduleRepository.save(schedule);
            return new StatusUpdateResponseKiet(0, "Status updated to SKIPPED successfully.");
        }

        if (status == 2) {
            // Nếu dùng thuốc, kiểm tra xem có muộn hơn 10 phút không
            long minutesDifference = ChronoUnit.MINUTES.between(defaultTime, selectedTime);
            if (minutesDifference > 10) {
                status = 1; // Dùng muộn
            }
            schedule.setStatus(status);
            schedule.setEditted(true);
            scheduleRepository.save(schedule);
            return new StatusUpdateResponseKiet(status, "Status updated successfully.");
        }

        // Trường hợp status khác (ví dụ 1 - dùng muộn)
        schedule.setStatus(status);
        schedule.setEditted(editted);
        scheduleRepository.save(schedule);

        return new StatusUpdateResponseKiet(status, "Status updated successfully.");
    }


    @Transactional(readOnly = true)
    public List<ScheduleResponseKiet> getHistoryByUserId(Long userId) {
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

        List<ScheduleResponseKiet> responses = new ArrayList<>();

        for (Map.Entry<java.sql.Date, List<Schedule>> entry : groupedByDate.entrySet()) {
            java.sql.Date date = entry.getKey();
            List<Schedule> schedulesForDate = entry.getValue();

            List<TimeDosageResponseKiet> timeDosages = schedulesForDate.stream()
                    .map(schedule -> {
                        TimeDosageResponseKiet tdr = new TimeDosageResponseKiet();
                        tdr.setId(schedule.getId());
                        tdr.setTime(schedule.getDate().toLocalTime().toString());

                        DrugResponseKiet drugResponseKiet = new DrugResponseKiet();
                        drugResponseKiet.setName(schedule.getDrugInPrescription().getDrug().getName());
                        drugResponseKiet.setDosage(schedule.getTimeDosage().getDosage());
                        drugResponseKiet.setUnit(schedule.getDrugInPrescription().getUnit().getName());

                        tdr.setDrugs(List.of(drugResponseKiet));
                        tdr.setStatus(schedule.getStatus());
                        tdr.setEditted(schedule.isEditted());
                        return tdr;
                    })
                    .sorted((tdr1, tdr2) -> tdr2.getTime().compareTo(tdr1.getTime()))  // Sắp xếp giảm dần theo giờ
                    .collect(Collectors.toList());


            ScheduleResponseKiet scheduleResponseKiet = new ScheduleResponseKiet();
            scheduleResponseKiet.setDate(date.toString());
            scheduleResponseKiet.setTimeDosages(timeDosages);

            responses.add(scheduleResponseKiet);
        }

        return responses;
    }


}
