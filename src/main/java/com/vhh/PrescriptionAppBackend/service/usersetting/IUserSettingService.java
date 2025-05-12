package com.vhh.PrescriptionAppBackend.service.usersetting;

import com.vhh.PrescriptionAppBackend.model.entity.UserSetting;

public interface IUserSettingService {
    UserSetting findByUserId(Long userId);
    UserSetting saveOrUpdateUserSetting(UserSetting userSetting);
}