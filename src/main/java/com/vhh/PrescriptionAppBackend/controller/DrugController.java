package com.vhh.PrescriptionAppBackend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vhh.PrescriptionAppBackend.service.drug.IDrugService;

import com.vhh.PrescriptionAppBackend.model.entity.Drug;
import com.vhh.PrescriptionAppBackend.model.response.DrugNameResponseKiet;
import com.vhh.PrescriptionAppBackend.model.response.ResponseObject;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/drugs")
@RequiredArgsConstructor
public class DrugController {
	private final IDrugService drugService;

	@GetMapping("/simple")
		public ResponseEntity<ResponseObject<List<DrugNameResponseKiet>>> getSimpleDrugs() {
			List<DrugNameResponseKiet> drugs = this.drugService.findAllSimple();
			return ResponseEntity.ok().body(ResponseObject.<List<DrugNameResponseKiet>>builder()
				.message("Lấy danh sách quốc gia thành công")
				.data(drugs)
				.status(HttpStatus.OK)
				.build());
		}

	@PostMapping("/import")
public ResponseEntity<?> importDrugs(@RequestBody List<Drug> drugs) {
	drugs.forEach(drug -> {
		drug.getSections().forEach(section -> {
			section.setDrug(drug);
		});
	});
    this.drugService.importDrugs(drugs);
    return ResponseEntity.ok("Import thành công");
}
}
