package com.divergentsl.multitenant.web;

import com.divergentsl.multitenant.TestApplication;
import com.divergentsl.multitenant.config.TestSecurityConfig;
import com.divergentsl.multitenant.entity.Message;
import com.divergentsl.multitenant.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MessageController.class)
@ContextConfiguration(classes = TestApplication.class)
@Import(TestSecurityConfig.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    private Message message1;
    private Message message2;

    @BeforeEach
    void setUp() {
        message1 = new Message(1L, 1L, 100L, "user1");
        message2 = new Message(2L, 2L, 100L, "user2");
    }

    @Test
    @WithMockUser
    void testPostMessage() throws Exception {
        when(messageService.createMessage(any(Message.class))).thenReturn(message1);

        mockMvc.perform(post("/api/v1/messages")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(message1.getId()))
                .andExpect(jsonPath("$.content").value(message1.getContent()))
                .andExpect(jsonPath("$.threadId").value(message1.getThreadId()))
                .andExpect(jsonPath("$.userId").value(message1.getUserId()));
    }

    @Test
    @WithMockUser
    void testGetMessages() throws Exception {
        List<Message> messages = Arrays.asList(message1, message2);
        when(messageService.getMessages(100L)).thenReturn(messages);

        mockMvc.perform(get("/api/v1/messages/thread/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value(message1.getContent()))
                .andExpect(jsonPath("$[1].content").value(message2.getContent()));
    }
}
