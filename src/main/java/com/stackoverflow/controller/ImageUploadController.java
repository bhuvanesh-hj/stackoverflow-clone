package com.stackoverflow.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class ImageUploadController {

    private final S3Client s3Client;

    private final String bucketName;

    private final String region;

    public ImageUploadController(@Value("${aws.accessKeyId}") String accessKeyId,
                                 @Value("${aws.secretKey}") String secretKey,
                                 @Value("${aws.s3.region}") String region,
                                 @Value("${aws.s3.bucketName}") String bucketName) {
        this.bucketName = bucketName;
        this.region = region;
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretKey);
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();

        try {
            if (file.getSize() > 3 * 1024 * 1024) {
                response.put("error", "File size exceeds 3MB limit.");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            String fileName = file.getOriginalFilename();
            String filePath = "images/" + fileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

            String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, filePath);

            response.put("location", imageUrl);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }

}