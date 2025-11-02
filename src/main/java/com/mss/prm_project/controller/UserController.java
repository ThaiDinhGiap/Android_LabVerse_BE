package com.mss.prm_project.controller;

import com.mss.prm_project.dto.PasswordChangeDTO;
import com.mss.prm_project.dto.ProfileDTO;
import com.mss.prm_project.dto.SettingDTO;
import com.mss.prm_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile/{username}")
    public ResponseEntity<ProfileDTO> getUserProfileByUsername(@PathVariable("username") String username) throws Exception {
        ProfileDTO userProfile = userService.getUserProfileByUsername(username);
        return ResponseEntity.ok(userProfile);
    }

    @PostMapping("/profile")
    public ResponseEntity<ProfileDTO> updateProfile(@RequestBody ProfileDTO profileDTO) throws Exception{
        return ResponseEntity.ok(userService.updateProfile(profileDTO));
    }

    @PostMapping("/password")
    public ResponseEntity<Boolean> changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO) {
        return ResponseEntity.ok(userService.updateUserPassword(passwordChangeDTO));
    }

    @PostMapping("/settings")
    public ResponseEntity<Boolean> updateSetting(@RequestBody SettingDTO settingDTO) {
        return ResponseEntity.ok(userService.updateNotificationPreferences(settingDTO));
    }
}
