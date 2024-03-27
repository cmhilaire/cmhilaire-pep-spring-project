package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

@Service
public class MessageService {

    private MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository){
        this.messageRepository = messageRepository;
    }

    /**
     * ...
     * @param message a transient message
     * @return the persisted message
     */
    public Message persistMessage(Message message){
        return messageRepository.saveAndFlush(message);
    }

    /**
     * finaAll() will return all message entities stored in the DB table.
     * @return all Message entities.
     */
    public List<Message> getAllMessage(){
        return messageRepository.findAll();
    }

    /**
     * ...
     * @param id the id of the Message entity.
     * @return Message entity
     */
    public Message getMessageById(int id){
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if(optionalMessage.isPresent()){
            return optionalMessage.get();
        }else{
            return null;
        }
    }

    /**
     * Delete a message entity of a certain id.
     * @param id
     * @return
     */
    public void deleteMessage(int id){
        messageRepository.deleteById(id);
    }

    /**
     * Given a message ID, overwrite the contents of the message entity.
     * @param id
     * @return
     */
    public void updateMessage(int id, Message replacement){
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if(optionalMessage.isPresent()){
            Message message = optionalMessage.get();
            message.setMessage_text(replacement.getMessage_text());
            this.persistMessage(message);
        }
    }

     /**
     * ...
     * @return all messages
     */
    public List<Message> getAllMessages() {
        return this.messageRepository.findAll();
    }

    /**
     * ...
     * @param message an message object.
     * @return The persisted message if the persistence is successful.
     */
    public Message addMessage(Message message) {
        if(message.getMessage_text() != "" && message.getMessage_text().length() <= 255 ){
            // if(!this.getMessageByAccountId(message.getPosted_by()).isEmpty()){
                return this.persistMessage(message);
            // }
        }
        return null;
    }

    /**
     * ...
     * @param message_id
     * @param message_text
     * @return
     */
    public Message patchMessageById(int message_id, Message message){
        if(message_id >= 0 && message.getMessage_text().trim() != "" && message.getMessage_text().trim().length() <= 255){
            Message newMessage = this.getMessageById(message_id);
            if(newMessage == null) return null;
            newMessage = message;
            return this.persistMessage(newMessage);
        }
        return null;
    }

    /**
     * ...
     * @param message_id
     * @return
     */
    public List<Message> getMessageByAccountId(int account_id){
        return this.messageRepository.findByPostedBy(account_id);
    }

    
}
