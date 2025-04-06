package com.vhh.PrescriptionAppBackend.service.token;

import com.vhh.PrescriptionAppBackend.model.entity.Token;
import com.vhh.PrescriptionAppBackend.model.entity.User;

public interface ITokenService {
    Token addToken(User user, String token);
    Token refreshToken(User user, String refreshToken) throws Exception;
    
}
