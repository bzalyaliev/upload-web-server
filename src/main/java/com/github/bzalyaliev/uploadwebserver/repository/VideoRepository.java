package com.github.bzalyaliev.uploadwebserver.repository;

import com.github.bzalyaliev.uploadwebserver.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByUsername(String username);
    Video findByUsernameAndId(String username, Long id);
    }

