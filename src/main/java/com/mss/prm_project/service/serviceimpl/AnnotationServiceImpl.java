package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.dto.AnnotationDTO;
import com.mss.prm_project.dto.UserDTO;
import com.mss.prm_project.entity.*;
import com.mss.prm_project.entity.Collection;
import com.mss.prm_project.mapper.AnnotationMapper;
import com.mss.prm_project.mapper.UserMapper;
import com.mss.prm_project.repository.*;
import com.mss.prm_project.service.AnnotationService;
import com.mss.prm_project.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final CollectionRepository collectionRepository;


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
    public List<AnnotationDTO> findAllReadableAnnotationByUserId(int collectionId, long paperId) {
        String currentUsername = SecurityUtils.getCurrentUserName().get();
        long userId = userRepository.findByUsername(currentUsername).get().getUserId();
        List<Annotation> annotationList = new ArrayList<>();
        Collection collection = collectionRepository.findById((long) collectionId).get();
        Set<CollectionMember> collectionMembers = collection.getMembers();
        List<User> usersInCollection = new ArrayList<>();
        for (CollectionMember collectionMember : collectionMembers) {
            usersInCollection.add(collectionMember.getUser());
        }
        annotationList = annotationRepository.findAllSharableByUserIdAndPaperId(userId, paperId);
        for (Annotation annotation : annotationList) {
            if(!usersInCollection.contains(annotation)){
                annotationList.remove(annotation);
            }
        }
        return annotationList.stream().map(AnnotationMapper.INSTANCE::toDTO).collect(Collectors.toList());
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

    @Override
    public AnnotationDTO importAnnotationFromOtherMember(long annotationId, int paperId) {
        String currentUsername = SecurityUtils.getCurrentUserName().get();
        int userId = userRepository.findByUsername(currentUsername).get().getUserId();
        Annotation annotationNew = annotationRepository.findById(annotationId).get();
        Annotation annotationRoot = annotationRepository.findByPaperPaperIdAndOwnerUserId(userId, paperId);
        if(Objects.nonNull(annotationRoot) && Objects.nonNull(annotationNew)) {
            String newUrl = annotationNew.getAnnotationUrl();
            annotationRoot.setAnnotationUrl(newUrl);
        }
        return AnnotationMapper.INSTANCE.toDTO(annotationRoot);
    }
}
