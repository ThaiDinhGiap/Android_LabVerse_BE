package com.mss.prm_project.service.serviceimpl;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.mss.prm_project.dto.PasswordChangeDTO;
import com.mss.prm_project.dto.ProfileDTO;
import com.mss.prm_project.dto.SettingDTO;
import com.mss.prm_project.dto.UserDTO;
import com.mss.prm_project.entity.Role;
import com.mss.prm_project.entity.User;
import com.mss.prm_project.mapper.UserMapper;
import com.mss.prm_project.model.NotificationPayload;
import com.mss.prm_project.repository.RoleRepository;
import com.mss.prm_project.service.FcmService;
import com.mss.prm_project.service.RedisService;
import com.mss.prm_project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.mss.prm_project.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FcmService fcmService;
    private final RedisService redisService;

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
        Optional<Role> role = roleRepository.findById(2L);
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
        profileDTO.setInstantPushNotification(user.isInstantPushNotification());
        profileDTO.setScheduledPushNotification(user.isScheduledPushNotification());
        if (user.getGoogleSub() != null) {
            profileDTO.setGoogleLinked(true);
        } else {
            profileDTO.setGoogleLinked(false);
        }

        String scheduledTime = redisService.getUserScheduledTime(user.getUserId());
        profileDTO.setScheduledTime(scheduledTime);

        return profileDTO;
    }

    @Override
    public boolean updateUserPassword(PasswordChangeDTO passwordChangeDTO) {
        try {
            User user = userRepository.findByUsername(passwordChangeDTO.getUserName()).get();
            if (!passwordEncoder.matches(passwordChangeDTO.getOldPassword(), user.getPassword())) {
                return false;
            }
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

            String oldTime = redisService.getUserScheduledTime(user.getUserId());
            String newTime = settingDTO.getScheduledTime();
            if (oldTime != null && !oldTime.isEmpty()) {
                redisService.removeUserFromTimeSet(oldTime, user.getUserId());
            }
            if (settingDTO.isScheduledNotification() && newTime != null && !newTime.isEmpty()) {
                redisService.addUserToTimeSet(newTime, user.getUserId());
                redisService.saveUserScheduledTime(user.getUserId(), newTime);
            } else {

                redisService.deleteUserScheduledTime(user.getUserId());
            }

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
        userRepository.save(user);
    }

    @Override
    public void sendUnreadNotificationsToUser(String username) throws FirebaseMessagingException {
        User user = userRepository.findByUsername(username).get();
        int userId = Math.toIntExact(user.getUserId());
        String fcmToken = user.getFcmToken();

        List<NotificationPayload> missedNotifications = redisService.getAndClearMissedNotifications(userId);

        if (missedNotifications.isEmpty()) {
            log.info("User {} không có thông báo lỡ.", userId);
            return;
        }

        log.info("Đang gửi {} thông báo đã lỡ cho User {}...", missedNotifications.size(), userId);
        for (NotificationPayload payload : missedNotifications) {
            try {
                fcmService.sendNotificationToToken(
                        fcmToken,
                        payload.title(),
                        payload.body(),
                        payload.data()
                );
            } catch (FirebaseMessagingException e) {
                log.error("Lỗi khi gửi thông báo lỡ cho user {}: {}", userId, e.getMessage());
            }
        }
    }
}