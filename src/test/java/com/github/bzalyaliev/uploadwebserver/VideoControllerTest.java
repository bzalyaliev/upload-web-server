package com.github.bzalyaliev.uploadwebserver;

import com.github.bzalyaliev.uploadwebserver.controller.VideoController;

import com.github.bzalyaliev.uploadwebserver.repository.VideoRepository;
import com.github.bzalyaliev.uploadwebserver.service.VideoTypeChecker;
import com.github.bzalyaliev.uploadwebserver.service.VideoUploadManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;



@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class VideoControllerTest {

    @InjectMocks
    private VideoController videoController;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private VideoUploadManager videoUploadManager;

    @Mock
    private VideoTypeChecker videoTypeChecker;

    @BeforeEach
    public void setUp() {
        videoRepository.deleteAll();
    }

    @Test
    @WithMockUser("username")
    public void testUploadVideo_InvalidFile_FormatError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "video.txt", "text/plain", "test text content".getBytes());

        when(videoTypeChecker.isVideoFile(file)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/videos/upload")
                        .file(file)
                        .param("username", "username"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid file format. Only video files are allowed."));
    }

    @Test
    @WithMockUser("username")
    public void testDownloadVideo_NonExistingFile_NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/videos/download/nonexistent.mp4"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
