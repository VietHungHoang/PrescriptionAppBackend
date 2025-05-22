package com.vhh.PrescriptionAppBackend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vhh.PrescriptionAppBackend.exception.UnauthorizedException;
import com.vhh.PrescriptionAppBackend.model.entity.Dosage;
import com.vhh.PrescriptionAppBackend.model.entity.Drug;
import com.vhh.PrescriptionAppBackend.model.entity.DrugInPrescription;
import com.vhh.PrescriptionAppBackend.model.entity.Prescription;
import com.vhh.PrescriptionAppBackend.model.entity.Schedule;
import com.vhh.PrescriptionAppBackend.model.entity.Unit;
import com.vhh.PrescriptionAppBackend.model.entity.User;
import com.vhh.PrescriptionAppBackend.model.request.DrugInPresRequest;
import com.vhh.PrescriptionAppBackend.model.request.PrescriptionRequest;
import com.vhh.PrescriptionAppBackend.model.response.DrugNameResponse;
import com.vhh.PrescriptionAppBackend.model.response.PrescriptionResponse;
import com.vhh.PrescriptionAppBackend.model.response.ResponseObject;
import com.vhh.PrescriptionAppBackend.service.drug.IDrugService;
import com.vhh.PrescriptionAppBackend.service.prescription.IPrescriptionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {
	private final IPrescriptionService prescriptionService;

	@PostMapping()
	public ResponseEntity<ResponseObject<Void>> savePrescription(@RequestBody PrescriptionRequest prescriptionRequest) {
		Prescription prescription = Prescription.builder()
			.name(prescriptionRequest.getName())
			.doctorName(prescriptionRequest.getDoctorName())
			.hospital(prescriptionRequest.getHospital())
			.user(User.builder().id(10L).build())
			.status(0)
						.build();
		
		// prescription.setConsultationDate(prescriptionRequest.getConsultationDate());
		// prescription.setFollowUpDate(prescriptionRequest.getFollowUpDate());
		List<DrugInPrescription> drugInPrescriptionList = new ArrayList<>();
		for(DrugInPresRequest drugInPresRequest : prescriptionRequest.getDrugs()) {
			DrugInPrescription drugInPrescription = DrugInPrescription.builder()
			.drug(Drug.builder().id(drugInPresRequest.getDrugId()).build())
			.unit(Unit.builder().id(drugInPresRequest.getUnitId()).build())
			.prescription(prescription)
			.build();
			List<Schedule> scheduleList = drugInPresRequest.getSchedules().stream()
				.map(Schedule::responseToEntity)
				.collect(Collectors.toList());
			for(Schedule x : scheduleList) {
				x.setDrugInPrescription(drugInPrescription);
				x.setTimeDosage(Dosage.builder().dosage(drugInPresRequest.getSchedules().get(0).getDosage()).build());
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
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionsByStatus(@RequestParam int status) {
        // Validate status đầu vào
        if (status != 0 && status != 1) {
            return ResponseEntity.badRequest().build();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = 10L; // default userId cho test nếu chưa đăng nhập

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
