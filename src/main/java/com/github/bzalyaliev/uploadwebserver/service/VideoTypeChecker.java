package com.github.bzalyaliev.uploadwebserver.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class VideoTypeChecker {

    private final Tika tika = new Tika();

    public boolean isVideoFile(MultipartFile file) throws IOException {
        String contentType = tika.detect(file.getBytes());
        return contentType.startsWith("video/");
    }
}
