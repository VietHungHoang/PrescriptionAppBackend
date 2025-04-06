package com.vhh.PrescriptionAppBackend.service.user;

import java.util.Optional;

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

    @Override
    public User createUser(CustomerRegisterGoogleRequest userDTO) throws Exception {
        String email = userDTO.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("Địa chỉ email đã tồn tại.");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role Id không tồn tại"));

        Country country = countryRepository.findById(userDTO.getCountryId())
                .orElseThrow(() -> new DataNotFoundException("Role Id không tồn tại"));
        User newUser = UserMapper.convertToEntity(userDTO);
        newUser.setRole(role);
        newUser.setCountry(country);
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
    public Optional<User> findByEmail(String email) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser;
    }

    @Override
    public String googleLogin(String email, String googleAccountId, String name) throws Exception {
        // Optional<User> optionalUser = userRepository.findByEmail(email);
        // if (optionalUser.isEmpty()) {
        // Role role = roleRepository.findById(1L).get(); // ROLE USER
        // User newUser = User.builder()
        // .email(email)
        // .fullName(userDTO.getFullName())
        // .dateOfBirth(userDTO.getDateOfBirth())
        // .facebookAccountId(userDTO.getFacebookAccountId())
        // .googleAccountId(userDTO.getGoogleAccountId())
        // .role(role)
        // .build();

        // if (userDTO.getGoogleAccountId() == null && userDTO.getFacebookAccountId() ==
        // null) {
        // String password = userDTO.getPassword();
        // String encodedPassword = passwordEncoder.encode(password);
        // newUser.setPassword(encodedPassword);
        // }
        // return userRepository.save(newUser);
        // }

        // String password = userDTO.getPassword();
        // User user = optionalUser.get();
        // if (user.getFacebookAccountId() == null && user.getGoogleAccountId() == null
        // && !this.passwordEncoder.matches(password, user.getPassword())) {
        // throw new BadCredentialsException("Sai tài khoản hoặc mật khẩu");
        // }

        // UsernamePasswordAuthenticationToken authenticationToken = new
        // UsernamePasswordAuthenticationToken(email,
        // password, user.getAuthorities());
        // authenticationManager.authenticate(authenticationToken);

        // return jwtUtils.generateToken(user);
        return null;
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
    public User createUser(UserRegisterRequest userDTO) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
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

}
