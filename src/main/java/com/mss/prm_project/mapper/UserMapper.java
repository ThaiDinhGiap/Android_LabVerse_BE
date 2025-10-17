package com.mss.prm_project.mapper;

import com.mss.prm_project.dto.UserDTO;
import com.mss.prm_project.entity.Role;
import com.mss.prm_project.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "role.roleId", target = "role")
    UserDTO userToUserDTO(User user);

    @Mapping(source = "role", target = "role")
    @Mapping(source = "id", target = "userId")
    User userDTOToUser(UserDTO dto);

    default Role map(Long id) {
        if (id == null) return null;
        return new Role(id);
    }
}
