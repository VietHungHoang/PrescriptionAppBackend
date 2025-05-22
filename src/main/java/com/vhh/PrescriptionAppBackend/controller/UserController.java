package com.vhh.PrescriptionAppBackend.controller;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.vhh.PrescriptionAppBackend.service.country.CountryService;
import com.vhh.PrescriptionAppBackend.service.token.GoogleTokenVerifierService;
import com.vhh.PrescriptionAppBackend.service.token.ITokenService;
import com.vhh.PrescriptionAppBackend.service.user.IUserService;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.vhh.PrescriptionAppBackend.jwt.JwtUtils;
import com.vhh.PrescriptionAppBackend.mapper.CountryMapper;
import com.vhh.PrescriptionAppBackend.model.entity.Country;
import com.vhh.PrescriptionAppBackend.model.entity.Token;
import com.vhh.PrescriptionAppBackend.model.entity.User;
import com.vhh.PrescriptionAppBackend.model.entity.UserSetting;
import com.vhh.PrescriptionAppBackend.model.request.CustomerRegisterGoogleRequest;
import com.vhh.PrescriptionAppBackend.model.request.GoogleLoginRequest;
import com.vhh.PrescriptionAppBackend.model.request.UserLoginRequest;
import com.vhh.PrescriptionAppBackend.model.request.UserRegisterRequest;
import com.vhh.PrescriptionAppBackend.model.response.CountryResponse;
import com.vhh.PrescriptionAppBackend.model.response.GoogleAuthResponse;
import com.vhh.PrescriptionAppBackend.model.response.LoginResponse;
import com.vhh.PrescriptionAppBackend.model.response.ResponseObject;
import com.vhh.PrescriptionAppBackend.model.response.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
	private final IUserService userService;
	private final ITokenService tokenService;
	private final CountryService countryService;
	private final JwtUtils jwtUtils;
	private final GoogleTokenVerifierService googleTokenVerifierService;

	@PostMapping("/register")
	public ResponseEntity<ResponseObject<UserResponse>> createUser(@Valid @RequestBody UserRegisterRequest userDTO,
																   BindingResult result) throws Exception {
		if (result.hasErrors()) {
			List<String> errorMessages = result.getFieldErrors()
					.stream()
					.map(FieldError::getDefaultMessage)
					.toList();

			return ResponseEntity.badRequest().body(ResponseObject.<UserResponse>builder()
					.status(HttpStatus.BAD_REQUEST)
					.data(null)
					.message(errorMessages.toString())
					.build());
		}
		if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
			return ResponseEntity.badRequest().body(ResponseObject.<UserResponse>builder()
					.status(HttpStatus.BAD_REQUEST)
					.data(null)
					.message("Mật khẩu không khớp")
					.build());
		}

		User user = userService.createUser(userDTO);
		return ResponseEntity.ok(ResponseObject.<UserResponse>builder()
				.status(HttpStatus.CREATED)
				.data(UserResponse.fromUser(user))
				.message("Đăng ký tài khoản thành công")
				.build());
	}

	@PostMapping("/register/google")
	public ResponseEntity<ResponseObject<LoginResponse>> createUser(
			@Valid @RequestBody CustomerRegisterGoogleRequest userDTO) throws Exception {
		User newUser = userService.createUser(userDTO);
		String token = userService.login(userDTO);
		User user = userService.getUserDetailFromToken(token);
		Token jwtToken = tokenService.addToken(user, token);
		LoginResponse loginResponse = LoginResponse.builder()
				.message("Login successfully")
				.token(jwtToken.getToken())
				.tokenType(jwtToken.getTokenType())
				.refreshToken(jwtToken.getRefreshToken())
				.id(user.getId())
				.username(user.getUsername())
				// .roles(user.getAuthorities().stream().map(item -> item.getAuthority()).toList())
				.build();

		return ResponseEntity.ok().body(ResponseObject.<LoginResponse>builder()
				.message("Đăng nhập thành công")
				.data(loginResponse)
				.status(HttpStatus.OK)
				.build());
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseObject<LoginResponse>> login(@Valid @RequestBody UserLoginRequest userDTO,
															   HttpServletRequest request) throws Exception {
		// userDTO.setRoleId(userDTO.getRoleId() == null ? 1 : userDTO.getRoleId());
		String token = userService.login(userDTO);
		User user = userService.getUserDetailFromToken(token);
		Token jwtToken = tokenService.addToken(user, token);
		LoginResponse loginResponse = LoginResponse.builder()
				.message("Login successfully")
				.token(jwtToken.getToken())
				.tokenType(jwtToken.getTokenType())
				.refreshToken(jwtToken.getRefreshToken())
				.id(user.getId())
				.username(user.getUsername())
				// .roles(user.getAuthorities().stream().map(item -> item.getAuthority()).toList())
				.build();

		return ResponseEntity.ok().body(ResponseObject.<LoginResponse>builder()
				.message("Đăng nhập thành công")
				.data(loginResponse)
				.status(HttpStatus.OK)
				.build());
	}

	@PostMapping("/country")
	public ResponseEntity<ResponseObject<List<CountryResponse>>> getAllCountries() {
		List<CountryResponse> countries = CountryMapper.toCountryResponses(countryService.getAllCountries());
		return ResponseEntity.ok().body(ResponseObject.<List<CountryResponse>>builder()
				.message("Lấy danh sách quốc gia thành công")
				.data(countries)
				.status(HttpStatus.OK)
				.build());
	}

	@PostMapping("/google/verify")
	public ResponseEntity<GoogleAuthResponse> verifyGoogleToken(@Valid @RequestBody GoogleLoginRequest request) {
		try {
			GoogleIdToken.Payload payload = googleTokenVerifierService.verify(request.getIdToken());
			String email = payload.getEmail();
			String googleId = payload.getSubject(); // Google's unique ID

			Optional<User> existingUserOpt = userService.findByEmail(email);

			if (existingUserOpt.isPresent()) {
				// User exists - Login Flow
				User user = existingUserOpt.get();
				// Optional: Update user info (name, picture) from Google if desired
				// Optional: Ensure Google ID matches if stored
				if (user.getGoogleAccountId() == null) {
					user.setGoogleAccountId(googleId); // Link account if not already linked
					// Consider saving the updated user: userService.save(user);
				} else if (!googleId.equals(user.getGoogleAccountId())) {
					// log.warn("Google ID mismatch for user {}. Stored: {}, Provided: {}", email,
					// user.getGoogleId(), googleId);
					// Decide how to handle: maybe error, maybe ignore, maybe update?
					// For now, proceed with login based on email match.
				}

				String token = jwtUtils.generateToken(user);
				String name = (String) payload.get("name");
				// log.info("Google Login successful for user: {}", email);
				return ResponseEntity.ok(GoogleAuthResponse.loginSuccess(token, name));

			} else {
				// User does not exist - Registration Required Flow
				String name = (String) payload.get("name");
				// log.info("Google user not found, registration required for email: {}",
				// email);
				// CustomerRegisterGoogleRequest userDTO = CustomerRegisterGoogleRequest.builder().email(email).name(name).googleAccountId(googleId).roleId(1L).countryId(1L).build();

				// User newUser = userService.createUser(userDTO);
						String token = userService.googleLogin(email, googleId, name);
						User user = userService.getUserDetailFromToken(token);
						Token jwtToken = tokenService.addToken(user, token);
						return ResponseEntity.ok(GoogleAuthResponse.registerSuccess(token, jwtToken.getRefreshToken(), name));

			}

		} catch (Exception e) {
			// log.error("Error verifying Google token: {}", e.getMessage(), e);
			// Distinguish between bad token (401) and server errors (500)
			if (e instanceof GeneralSecurityException || e instanceof IllegalArgumentException) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google token", e);
			} else {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error verifying Google token", e);
			}
		}
	}
	
		@GetMapping("/me")
		public ResponseEntity<ResponseObject<String>> checkToken(@AuthenticationPrincipal User user) {
			if(user == null) {
				ResponseObject<String> response = ResponseObject.<String>builder()
				.status(HttpStatus.BAD_REQUEST)
				.message("Token is valid")
				.data(null)
				.build();
				return new ResponseEntity<>(response, response.getStatus());
			} else {
				ResponseObject<String> response = ResponseObject.<String>builder()
				.status(HttpStatus.OK)
				.message("Token is valid")
				.data(user.getName())
				.build();
				return ResponseEntity.ok(response);
			}
		}
	}
