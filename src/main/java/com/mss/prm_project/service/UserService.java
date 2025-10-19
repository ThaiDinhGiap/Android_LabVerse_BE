package com.mss.prm_project.service;

import com.mss.prm_project.dto.UserDTO;
import com.mss.prm_project.entity.User;

import java.util.Optional;

public interface UserService {
    UserDTO getUserById(Long userId) throws Exception;
    Optional<UserDTO> getUserByEmail(String email);
    UserDTO createUser(User user);
    UserDTO getUserByGoogleSub(String sub) throws Exception;
    boolean checkIfEmailExists(String email);
    void updateEmailVerified(String email) throws Exception;
}
