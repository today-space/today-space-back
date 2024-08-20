package com.complete.todayspace.domain.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.complete.todayspace.global.exception.CustomException;
import com.complete.todayspace.global.exception.ErrorCode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3Provider {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.baseUrl}")
    private String s3BaseUrl;

    private final AmazonS3 s3Client;

    public void createFolder(String folderName) {
        s3Client.putObject(bucket, folderName + "/", new ByteArrayInputStream(new byte[0]), new ObjectMetadata());
    }

    public void deleteFile(String fileUrl) {
        String filePath = fileUrl.replace(s3BaseUrl, "");
        s3Client.deleteObject(new DeleteObjectRequest(bucket, filePath));
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        if (fileName.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
        }
        ArrayList<String> fileValidate = new ArrayList<>();
        fileValidate.add(".jpg");
        fileValidate.add(".jpeg");
        fileValidate.add(".png");
        fileValidate.add(".JPG");
        fileValidate.add(".JPEG");
        fileValidate.add(".PNG");
        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!fileValidate.contains(idxFileName)) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public String getS3Url(String filePath) {
        if (filePath.startsWith(s3BaseUrl)) {
            return filePath;
        }
        return s3BaseUrl + filePath;
    }
}
