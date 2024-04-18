package com.gachonoj.memberservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 이미지 저장
    public String saveFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        // 확장자
        assert originalFilename != null;
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 새 파일명 생성 (UUID)
        String newFilename = UUID.randomUUID() + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, newFilename, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, newFilename).toString();
    }
    // 이미지 삭제
    public void deleteImage(String filename)  {
        amazonS3.deleteObject(bucket, filename);
    }
}
