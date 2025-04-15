package com.divergentsl.multitenant.web;

import com.divergentsl.multitenant.entity.Message;
import com.divergentsl.multitenant.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public Message postMessage(@RequestBody Message message) {
        return messageService.createMessage(message);
    }

    @GetMapping("/thread/{threadId}")
    public List<Message> getMessages(@PathVariable Long threadId) {
        return messageService.getMessages(threadId);
    }
}

