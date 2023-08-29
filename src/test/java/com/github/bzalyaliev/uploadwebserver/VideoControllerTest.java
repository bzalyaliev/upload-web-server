package com.github.bzalyaliev.uploadwebserver;

import com.github.bzalyaliev.uploadwebserver.controller.VideoController;
import com.github.bzalyaliev.uploadwebserver.model.Video;
import com.github.bzalyaliev.uploadwebserver.repository.VideoRepository;
import com.github.bzalyaliev.uploadwebserver.service.VideoTypeChecker;
import com.github.bzalyaliev.uploadwebserver.service.VideoUploadManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.FileCopyUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;

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
        videoRepository.deleteAll(); // Clear the repository before each test
    }

    /*@Test
    @WithMockUser("username")
    public void testUploadVideo_ValidFile_Success() throws Exception {
        ClassPathResource videoResource = new ClassPathResource("videos/sample_video.mp4");
        MockMultipartFile file = new MockMultipartFile("file", videoResource.getInputStream());

        when(videoTypeChecker.isVideoFile(file)).thenReturn(true);  // Return true to simulate a valid video file
        when(videoUploadManager.isUploadLimitExceeded("username")).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/videos/upload")
                        .file(file)
                        .param("username", "username"))
                .andExpect(MockMvcResultMatchers.status().isOk());  // Expecting a 200 OK
    }
    */
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

    /*@Test
    @WithMockUser("username")
    public void testGetVideosForUser() throws Exception {
        Video video = new Video("username", "download/video.mp4");
        videoRepository.save(video);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/videos/username"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("username"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fileName").value("download/video.mp4"));
    }
*/
    /*@Test
    @WithMockUser("username")
    public void testDownloadVideo_ExistingFile_Success() throws Exception {
        String fileName = "video.mp4";
        Video video = new Video("username", fileName);
        videoRepository.save(video);

        Resource videoResource = new ClassPathResource("download/" + fileName);
        System.out.println(videoResource.getFile().getPath());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/videos/download/" + fileName))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=\"" + fileName + "\""))
                .andExpect(MockMvcResultMatchers.content().bytes(FileCopyUtils.copyToByteArray(videoResource.getInputStream())));
    }*/

    @Test
    @WithMockUser("username")
    public void testDownloadVideo_NonExistingFile_NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/videos/download/nonexistent.mp4"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    // Add more integration test cases for different scenarios

}
