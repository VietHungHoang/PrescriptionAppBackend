package com.vhh.PrescriptionAppBackend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vhh.PrescriptionAppBackend.model.entity.Drug;
import com.vhh.PrescriptionAppBackend.model.response.DrugNameResponse;
import com.vhh.PrescriptionAppBackend.model.response.ResponseObject;
import com.vhh.PrescriptionAppBackend.service.drug.IDrugService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/drugs")
@RequiredArgsConstructor
public class DrugController {
	private final IDrugService drugService;

	@GetMapping("/simple")
	public ResponseEntity<ResponseObject<List<DrugNameResponse>>> getSimpleDrugs() {
		List<DrugNameResponse> drugs = this.drugService.findAllSimple();
		return ResponseEntity.ok().body(ResponseObject.<List<DrugNameResponse>>builder()
				.message("Lấy danh sách thuốc thành công")
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

	@GetMapping
	public ResponseEntity<ResponseObject<List<Drug>>> getAllDrugs() {
		List<Drug> drugs = this.drugService.findAll();
		return ResponseEntity.ok().body(ResponseObject.<List<Drug>>builder()
				.message("Lấy danh sách thuốc đầy đủ thành công")
				.data(drugs)
				.status(HttpStatus.OK)
				.build());
	}

	@GetMapping("/search")
	public ResponseEntity<ResponseObject<List<Drug>>> searchDrugs(@RequestParam("query") String query) {
		List<Drug> drugs = this.drugService.searchDrugs(query);
		return ResponseEntity.ok().body(ResponseObject.<List<Drug>>builder()
				.message("Tìm kiếm thuốc thành công")
				.data(drugs)
				.status(HttpStatus.OK)
				.build());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseObject<Drug>> getDrugById(@PathVariable Long id) {
		Drug drug = this.drugService.findById(id);
		return ResponseEntity.ok().body(ResponseObject.<Drug>builder()
				.message("Lấy chi tiết thuốc thành công")
				.data(drug)
				.status(HttpStatus.OK)
				.build());
	}

}