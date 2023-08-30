package com.github.bzalyaliev.uploadwebserver.controller;

import com.github.bzalyaliev.uploadwebserver.model.ProgressUpdateMessage;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final String uploadDirectory = new FileSystemResource("").getFile().getAbsolutePath() + File.separator + "uploaded-videos";

    // Внедряю бин для отправки сообщений через WebSocket
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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

        // Получаю общий размер файла
        long totalFileSize = file.getSize();

        // Читаю данные из MultipartFile и обновляю прогресс
        try (InputStream inputStream = file.getInputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            long uploadedBytes = 0;

            //Отправляю сообщение по каналу /topic/progress-start об объекте, который начал загружаться
            messagingTemplate.convertAndSend("/topic/progress-start", new ProgressUpdateMessage(username, file.getName(), 0));

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // Обновляю uploadedBytes и рассчитываю прогресс
                uploadedBytes += bytesRead;
                double progress = calculateProgress(totalFileSize, uploadedBytes);

                //Отправляю сообщение по каналу /topic/progress о текущем состоянии загрузки
                messagingTemplate.convertAndSend("/topic/progress", new ProgressUpdateMessage(username, file.getName(), progress));
                ;
            }
        }

        // Файл прочитан - начинаю загружать его на диск
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

    @GetMapping("/download/{username}/{videoId}")
    public ResponseEntity<InputStreamResource> downloadVideo(@PathVariable String username, @PathVariable Long videoId) throws IOException {
        Video video = videoRepository.findByUsernameAndId(username, videoId);

        if (video == null) {
            return ResponseEntity.notFound().build();
        }

        String fileName = video.getFileName();
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
        String originalFileName = file.getOriginalFilename();
        String sanitizedFileName = sanitizeFileName(originalFileName);

        File targetFile = new File(uploadDirectory + File.separator + sanitizedFileName);
        try {
            file.transferTo(targetFile);
            log.info("File {} uploaded successfully.", sanitizedFileName);
            return sanitizedFileName;
        } catch (IOException e) {
            log.error("Failed to upload file {}: {}", sanitizedFileName, e.getMessage());
            throw e;
        }
    }

    private String sanitizeFileName(String fileName) throws UnsupportedEncodingException {
        return URLEncoder.encode(fileName, "UTF-8");
    }

    private double calculateProgress(long totalFileSize, long uploadedBytes) {
        if (totalFileSize == 0) {
            return 0.0;
        }
        return (double) uploadedBytes / totalFileSize * 100;
    }

}


