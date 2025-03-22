package com.vhh.PrescriptionAppBackend.user;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vhh.PrescriptionAppBackend.exception.DataNotFoundException;
import com.vhh.PrescriptionAppBackend.exception.ExpiredTokenException;
import com.vhh.PrescriptionAppBackend.jwt.JwtUtils;
import com.vhh.PrescriptionAppBackend.model.Role;
import com.vhh.PrescriptionAppBackend.model.User;
import com.vhh.PrescriptionAppBackend.model.request.UserRegisterRequest;
import com.vhh.PrescriptionAppBackend.repository.RoleRepository;
import com.vhh.PrescriptionAppBackend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public User createUser(UserRegisterRequest userDTO) throws Exception {
        String email = userDTO.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("Địa chỉ email đã tồn tại.");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role Id không tồn tại"));
        User newUser = User.builder()
                .email(userDTO.getEmail())
                .fullName(userDTO.getFullName())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .role(role)
                .build();

        if (userDTO.getGoogleAccountId() == 0 && userDTO.getFacebookAccountId() == 0) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String email, String password, Long roleId) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Sai tài khoản hoặc mật khẩu");
        }

        User user = optionalUser.get();
        if (user.getFacebookAccountId() == 0 && user.getGoogleAccountId() == 0
                && !passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Sai tài khoản hoặc mật khẩu");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
                password, user.getAuthorities());
        authenticationManager.authenticate(authenticationToken);

        return jwtUtils.generateToken(user);
    }

    @Override
    public User getUserDetailFromToken(String token) throws Exception{
        if(jwtUtils.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token đã hết hạn");
        }

        String email = jwtUtils.extractEmail(token);
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isPresent()) {
            return user.get();
        }
        throw new DataNotFoundException("Không tìm thấy người dùng");
        
    }

}
