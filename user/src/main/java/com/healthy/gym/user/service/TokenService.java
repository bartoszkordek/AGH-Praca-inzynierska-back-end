package com.healthy.gym.user.service;

import com.healthy.gym.user.data.entity.RegistrationToken;
import com.healthy.gym.user.data.entity.ResetPasswordToken;
import com.healthy.gym.user.data.entity.UserEntity;
import com.healthy.gym.user.exceptions.token.ExpiredTokenException;
import com.healthy.gym.user.exceptions.token.InvalidTokenException;
import com.healthy.gym.user.shared.UserDTO;

public interface TokenService {

    ResetPasswordToken createResetPasswordToken(UserEntity userEntity) throws IllegalStateException;

    RegistrationToken createRegistrationToken(UserDTO user, String token);

    void verifyRegistrationToken(String token) throws InvalidTokenException, ExpiredTokenException;

    void verifyResetPasswordToken(String token) throws InvalidTokenException, ExpiredTokenException;
}
