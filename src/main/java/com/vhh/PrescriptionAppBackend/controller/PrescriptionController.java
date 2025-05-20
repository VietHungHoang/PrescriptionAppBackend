package com.vhh.PrescriptionAppBackend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vhh.PrescriptionAppBackend.model.entity.Drug;
import com.vhh.PrescriptionAppBackend.model.entity.DrugInPrescription;
import com.vhh.PrescriptionAppBackend.model.entity.Prescription;
import com.vhh.PrescriptionAppBackend.model.entity.Schedule;
import com.vhh.PrescriptionAppBackend.model.request.DrugInPresRequest;
import com.vhh.PrescriptionAppBackend.model.request.PrescriptionRequest;
import com.vhh.PrescriptionAppBackend.model.response.DrugNameResponse;
import com.vhh.PrescriptionAppBackend.model.response.ResponseObject;
import com.vhh.PrescriptionAppBackend.service.drug.IDrugService;
import com.vhh.PrescriptionAppBackend.service.prescription.IPrescriptionService;

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
			.build();
		
		// prescription.setConsultationDate(prescriptionRequest.getConsultationDate());
		// prescription.setFollowUpDate(prescriptionRequest.getFollowUpDate());
		List<DrugInPrescription> drugInPrescriptionList = new ArrayList<>();
		for(DrugInPresRequest drugInPresRequest : prescriptionRequest.getDrugs()) {
			DrugInPrescription drugInPrescription = DrugInPrescription.builder()
			.drug(Drug.builder().id(drugInPresRequest.getDrugId()).build())
			.prescription(prescription)
			.build();
			List<Schedule> scheduleList = drugInPresRequest.getSchedules().stream()
				.map(Schedule::responseToEntity)
				.collect(Collectors.toList());
			
			drugInPrescription.setSchedules(scheduleList);
			drugInPrescriptionList.add(drugInPrescription);
		}
		prescription.setDrugInPrescriptions(drugInPrescriptionList);
		this.prescriptionService.save(prescription);
		return ResponseEntity.ok(new ResponseObject<Void>("Thêm đơn thuốc thành công", HttpStatus.OK, null));
	}

}