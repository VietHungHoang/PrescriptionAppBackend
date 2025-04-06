package com.vhh.PrescriptionAppBackend.mapper;

import org.modelmapper.ModelMapper;

import com.vhh.PrescriptionAppBackend.model.entity.User;
import com.vhh.PrescriptionAppBackend.model.request.CustomerRegisterGoogleRequest;
import com.vhh.PrescriptionAppBackend.model.request.UserLoginRequest;

public class UserMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static User convertToEntity(UserLoginRequest userLoginRequest) {
        return modelMapper.map(userLoginRequest, User.class);
    }

    public static User convertToEntity(CustomerRegisterGoogleRequest userLoginRequest) {
        return User.builder()
        .dateOfBirth(userLoginRequest.getDob())
        .email(userLoginRequest.getEmail())
        .gender(userLoginRequest.getGender())
        .googleAccountId(userLoginRequest.getGoogleAccountId())
        .name(userLoginRequest.getName())
        .phoneNumber(userLoginRequest.getPhoneNumber())
        .build();
    }


}
