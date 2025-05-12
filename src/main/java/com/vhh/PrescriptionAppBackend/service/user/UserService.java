package com.vhh.PrescriptionAppBackend.service.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import com.vhh.PrescriptionAppBackend.model.entity.UserSetting;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vhh.PrescriptionAppBackend.exception.DataNotFoundException;
import com.vhh.PrescriptionAppBackend.exception.ExpiredTokenException;
import com.vhh.PrescriptionAppBackend.jwt.JwtUtils;
import com.vhh.PrescriptionAppBackend.mapper.UserMapper;
import com.vhh.PrescriptionAppBackend.model.entity.Country;
import com.vhh.PrescriptionAppBackend.model.entity.Role;
import com.vhh.PrescriptionAppBackend.model.entity.User;
import com.vhh.PrescriptionAppBackend.model.request.CustomerRegisterGoogleRequest;
import com.vhh.PrescriptionAppBackend.model.request.UserLoginRequest;
import com.vhh.PrescriptionAppBackend.model.request.UserRegisterRequest;
import com.vhh.PrescriptionAppBackend.repository.CountryRepository;
import com.vhh.PrescriptionAppBackend.repository.RoleRepository;
import com.vhh.PrescriptionAppBackend.repository.UserRepository;
import com.vhh.PrescriptionAppBackend.service.usersetting.IUserSettingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IUserSettingService userSettingService;

    @Override
    public User createUser(CustomerRegisterGoogleRequest userDTO) throws Exception {
        String email = userDTO.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("Địa chỉ email đã tồn tại.");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role Id không tồn tại"));

        Country country = countryRepository.findById(userDTO.getCountryId())
                .orElseThrow(() -> new DataNotFoundException("Country Id không tồn tại"));
        User newUser = UserMapper.convertToEntity(userDTO);
        newUser.setRole(role);
        newUser.setCountry(country);
        return userRepository.save(newUser);
    }

    @Override
    public User createUser(UserRegisterRequest userDTO) throws Exception {
        String email = userDTO.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("Địa chỉ email đã tồn tại.");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role Id không tồn tại"));
        Country country = countryRepository.findById(userDTO.getCountryId())
                .orElseThrow(() -> new DataNotFoundException("Country Id không tồn tại"));

        User newUser = UserMapper.convertToEntity(userDTO);
        newUser.setRole(role);
        newUser.setCountry(country);
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        newUser.setPassword(encodedPassword);
        return userRepository.save(newUser);
    }

    @Override
    public String login(UserLoginRequest userDTO) throws Exception {
        String email = userDTO.getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Sai tài khoản hoặc mật khẩu");
        }

        String password = userDTO.getPassword();
        User user = optionalUser.get();
        if (user.getFacebookAccountId() == null && user.getGoogleAccountId() == null
                && !this.passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Sai tài khoản hoặc mật khẩu");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
                password, user.getAuthorities());
        authenticationManager.authenticate(authenticationToken);

        return jwtUtils.generateToken(user);
    }

    @Override
    public String login(CustomerRegisterGoogleRequest userDTO) throws Exception {
        String email = userDTO.getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy email");
        }
        User user = optionalUser.get();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
                null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        return jwtUtils.generateToken(user);
    }

    @Override
    public String googleLogin(String email, String googleAccountId, String name) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            Role role = roleRepository.findById(1L).get();
            User newUser = User.builder()
                    .email(email)
                    .name(name)
                    .googleAccountId(googleAccountId)
                    .role(role)
                    .build();
            return jwtUtils.generateToken(userRepository.save(newUser));
        }

        User user = optionalUser.get();
        if (user.getGoogleAccountId() == null) {
            user.setGoogleAccountId(googleAccountId);
            userRepository.save(user);
        } else if (!googleAccountId.equals(user.getGoogleAccountId())) {
            throw new BadCredentialsException("Google ID không khớp");
        }

        return jwtUtils.generateToken(user);
    }

    @Override
    public User getUserDetailFromToken(String token) throws Exception {
        if (jwtUtils.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token đã hết hạn");
        }

        String email = jwtUtils.extractEmail(token);
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            return user.get();
        }
        throw new DataNotFoundException("Không tìm thấy người dùng");
    }

    @Override
    public Optional<User> findByEmail(String email) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser;
    }

    @Override
    public User updateUser(User user) throws Exception {
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy người dùng");
        }

        // Validation
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("Tên không được để trống");
        }
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        if (user.getGender() == null || user.getGender().isEmpty()) {
            throw new IllegalArgumentException("Giới tính không được để trống");
        }

        User updatedUser = existingUser.get();
        updatedUser.setName(user.getName());
        updatedUser.setDateOfBirth(user.getDateOfBirth());
        updatedUser.setPhoneNumber(user.getPhoneNumber());
        updatedUser.setGender(user.getGender());
        updatedUser.setUpdatedAt(LocalDateTime.now());

        // Đồng bộ với UserSetting
        if (updatedUser.getUserSetting() != null) {
            updatedUser.getUserSetting().setName(user.getName());
            updatedUser.getUserSetting().setDateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null);
            updatedUser.getUserSetting().setPhoneNumber(user.getPhoneNumber());
            updatedUser.getUserSetting().setGender(user.getGender());
            userSettingService.saveOrUpdateUserSetting(updatedUser.getUserSetting());
        } else {
            UserSetting userSetting = new UserSetting(
                    user.getId(),
                    user.getName(),
                    user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null,
                    user.getPhoneNumber(),
                    user.getGender(),
                    null,
                    null
            );
            updatedUser.setUserSetting(userSetting);
            userSettingService.saveOrUpdateUserSetting(userSetting);
        }

        return userRepository.save(updatedUser);
    }
}