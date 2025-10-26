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

    @Value("${aws.s3.region}")
    private String region;

    public S3ServiceV2(S3Client s3) {
        this.s3 = s3;
    }

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String key = generateKey(multipartFile);
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(multipartFile.getContentType())
                .build();
        s3.putObject(putReq, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
        String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
        return fileUrl;
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

    public void deleteFile(String url) {
        String filekey = this.extractFileKeyFromUrl(url);
        DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(filekey)
                .build();
        s3.deleteObject(delReq);
    }

    private String extractFileKeyFromUrl(String fileUrl) {
        String bucketName = "prm392-labverse";
        String marker = bucketName + ".s3.";

        int markerIndex = fileUrl.indexOf(marker);
        if (markerIndex == -1) {
            throw new IllegalArgumentException("Invalid S3 URL: missing bucket name");
        }

        // Tìm vị trí dấu "/" đầu tiên sau phần domain của S3
        int startIndex = fileUrl.indexOf("/", markerIndex);
        if (startIndex == -1 || startIndex + 1 >= fileUrl.length()) {
            throw new IllegalArgumentException("Invalid S3 URL: missing file key");
        }

        return fileUrl.substring(startIndex + 1);
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