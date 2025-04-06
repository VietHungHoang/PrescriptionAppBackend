package com.vhh.PrescriptionAppBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vhh.PrescriptionAppBackend.model.entity.Token;
import com.vhh.PrescriptionAppBackend.model.entity.User;

public interface TokenRepository extends JpaRepository<Token, Long> {
   List<Token> findByUser(User user);
   Token findByToken(String token);
   Token findByRefreshToken(String refreshToken);

}
