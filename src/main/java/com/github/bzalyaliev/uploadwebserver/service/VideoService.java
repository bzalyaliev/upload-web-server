package com.github.bzalyaliev.uploadwebserver.service;

import com.github.bzalyaliev.uploadwebserver.model.Video;
import com.github.bzalyaliev.uploadwebserver.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    public List<Video> getAllUploadedVideos() {
        return videoRepository.findAll();
    }

    public Video saveVideo(Video video) {
        return videoRepository.save(video);
    }

}