// <======================================================== Đăng ========================================================>

// 	@PutMapping("/{id}")
// 	public ResponseEntity<ResponseObject<UserResponse>> updateUser(@PathVariable Long id, @RequestBody User user) {
// 		try {
// 			user.setId(id);
// 			User updatedUser = userService.updateUser(user);
// 			return ResponseEntity.ok(ResponseObject.<UserResponse>builder()
// 					.status(HttpStatus.OK)
// 					.data(UserResponse.fromUser(updatedUser))
// 					.message("Cập nhật thông tin người dùng thành công")
// 					.build());
// 		} catch (Exception e) {
// 			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.<UserResponse>builder()
// 					.status(HttpStatus.BAD_REQUEST)
// 					.data(null)
// 					.message(e.getMessage())
// 					.build());
// 		}
// 	}

// 	@GetMapping("/{id}/settings")
// 	public ResponseEntity<ResponseObject<UserSetting>> getUserSetting(@PathVariable Long id) {
// 		UserSetting userSetting = userSettingService.findByUserId(id);
// 		if (userSetting == null) {
// 			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.<UserSetting>builder()
// 					.status(HttpStatus.NOT_FOUND)
// 					.data(null)
// 					.message("Không tìm thấy thông tin cài đặt người dùng")
// 					.build());
// 		}
// 		return ResponseEntity.ok(ResponseObject.<UserSetting>builder()
// 				.status(HttpStatus.OK)
// 				.data(userSetting)
// 				.message("Lấy thông tin cài đặt người dùng thành công")
// 				.build());
// 	}

// 	@PutMapping("/{id}/settings")
// 	public ResponseEntity<ResponseObject<UserSetting>> updateUserSetting(@PathVariable Long id, @RequestBody UserSetting userSetting) {
// 		userSetting.setUserId(id);
// 		UserSetting updatedUserSetting = userSettingService.saveOrUpdateUserSetting(userSetting);
// 		return ResponseEntity.ok(ResponseObject.<UserSetting>builder()
// 				.status(HttpStatus.OK)
// 				.data(updatedUserSetting)
// 				.message("Cập nhật cài đặt người dùng thành công")
// 				.build());
// 	}
// }
