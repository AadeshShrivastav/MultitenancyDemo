package com.divergentsl.multitenant.service.impl;

import com.divergentsl.multitenant.entity.Message;
import com.divergentsl.multitenant.repository.MessageRepository;
import com.divergentsl.multitenant.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getMessages(Long threadId) {
        return messageRepository.findByThreadId(threadId);
    }


    @Override
    public String getRecommendation(Long threadId) {
        String[] suggestions = {"Follow up", "Escalate", "Close", "Investigate"};
        return suggestions[new Random().nextInt(suggestions.length)];
    }
}

