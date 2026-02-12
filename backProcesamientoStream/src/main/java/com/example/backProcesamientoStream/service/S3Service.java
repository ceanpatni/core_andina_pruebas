package com.example.backProcesamientoStream.service;



import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;


@Service
@RequiredArgsConstructor
public class S3Service {

    private S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    @PostConstruct
    public void init() {
        s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * Sube un archivo a S3.
     *
     * @param data     bytes del archivo
     * @param fileName nombre del archivo en S3
     * @return URL del archivo en S3
     */
    public String uploadFile(byte[] data, String fileName) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .acl(ObjectCannedACL.PUBLIC_READ) // opcional, según permisos
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));

        return getFileUrl(fileName);
    }

    /**
     * Verifica si el archivo existe en S3.
     *
     * @param fileName nombre del archivo
     * @return true si existe
     */
    public boolean fileExists(String fileName) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }

    /**
     * Retorna la URL pública del archivo.
     *
     * @param fileName nombre del archivo
     * @return URL
     */
    public String getFileUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }
}
