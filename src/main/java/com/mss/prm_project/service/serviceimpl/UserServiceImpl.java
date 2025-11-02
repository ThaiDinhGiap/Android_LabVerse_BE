package com.mss.prm_project.service.serviceimpl;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.mss.prm_project.dto.PasswordChangeDTO;
import com.mss.prm_project.dto.ProfileDTO;
import com.mss.prm_project.dto.SettingDTO;
import com.mss.prm_project.dto.UserDTO;
import com.mss.prm_project.entity.Role;
import com.mss.prm_project.entity.User;
import com.mss.prm_project.mapper.UserMapper;
import com.mss.prm_project.repository.RoleRepository;
import com.mss.prm_project.service.FcmService;
import com.mss.prm_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.mss.prm_project.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FcmService fcmService;

    @Override
    public UserDTO getUserById(Long userId) throws Exception {
        return userRepository.findById(userId)
                .map(UserMapper.INSTANCE::userToUserDTO)
                .orElseThrow(() -> new Exception("User Not Found"));
    }

    @Override
    public Optional<UserDTO> getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).get();
        return userRepository.findByEmail(email)

                .map(UserMapper.INSTANCE::userToUserDTO);

    }

    @Override
    public UserDTO createUser(User user) {
        Optional<Role> role = roleRepository.findById(1L);
        user.setRole(role.get());
        return UserMapper.INSTANCE.userToUserDTO(userRepository.save(user));
    }

    @Override
    public UserDTO getUserByGoogleSub(String googleSub) throws Exception {
        return userRepository.findByGoogleSub(googleSub)
                .map(UserMapper.INSTANCE::userToUserDTO)
                .orElseThrow(() -> new Exception("User Not Found"));
    }

    @Override
    public boolean checkIfEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void updateEmailVerified(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User Not Found"));

        user.setEmailVerifyAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public ProfileDTO getUserProfileByUsername(String username) throws Exception {
        User user = userRepository.findByUsername(username).get();
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setName(user.getFullName());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setInstantNotification(user.isInstantPushNotification());
        profileDTO.setScheduledNotification(user.isScheduledPushNotification());
        if (user.getGoogleSub() != null) {
            profileDTO.setGoogleLinked(true);
        } else {
            profileDTO.setGoogleLinked(false);
        }
        return profileDTO;
    }

    @Override
    public boolean updateUserPassword(PasswordChangeDTO passwordChangeDTO) {
        try {
            User user = userRepository.findByUsername(passwordChangeDTO.getUserName()).get();
            user.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateNotificationPreferences(SettingDTO settingDTO) {
        try {
            User user = userRepository.findByUsername(settingDTO.getUserName()).get();
            user.setInstantPushNotification(settingDTO.isInstantNotification());
            user.setScheduledPushNotification(settingDTO.isScheduledNotification());
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ProfileDTO updateProfile(ProfileDTO profileDTO) throws Exception {
        User user = userRepository.findByUsername(profileDTO.getUserName()).get();
        String newEmail = profileDTO.getEmail();
        String oldEmail = user.getEmail();

        if (!oldEmail.equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new Exception("Email already in use!");
        }

        if (!oldEmail.equals(newEmail)) {
            user.setEmail(newEmail);
            user.setGoogleSub(null);
        }

        user.setFullName(profileDTO.getName());
        userRepository.save(user);
        return profileDTO;
    }

    @Override
    public void updateFcmToken(String username, String fcmToken) throws FirebaseMessagingException {
        User user = userRepository.findByUsername(username).get();
        user.setFcmToken(fcmToken);
        fcmService.sendNotificationToToken(fcmToken, "FCM Token Updated", "Your FCM token has been successfully updated.");
        userRepository.save(user);
    }
}