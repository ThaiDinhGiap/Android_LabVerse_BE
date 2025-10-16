package com.mss.prm_project.service.serviceimpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Service
public class S3ServiceV2 {
    private final S3Client s3;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public S3ServiceV2(S3Client s3) {
        this.s3 = s3;
    }

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String key = generateKey(multipartFile);
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(multipartFile.getContentType())
                .contentLength(multipartFile.getSize())
                .build();
        s3.putObject(putReq, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
        return key;
    }

    public byte[] downloadFile(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();
        } catch (S3Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error downloading file from S3: " + e.awsErrorDetails().errorMessage());
        }
    }

    public void deleteFile(String key) {
        DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3.deleteObject(delReq);
    }

    public String getBucketName() {
        return bucketName;
    }

    private String generateKey(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "uploads/" + timestamp + "_" + (originalFilename != null ? originalFilename : "unknown.pdf");
    }
}