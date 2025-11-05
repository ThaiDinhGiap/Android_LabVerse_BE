package com.mss.prm_project.mapper;

import com.mss.prm_project.dto.AnnotationDTO;
import com.mss.prm_project.dto.PaperDTO;
import com.mss.prm_project.dto.UserDTO;
import com.mss.prm_project.entity.Annotation;
import com.mss.prm_project.entity.Paper;
import com.mss.prm_project.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(uses =  {UserMapper.class, PaperMapper.class})
public interface AnnotationMapper {
    AnnotationMapper INSTANCE = Mappers.getMapper(AnnotationMapper.class);

    @Mapping(source = "annotationId", target = "id")
    @Mapping(source = "owner", target = "ownerDTO")
    @Mapping(source = "paper", target = "paperDTO")
    @Mapping(target = "readerDTO", expression = "java(mapReadersToDTO(entity.getReaders()))")
    AnnotationDTO toDTO(Annotation entity);

    @Mapping(source = "id", target = "annotationId")
    @Mapping(source = "ownerDTO", target = "owner")
    @Mapping(target = "readers", expression = "java(mapDTOToReaders(dto.getReaderDTO()))")
    @Mapping(source = "paperDTO", target = "paper")
    Annotation toEntity(AnnotationDTO dto);

    // ====== Helper methods ======

    default List<UserDTO> mapReadersToDTO(List<User> readers) {
        if (readers == null) return null;
        return readers.stream()
                .map(UserMapper.INSTANCE::userToUserDTO)
                .collect(Collectors.toList());
    }

    default List<User> mapDTOToReaders(List<UserDTO> readerDTOs) {
        if (readerDTOs == null) return null;
        return readerDTOs.stream()
                .map(UserMapper.INSTANCE::userDTOToUser)
                .collect(Collectors.toList());
    }
}
