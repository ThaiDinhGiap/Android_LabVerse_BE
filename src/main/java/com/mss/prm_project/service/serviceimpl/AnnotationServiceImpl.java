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
    public List<UserDTO> shareAnnotationToOther(long paperId, List<Long> userIdList) {
        String currentUsername = SecurityUtils.getCurrentUserName().get();
        User user = userRepository.findByUsername(currentUsername).orElseThrow(()-> new IllegalArgumentException("User not found"));
        Annotation annotation = annotationRepository.findByPaperPaperIdAndOwnerUserId((int)paperId, user.getUserId());
        List<User> newReaderList = annotation.getReaders();
        List<UserDTO> readerDTOList = new ArrayList<>();
        for(Long userId : userIdList) {
            User newUser = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("User not found"));
            if(!newReaderList.contains(newUser)) {
                newReaderList.add(newUser);
            }
        }
        annotation.setReaders(newReaderList);
        annotationRepository.save(annotation);
        readerDTOList = newReaderList.stream().map(UserMapper.INSTANCE::userToUserDTO).collect(Collectors.toList());
        return readerDTOList;
    }

    @Override
    public List<UserDTO> removeReaderFromAnnotation(long annotationId, long userId) {
        Annotation annotation = annotationRepository.findById(annotationId).get();
        List<UserDTO> userDTOList = new ArrayList<>();
        User user = userRepository.findById(userId).get();
        if (annotation.getReaders().contains(user)) {
            List<User> readerDTOs = annotation.getReaders();
            boolean addResult = readerDTOs.remove(user);
            annotationRepository.save(annotation);
            userDTOList = readerDTOs.stream().map(UserMapper.INSTANCE::userToUserDTO).collect(Collectors.toList());
        }
        return userDTOList;
    }

    @Override
    public List<AnnotationDTO> findAllReadableAnnotationByUserId(int collectionId, long paperId) {
        String currentUsername = SecurityUtils.getCurrentUserName().get();
        long userId = userRepository.findByUsername(currentUsername).get().getUserId();
        List<Annotation> annotationList = new ArrayList<>();
        Collection collection = collectionRepository.findById((long) collectionId).get();
        List<CollectionMember> collectionMembers = collection.getMembers();
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
            annotation.setReaders(new ArrayList<>(Arrays.asList(user)));
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
    public AnnotationDTO importAnnotationFromOtherMember(long annotationId) {
        String currentUsername = SecurityUtils.getCurrentUserName().get();
        User user = userRepository.findByUsername(currentUsername).get();
        Annotation annotationNew = annotationRepository.findById(annotationId)
                .orElseThrow(() -> new IllegalArgumentException("Annotation with ID " + annotationId + " not found"));
        Paper paper = annotationNew.getPaper();
        Annotation annotationRoot = annotationRepository.findByPaperPaperIdAndOwnerUserId(user.getUserId(), paper.getPaperId());
        if(Objects.nonNull(annotationRoot)) {
            String newUrl = annotationNew.getAnnotationUrl();
            annotationRoot.setAnnotationUrl(newUrl);
        }else {
            annotationRoot = new Annotation();
            annotationRoot.setAnnotationName(annotationNew.getAnnotationName());
            annotationRoot.setAnnotationUrl(annotationNew.getAnnotationUrl());
            annotationRoot.setOwner(user);
            annotationRoot.setPaper(paper);
            annotationRoot.setReaders(new ArrayList<>(Arrays.asList(user)));
        }
        annotationRepository.save(annotationRoot);
        return AnnotationMapper.INSTANCE.toDTO(annotationRoot);
    }
}
