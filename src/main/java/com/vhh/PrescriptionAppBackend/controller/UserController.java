package com.vhh.PrescriptionAppBackend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vhh.PrescriptionAppBackend.user.IUserService;
import com.vhh.PrescriptionAppBackend.model.User;
import com.vhh.PrescriptionAppBackend.model.request.UserLoginRequest;
import com.vhh.PrescriptionAppBackend.model.request.UserRegisterRequest;
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

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginRequest userRequest, HttpServletRequest request)
            throws Exception {

                // String token = userService.login(userRequest.getEmail(), userRequest.getPassword(), userRequest.getRoleId() == null ? 1 : userRequest.getRoleId());`
                // String userAgent = request.getHeader("User-Agent");
                // User user = userService.getUserDetailFromToken(token);
                String token = userService.login(userRequest.getEmail(), userRequest.getPassword(), userRequest.getRoleId());
                return ResponseEntity.ok(token);
                
            }

}
