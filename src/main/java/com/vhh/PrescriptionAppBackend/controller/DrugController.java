package com.vhh.PrescriptionAppBackend.controller;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.vhh.PrescriptionAppBackend.service.country.CountryService;
import com.vhh.PrescriptionAppBackend.service.drug.IDrugService;
import com.vhh.PrescriptionAppBackend.service.token.GoogleTokenVerifierService;
import com.vhh.PrescriptionAppBackend.service.token.ITokenService;
import com.vhh.PrescriptionAppBackend.service.user.IUserService;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.vhh.PrescriptionAppBackend.jwt.JwtUtils;
import com.vhh.PrescriptionAppBackend.mapper.CountryMapper;
import com.vhh.PrescriptionAppBackend.model.entity.Country;
import com.vhh.PrescriptionAppBackend.model.entity.Drug;
import com.vhh.PrescriptionAppBackend.model.entity.Token;
import com.vhh.PrescriptionAppBackend.model.entity.User;
import com.vhh.PrescriptionAppBackend.model.request.CustomerRegisterGoogleRequest;
import com.vhh.PrescriptionAppBackend.model.request.GoogleLoginRequest;
import com.vhh.PrescriptionAppBackend.model.request.UserLoginRequest;
import com.vhh.PrescriptionAppBackend.model.request.UserRegisterRequest;
import com.vhh.PrescriptionAppBackend.model.response.CountryResponse;
import com.vhh.PrescriptionAppBackend.model.response.DrugNameResponse;
import com.vhh.PrescriptionAppBackend.model.response.GoogleAuthResponse;
import com.vhh.PrescriptionAppBackend.model.response.LoginResponse;
import com.vhh.PrescriptionAppBackend.model.response.ResponseObject;
import com.vhh.PrescriptionAppBackend.model.response.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
