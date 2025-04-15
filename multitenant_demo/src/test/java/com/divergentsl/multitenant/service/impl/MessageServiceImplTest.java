package com.divergentsl.multitenant.service.impl;

import com.divergentsl.multitenant.entity.Message;
import com.divergentsl.multitenant.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    private Message message1;
    private Message message2;
    private Long threadId;

    @BeforeEach
    void setUp() {
        // Setup test data
        threadId = 1L;
        message1 = new Message(1L, threadId, 100L, "Test message 1");
        message2 = new Message(2L, threadId, 101L, "Test message 2");
    }

    @Test
    void createMessage_ShouldSaveAndReturnMessage() {
        // Arrange
        when(messageRepository.save(any(Message.class))).thenReturn(message1);

        // Act
        Message result = messageService.createMessage(message1);

        // Assert
        assertNotNull(result);
        assertEquals(message1.getId(), result.getId());
        assertEquals(message1.getContent(), result.getContent());
        verify(messageRepository, times(1)).save(message1);
    }

    @Test
    void getMessages_ShouldReturnListOfMessages() {
        // Arrange
        List<Message> messages = Arrays.asList(message1, message2);
        when(messageRepository.findByThreadId(threadId)).thenReturn(messages);

        // Act
        List<Message> result = messageService.getMessages(threadId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(message1.getId(), result.get(0).getId());
        assertEquals(message2.getId(), result.get(1).getId());
        verify(messageRepository, times(1)).findByThreadId(threadId);
    }

    @Test
    void getRecommendation_ShouldReturnValidSuggestion() {
        // Act
        String recommendation = messageService.getRecommendation(threadId);

        // Assert
        assertNotNull(recommendation);
        assertTrue(Arrays.asList("Follow up", "Escalate", "Close", "Investigate").contains(recommendation),
                "Recommendation should be one of the expected values");
        
        // No repository calls expected for this method
        verifyNoInteractions(messageRepository);
    }

    @Test
    void getRecommendation_ShouldReturnDifferentSuggestions() {
        // This test verifies that the random suggestion works
        // by checking that at least one different suggestion appears
        // after multiple calls

        boolean differentSuggestionFound = false;
        String firstSuggestion = messageService.getRecommendation(threadId);
        
        // Try multiple times to get different suggestion
        for (int i = 0; i < 20 && !differentSuggestionFound; i++) {
            String nextSuggestion = messageService.getRecommendation(threadId);
            if (!firstSuggestion.equals(nextSuggestion)) {
                differentSuggestionFound = true;
            }
        }
        
        assertTrue(differentSuggestionFound, 
                "Random suggestion should eventually return different values");
    }
} 