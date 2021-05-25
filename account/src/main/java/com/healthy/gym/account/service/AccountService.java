package com.healthy.gym.account.service;

import com.healthy.gym.account.exception.IdenticalOldAndNewPasswordException;
import com.healthy.gym.account.exception.OldPasswordDoesNotMatchException;
import com.healthy.gym.account.exception.UserDataNotUpdatedException;
import com.healthy.gym.account.exception.UserPrivacyNotUpdatedException;
import com.healthy.gym.account.shared.UserDTO;
import com.healthy.gym.account.shared.UserPrivacyDTO;

public interface AccountService {
    UserDTO changePassword(String userId, String oldPassword, String newPassword)
            throws IdenticalOldAndNewPasswordException, OldPasswordDoesNotMatchException;

    UserDTO changeUserData(UserDTO userDTO) throws UserDataNotUpdatedException;

    UserDTO deleteAccount(String userId);

    UserPrivacyDTO changeUserPrivacy(UserPrivacyDTO userPrivacyDTO, String userId)
            throws UserPrivacyNotUpdatedException;
}
