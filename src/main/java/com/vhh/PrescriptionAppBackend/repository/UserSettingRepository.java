package com.vhh.PrescriptionAppBackend.repository;

import com.vhh.PrescriptionAppBackend.model.entity.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {
    Optional<UserSetting> findByUserId(Long userId);
}