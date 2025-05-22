// package com.vhh.PrescriptionAppBackend.service.usersetting;

// import com.vhh.PrescriptionAppBackend.model.entity.UserSetting;
// import com.vhh.PrescriptionAppBackend.repository.UserSettingRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;
// import java.util.Optional;

// @Service
// public class UserSettingService implements IUserSettingService {
//     @Autowired
//     private UserSettingRepository userSettingRepository;

//     @Override
//     public UserSetting findByUserId(Long userId) {
//         Optional<UserSetting> userSetting = userSettingRepository.findByUserId(userId);
//         return userSetting.orElse(null);
//     }

//     @Override
//     public UserSetting saveOrUpdateUserSetting(UserSetting userSetting) {
//         userSetting.setUpdatedAt(LocalDateTime.now());
//         if (userSetting.getId() == null) {
//             userSetting.setCreatedAt(LocalDateTime.now());
//         }
//         return userSettingRepository.save(userSetting);
//     }
// }