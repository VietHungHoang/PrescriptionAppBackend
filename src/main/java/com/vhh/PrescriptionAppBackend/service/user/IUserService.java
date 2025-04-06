package com.vhh.PrescriptionAppBackend.service.user;

import java.util.Optional;

import com.vhh.PrescriptionAppBackend.model.entity.User;
import com.vhh.PrescriptionAppBackend.model.request.CustomerRegisterGoogleRequest;
import com.vhh.PrescriptionAppBackend.model.request.UserLoginRequest;
import com.vhh.PrescriptionAppBackend.model.request.UserRegisterRequest;

public interface IUserService {
   User createUser(CustomerRegisterGoogleRequest userDTO) throws Exception;
   User createUser(UserRegisterRequest userDTO) throws Exception;


    String login(UserLoginRequest userDTO) throws Exception;
    String login(CustomerRegisterGoogleRequest userDTO) throws Exception;

    String googleLogin(String email, String googleAccountId, String name) throws Exception;

    User getUserDetailFromToken(String token) throws Exception;

    Optional<User> findByEmail(String email) throws Exception;
}
