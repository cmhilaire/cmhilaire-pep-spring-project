package com.example.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.hibernate.id.IntegralDataTypeHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {

    private AccountService accountService;
    private MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService){
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("login")
    public ResponseEntity<Account> login(@RequestBody Account account) throws AuthenticationException{
        Account connectedAccount = accountService.login(account.getUsername(), account.getPassword());
        // return ResponseEntity.noContent()
        if(connectedAccount == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        else
            return ResponseEntity.status(HttpStatus.OK)
                .body(connectedAccount);
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody Account account){
        boolean flag = true;
        try {
            accountService.register(account);
        } catch (DuplicateKeyException e) {
            System.out.println(e.getMessage() +" => DuplicateKeyException");
            flag = false;
        }
        
        if(flag)
            return ResponseEntity.status(HttpStatus.OK).body("Successfully registered.");
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @DeleteMapping("messages/{message_id}")
    public ResponseEntity<String> deleteMessages(@PathVariable String message_id){
        String strResponse = "";
        if(messageService.getMessageById(Integer.parseInt(message_id)) != null){
            this.messageService.deleteMessage(Integer.parseInt(message_id));
            strResponse = "1";
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(strResponse);
    }

    @GetMapping("/accounts/{account_id}/messages")
    public ResponseEntity<List<Message>> getMessageByAccountId(@PathVariable String account_id){
        List<Message> listMessage = null;
        if(accountService.getAccountById(Integer.parseInt(account_id)) != null){
            listMessage = messageService.getMessageByAccountId(Integer.parseInt(account_id));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(listMessage);
    }

    @GetMapping("messages/{message_id}")
    public ResponseEntity<Message> getMessageById(@PathVariable String message_id){
        Message messages = this.messageService.getMessageById(Integer.parseInt(message_id));
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(messages);
    }

    @GetMapping("messages")
    public ResponseEntity<List<Message>> getMessages(){
        List<Message> messages = this.messageService.getAllMessage();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(messages);
    }

    @PostMapping("messages")
    public ResponseEntity<Message> addMessage(@RequestBody Message message){
        Message savedMessage = null;

        if(accountService.getAccountById(message.getPosted_by()) != null && message.getMessage_text().length() > 0 && message.getMessage_text().length() <= 255 && message.getPosted_by() > 0){
            savedMessage = messageService.addMessage(message);
            return ResponseEntity.status(HttpStatus.OK).body(savedMessage);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PatchMapping("messages/{message_id}")
    public ResponseEntity<String> updateMessage(@PathVariable String message_id, @RequestBody Message message){

        if(Integer.parseInt(message_id) > 0 && 
            messageService.getMessageById(Integer.parseInt(message_id)) != null && 
            !message.getMessage_text().isEmpty() &&
            message.getMessage_text().trim().length() > 0 && 
            message.getMessage_text().length() <= 255){
                Message newMessage = new Message();
                newMessage.setMessage_text(message.getMessage_text());
                messageService.updateMessage(Integer.parseInt(message_id), newMessage);
                return ResponseEntity.status(HttpStatus.OK).body("1");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


}
