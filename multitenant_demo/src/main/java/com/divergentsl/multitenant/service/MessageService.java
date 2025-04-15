package com.divergentsl.multitenant.service;

import com.divergentsl.multitenant.entity.Message;

import java.util.List;


public interface MessageService {

    /**
     * Retrieves all messages associated with a specific thread.
     *
     * @param threadId The unique identifier of the thread
     * @return List of messages in the thread
     */
    List<Message> getMessages(Long threadId);

    /**
     * Creates a new message.
     *
     * @param messageDTO Data transfer object containing message information
     * @return The newly created message entity
     */
    Message createMessage(Message messageDTO);


    String getRecommendation(Long threadId);
}
