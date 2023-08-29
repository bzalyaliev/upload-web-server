package com.github.bzalyaliev.uploadwebserver.controller;

import com.github.bzalyaliev.uploadwebserver.model.Video;
import com.github.bzalyaliev.uploadwebserver.repository.VideoRepository;
import com.github.bzalyaliev.uploadwebserver.service.VideoTypeChecker;
import com.github.bzalyaliev.uploadwebserver.service.VideoUploadManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/videos")
public class VideoController {

    String uploadDirectory = new FileSystemResource("").getFile().getAbsolutePath() + "/uploaded-videos";

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoUploadManager videoUploadManager;

    @Autowired
    private VideoTypeChecker videoTypeChecker;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file,
                                              @RequestParam("username") String username) throws IOException {
        if (!videoTypeChecker.isVideoFile(file)) {
            return ResponseEntity.badRequest().body("Invalid file format. Only video files are allowed.");
        }

        if (videoUploadManager.isUploadLimitExceeded(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Upload limit exceeded.");
        }

        try {
            String fileName = saveFileToDisk(file);
            Video video = new Video(username, fileName);
            videoRepository.save(video);
            videoUploadManager.incrementUploadCount(username);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload the file.");
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<Video>> getVideosForUser(@PathVariable String username) {
        List<Video> videos = videoRepository.findByUsername(username);
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<InputStreamResource> downloadVideo(@PathVariable String fileName) throws IOException {
        File videoFile = new File(uploadDirectory + File.separator + fileName);

        if (videoFile.exists() && videoFile.isFile()) {
            InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(videoFile));
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(inputStreamResource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private String saveFileToDisk(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        File targetFile = new File(uploadDirectory + File.separator + fileName);
        try {
            file.transferTo(targetFile);
            log.info("File {} uploaded successfully.", fileName);
            return fileName;
        } catch (IOException e) {
            log.error("Failed to upload file {}: {}", fileName, e.getMessage());
            throw e;
        }
    }
}


