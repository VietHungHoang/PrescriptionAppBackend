package com.vhh.PrescriptionAppBackend.user;

import com.vhh.PrescriptionAppBackend.model.User;
import com.vhh.PrescriptionAppBackend.model.request.UserRegisterRequest;

public interface IUserService {
    User createUser(UserRegisterRequest userDTO) throws Exception;
    String login(String email, String password, Long roleId) throws Exception;
    User getUserDetailFromToken(String token) throws Exception;
}
