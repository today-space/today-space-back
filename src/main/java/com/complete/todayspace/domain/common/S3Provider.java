package com.complete.todayspace.domain.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3Provider {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.baseUrl}")
    private String s3BaseUrl;

    private final AmazonS3 s3Client;

    public void deleteFile(String fileUrl) {
        String filePath = fileUrl.replace(s3BaseUrl, "");
        s3Client.deleteObject(new DeleteObjectRequest(bucket, filePath));
    }

    public String getS3Url(String filePath) {
        if (filePath.startsWith(s3BaseUrl)) {
            return filePath;
        }
        return s3BaseUrl + filePath;
    }
}
