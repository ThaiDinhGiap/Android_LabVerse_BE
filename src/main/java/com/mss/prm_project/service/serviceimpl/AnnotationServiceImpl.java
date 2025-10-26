package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.dto.AnnotationDTO;
import com.mss.prm_project.dto.UserDTO;
import com.mss.prm_project.entity.Annotation;
import com.mss.prm_project.entity.File;
import com.mss.prm_project.entity.Paper;
import com.mss.prm_project.entity.User;
import com.mss.prm_project.mapper.AnnotationMapper;
import com.mss.prm_project.mapper.UserMapper;
import com.mss.prm_project.repository.AnnotationRepository;
import com.mss.prm_project.repository.PaperRepository;
import com.mss.prm_project.repository.UserRepository;
import com.mss.prm_project.service.AnnotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnotationServiceImpl implements AnnotationService {
    private final AnnotationRepository annotationRepository;
    private final UserRepository userRepository;
    private final S3ServiceV2 s3ServiceV2;
    private final PaperRepository paperRepository;


    @Override
    public Set<UserDTO> shareReaderToAnnotation(long annotationId, long userId) {
        Annotation annotation = annotationRepository.findById(annotationId).get();
        User user = userRepository.findById(userId).get();
        Set<UserDTO> userDTOSet = new HashSet<>();
        if(Objects.nonNull(user)) {
            Set<User> readerDTOs = annotation.getReaders();
            readerDTOs.add(user);
            annotationRepository.save(annotation);
            userDTOSet = readerDTOs.stream().map(UserMapper.INSTANCE::userToUserDTO).collect(Collectors.toSet());
        }
        return userDTOSet;
    }

    @Override
    public Set<UserDTO> removeReaderFromAnnotation(long annotationId, long userId) {
        Annotation annotation = annotationRepository.findById(annotationId).get();
        Set<UserDTO> userDTOSet = new HashSet<>();
        User user = userRepository.findById(userId).get();
        if (annotation.getReaders().contains(user)) {
            Set<User> readerDTOs = annotation.getReaders();
            boolean addResult = readerDTOs.remove(user);
            annotationRepository.save(annotation);
            userDTOSet = readerDTOs.stream().map(UserMapper.INSTANCE::userToUserDTO).collect(Collectors.toSet());
        }
        return userDTOSet;
    }

    @Override
    public List<AnnotationDTO> findAllReadableAnnotationByUserId(long userId) {
        List<AnnotationDTO> annotationDTOList = new ArrayList<>();
        annotationDTOList = annotationRepository.findAllReadableByUserId(userId).stream().map(AnnotationMapper.INSTANCE::toDTO).collect(Collectors.toList());
        return annotationDTOList;
    }

    @Override
    public AnnotationDTO findAnnotationById(long annotationId) {
        return AnnotationMapper.INSTANCE.toDTO(annotationRepository.findById(annotationId).get());
    }

    @Override
    public AnnotationDTO createAnnotation(String annotationName, MultipartFile multipartFile, int paperId, int userId) throws IOException {
        Annotation annotation = annotationRepository.findByPaperPaperIdAndOwnerUserId(paperId, userId);
        if(Objects.nonNull(annotation)) {
            s3ServiceV2.deleteFile(annotation.getAnnotationUrl());
            annotation.setAnnotationUrl(s3ServiceV2.uploadFile(multipartFile));
        }else{
            annotation = new Annotation();
            annotation.setAnnotationName(annotationName);
            annotation.setAnnotationUrl(s3ServiceV2.uploadFile(multipartFile));
            User user = userRepository.findById((long) userId).get();
            annotation.setOwner(user);
            annotation.setReaders(new HashSet<>(Arrays.asList(user)));
            Paper paper = paperRepository.findById((long) paperId).get();
            annotation.setPaper(paper);
          
        }
        Annotation savedAnnotation = annotationRepository.save(annotation);
        return AnnotationMapper.INSTANCE.toDTO(savedAnnotation);
    }

    @Override
    public boolean deleteAnnotation(long annotationId) {
        Annotation annotation = annotationRepository.findById(annotationId).get();
        annotationRepository.delete(annotation);
        return false;
    }
}
