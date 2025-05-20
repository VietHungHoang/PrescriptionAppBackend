package com.vhh.PrescriptionAppBackend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vhh.PrescriptionAppBackend.service.unit.IUnitService;

import com.vhh.PrescriptionAppBackend.model.response.UnitResponse;
import com.vhh.PrescriptionAppBackend.model.response.ResponseObject;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/units")
@RequiredArgsConstructor
public class UnitController {
	private final IUnitService unitService;

	@GetMapping("/all")
		public ResponseEntity<ResponseObject<List<UnitResponse>>> getAllUnits() {
			List<UnitResponse> units = this.unitService.findAll();
			return ResponseEntity.ok().body(ResponseObject.<List<UnitResponse>>builder()
				.message("Lấy danh sách đơn vị thành công")
				.data(units)
				.status(HttpStatus.OK)
				.build());
		}
}
