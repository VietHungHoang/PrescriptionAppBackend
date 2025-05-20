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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
	private final GoogleTokenVerifierService googleTokenVerifierSercvService;

	@PostMapping("/register")
	public ResponseEntity<ResponseObject<UserResponse>> createUser(@Valid @RequestBody UserRegisterRequest userDTO,
			BindingResult result)
			throws Exception {
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
			@Valid @RequestBody CustomerRegisterGoogleRequest userDTO)
			throws Exception {
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
				.roles(user.getAuthorities().stream().map(item -> item.getAuthority()).toList())
				.build();

		return ResponseEntity.ok().body(ResponseObject.<LoginResponse>builder()
				.message("Đăng nhập thành công")
				.data(loginResponse)
				.status(HttpStatus.OK)
				.build());
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseObject<LoginResponse>> login(@Valid @RequestBody UserLoginRequest userDTO,
			HttpServletRequest request)
			throws Exception {

		userDTO.setRoleId(userDTO.getRoleId() == null ? 1 : userDTO.getRoleId());
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
				.roles(user.getAuthorities().stream().map(item -> item.getAuthority()).toList())
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
			GoogleIdToken.Payload payload = googleTokenVerifierSercvService.verify(request.getIdToken());
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
				GoogleAuthResponse res = GoogleAuthResponse.builder().name(user.getName()).token(token).status("LOGIN_SUCCESS").build();
				// log.info("Google Login successful for user: {}", email);
				return ResponseEntity.ok(res);

			} else {
				// User does not exist - Registration Required Flow
				String name = (String) payload.get("name");
				// log.info("Google user not found, registration required for email: {}",
				// email);
				return ResponseEntity.ok(GoogleAuthResponse.registrationRequired(email, name, googleId));
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

	// public ResponseEntity<?> loginWithGoogle(@Valid @RequestBody
	// GoogleLoginRequest loginRequest) {
	// GoogleIdToken.Payload payload = googleTokenVerifierSercvService
	// .verify(loginRequest.getIdToken());
	// if (payload == null) {
	// return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	// .body(Map.of("error", "Invalid Google Token"));
	// }

	// // Check Nonce if the client sends up ---
	// // if (googleRequest.getNonce() == null ||
	// !googleVerifier.verifyNonce(payload,
	// // googleRequest.getNonce())) {
	// // logger.warn("Nonce verification failed for Google token.");
	// // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error",
	// // "Invalid Nonce"));
	// // }

	// String googleUserId = payload.getSubject();
	// String email = payload.getEmail();
	// String name = (String) payload.get("name");
	// // String pictureUrl = (String) payload.get("picture");

	// String token = userService.googleLogin(email, googleUserId, name);
	// User user = userService.getUserDetailFromToken(token);
	// Token jwtToken = tokenService.addToken(user, token);
	// LoginResponse loginResponse = LoginResponse.builder()
	// .message("Login successfully")
	// .token(jwtToken.getToken())
	// .tokenType(jwtToken.getTokenType())
	// .refreshToken(jwtToken.getRefreshToken())
	// .id(user.getId())
	// .username(user.getUsername())
	// .roles(user.getAuthorities().stream().map(item ->
	// item.getAuthority()).toList())
	// .build();

	// return ResponseEntity.ok().body(ResponseObject.<LoginResponse>builder()
	// .message("Đăng nhập thành công")
	// .data(loginResponse)
	// .status(HttpStatus.OK)
	// .build());

	// if (!emailVerified) {
	// // Quyết định xem có cho phép đăng nhập với email chưa xác thực không
	// // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error",
	// // "Google email not verified"));
	// }

	// // Tìm hoặc tạo user trong DB
	// // User user = userService.findOrCreateUser("google", googleUserId, email,
	// // name);

	// // // Tạo JWT tùy chỉnh của bạn
	// // String jwt = tokenProvider.generateToken(user.getId().toString(),
	// // user.getEmail());

	// // logger.info("Google authentication successful. Generated custom JWT for
	// user
	// // ID: {}", user.getId());
	// // return ResponseEntity.ok(new AuthResponse(jwt, user.getId(),
	// user.getEmail(),
	// // user.getDisplayName()));

	// // Cách 1: Lấy thông tin từ Authentication object
	// // String username = authentication.getName(); // Thường là 'sub' claim
	// (Google
	// // ID)

	// // // Cách 2: Lấy thông tin trực tiếp từ Jwt Principal (chi tiết hơn)
	// // String googleUserId = jwtPrincipal.getSubject(); // 'sub' claim
	// // String email = jwtPrincipal.getClaimAsString("email");
	// // String name = jwtPrincipal.getClaimAsString("name");
	// // String picture = jwtPrincipal.getClaimAsString("picture");
	// // String issuer = jwtPrincipal.getIssuer().toString();
	// // java.util.List<String> audience = jwtPrincipal.getAudience();
	// // String nonce = jwtPrincipal.getClaimAsString("nonce"); // Lấy Nonce nếu có

	// // System.out.println("Authenticated user (from Authentication): " +
	// username);
	// // System.out.println("Authenticated user (from Jwt): ID=" + googleUserId +
	// ",
	// // Email=" + email
	// // + ", Name=" + name);
	// // System.out.println("Token Issuer: " + issuer);
	// // System.out.println("Token Audience: " + audience);
	// // System.out.println("Token Nonce: " + nonce);

	// // --- QUAN TRỌNG: Xử lý Nonce thủ công (nếu cần) ---
	// // Spring Security Resource Server không tự kiểm tra nonce.
	// // Bạn cần lấy nonce đã lưu trước đó cho request này và so sánh.
	// // String expectedNonce = getNonceForThisRequest();
	// // if (expectedNonce == null || !expectedNonce.equals(nonce)) {
	// // System.err.println("Nonce mismatch or missing!");
	// // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error",
	// // "Invalid nonce"));
	// // }
	// // System.out.println("Nonce verified successfully!");
	// // --- Kết thúc kiểm tra Nonce ---

	// // TODO: Logic tìm hoặc tạo user trong DB của bạn dựa trên googleUserId hoặc
	// // email
	// // User user = userService.findOrCreateUser(googleUserId, email, name,
	// picture);

	// // Tạo response trả về cho client
	// // Map<String, Object> responseBody = new HashMap<>();
	// // responseBody.put("message", "Authentication successful via Spring
	// Security");
	// // responseBody.put("userId", googleUserId);
	// // responseBody.put("email", email);
	// // responseBody.put("name", name);
	// // responseBody.put("appToken", yourGeneratedAppToken); // Có thể tạo token
	// của
	// // riêng bạn ở đây

	// return ResponseEntity.ok(null);
	// }

	// (Nếu dùng Nonce) Hàm giả lập lấy nonce đã lưu
	// private String getNonceForThisRequest() { ... }
	// private String getNonceForThisRequest() {
	// // Implement logic để lấy nonce bạn đã tạo và gửi cho client trước đó
	// // Ví dụ: Lấy từ session, hoặc từ một cơ chế lưu trữ tạm thời khác
	// return "your_generated_and_stored_nonce";
	// }

	// --- Ví dụ DTO (Data Transfer Object) ---
	/*
	 * class GoogleTokenRequest {
	 * private String idToken;
	 * // Getters and Setters
	 * }
	 * 
	 * class AuthResponse {
	 * private String accessToken;
	 * private String email;
	 * private String name;
	 * // Constructor, Getters and Setters
	 * }
	 */

}
