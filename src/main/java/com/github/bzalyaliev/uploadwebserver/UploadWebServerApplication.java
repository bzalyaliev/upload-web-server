package com.github.bzalyaliev.uploadwebserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;

import java.io.File;


@SpringBootApplication
public class UploadWebServerApplication {

    public static void main(String[] args) {
        String uploadDirPath = new FileSystemResource("").getFile().getAbsolutePath() + File.separator + "uploaded-videos";

        // Создание директории uploaded-videos
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            deleteDirectory(uploadDir);
        }));

        SpringApplication.run(UploadWebServerApplication.class, args);
    }

    private static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        dir.delete();

    }
}
