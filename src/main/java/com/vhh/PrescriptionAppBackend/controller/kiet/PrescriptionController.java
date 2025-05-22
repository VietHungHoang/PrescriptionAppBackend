package com.vhh.PrescriptionAppBackend.controller.kiet;

import com.vhh.PrescriptionAppBackend.exception.UnauthorizedException;
import com.vhh.PrescriptionAppBackend.model.response.PrescriptionResponseKiet;
import com.vhh.PrescriptionAppBackend.model.response.ResponseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vhh.PrescriptionAppBackend.model.entity.Dosage;
import com.vhh.PrescriptionAppBackend.model.entity.Drug;
import com.vhh.PrescriptionAppBackend.model.entity.DrugInPrescription;
import com.vhh.PrescriptionAppBackend.model.entity.Prescription;
import com.vhh.PrescriptionAppBackend.model.entity.Schedule;
import com.vhh.PrescriptionAppBackend.model.entity.Unit;
import com.vhh.PrescriptionAppBackend.model.entity.User;
import com.vhh.PrescriptionAppBackend.model.request.DrugInPresRequest;
import com.vhh.PrescriptionAppBackend.model.request.PrescriptionRequest;
import com.vhh.PrescriptionAppBackend.model.request.ScheduleAddRequest;
import com.vhh.PrescriptionAppBackend.service.prescription.IPrescriptionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {
	private final IPrescriptionService prescriptionService;

	@PostMapping()
	public ResponseEntity<ResponseObject<Void>> savePrescription(
		@AuthenticationPrincipal User user,
		@RequestBody PrescriptionRequest prescriptionRequest) {
		if (user == null) {
			return ResponseEntity.ok(new ResponseObject<Void>("Không có người dùng", HttpStatus.BAD_REQUEST, null));
		}

		Prescription prescription = Prescription.builder()
			.name(prescriptionRequest.getName())
			.doctorName(prescriptionRequest.getDoctorName())
			.hospital(prescriptionRequest.getHospital())
			.user(user)
			.status(0)
			.build();
		
			prescription.setConsultationDate(prescriptionRequest.getConsultationDate());
		prescription.setFollowUpDate(prescriptionRequest.getFollowUpDate());
		List<DrugInPrescription> drugInPrescriptionList = new ArrayList<>();
		for(DrugInPresRequest drugInPresRequest : prescriptionRequest.getDrugs()) {
			DrugInPrescription drugInPrescription = DrugInPrescription.builder()
			.drug(Drug.builder().id(drugInPresRequest.getDrugId()).build())
			.unit(Unit.builder().id(drugInPresRequest.getUnitId()).build())
			.prescription(prescription)
			.build();

			Map<Double, List<ScheduleAddRequest>> grouped = drugInPresRequest.getSchedules().stream()
				.collect(Collectors.groupingBy(ScheduleAddRequest::getDosage));

			List<Schedule> scheduleList = new ArrayList<>();

			for (Map.Entry<Double, List<ScheduleAddRequest>> entry : grouped.entrySet()) {
				// Tạo Dosage cho nhóm
				Dosage dosage = Dosage.builder().dosage(entry.getKey()).build();

				// Convert từng ScheduleRequest trong nhóm thành Schedule, set Dosage + DrugInPrescription
				List<Schedule> schedules = entry.getValue().stream()
					.map(sr -> {
						Schedule s = Schedule.responseToEntity(sr);
						s.setDrugInPrescription(drugInPrescription);
						s.setTimeDosage(dosage);
						return s;
					})
					.collect(Collectors.toList());

				scheduleList.addAll(schedules);
			}
			drugInPrescription.setSchedules(scheduleList);
			drugInPrescriptionList.add(drugInPrescription);
		}
		prescription.setDrugInPrescriptions(drugInPrescriptionList);
		this.prescriptionService.save(prescription);
		return ResponseEntity.ok(new ResponseObject<Void>("Thêm đơn thuốc thành công", HttpStatus.OK, null));
	}

	/**
     * API lấy danh sách đơn thuốc theo trạng thái (0 hoặc 1)
     * @param status trạng thái đơn thuốc
     * @return danh sách PrescriptionResponse
     */
    @GetMapping("/getByStatus")
    public ResponseEntity<List<PrescriptionResponseKiet>> getPrescriptionsByStatus(@AuthenticationPrincipal User user, @RequestParam int status) {
        // Validate status đầu vào
        if (status != 0 && status != 1) {
            return ResponseEntity.badRequest().build();
        }
		Long userId = user.getId();
        List<PrescriptionResponseKiet> prescriptions = prescriptionService.getPrescriptionsByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(prescriptions);
    }

}
