package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.dto.UserDTO;
import com.mss.prm_project.entity.Role;
import com.mss.prm_project.entity.User;
import com.mss.prm_project.mapper.UserMapper;
import com.mss.prm_project.repository.RoleRepository;
import com.mss.prm_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.mss.prm_project.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDTO getUserById(Long userId) throws Exception {
        return userRepository.findById(userId + 1)
                .map(UserMapper.INSTANCE::userToUserDTO)
                .orElseThrow(() -> new Exception("User Not Found"));
    }

    @Override
    public Optional<UserDTO> getUserByEmail(String email) {
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
}