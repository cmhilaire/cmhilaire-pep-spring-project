package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
public class AccountService {

    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    /**
     * ...
     * @param account a transient account
     * @return the persisted account
     */
    public Account persistAccount(Account account){
        return accountRepository.saveAndFlush(account);
    }

    /**
     * finaAll() will return all account entities stored in the DB table.
     * @return all Account entities.
     */
    public List<Account> getAllAccount(){
        return accountRepository.findAll();
    }

    /**
     * ...
     * @param id the id of the Account entity.
     * @return Account entity
     */
    public Account getAccountById(int id){
        Optional<Account> optionalAccount = accountRepository.findById(id);
        // if (optionalAccount.isEmpty()) {
        //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Account with id %d not found", id));
        // }
        if(optionalAccount.isPresent()){
            return optionalAccount.get();
        }else{
            return null;
        }
    }

    /**
     * Delete a account entity of a certain id.
     * @param id
     * @return
     */
    public void deleteAccount(int id){
        accountRepository.deleteById(id);
    }
    
    /**
     * Given a account ID, overwrite the contents of the account entity.
     * @param id
     * @return
     */
    public void updateAccount(int id, Account replacement){
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if(optionalAccount.isPresent()){
            Account account = optionalAccount.get();
            account.setUsername(replacement.getUsername());
            accountRepository.save(account);
        }

    }

    public Account getAccountByUsername(String username){
        Account selAccount = null;
        List<Account> accountList = this.getAllAccount();
        for(Account acc: accountList){
            if( acc.getUsername().equals(username) ){ selAccount = acc; break;}
        }
        return selAccount;
    }

    public void register(Account account){
        List<Account> allAccounts = getAllAccount();
        for(Account acc: allAccounts){
            if( acc.getUsername().equals(account.getUsername()) ){
                throw new DuplicateKeyException("Account already exists.");
            }
        }

        if(account.getUsername().length() > 3){
            this.persistAccount(account);
        }
    }

    public Account login(String username, String pwd){
        Account connectedAccount = null;

        Account retrieveAccount = this.getAccountByUsername(username);

        if(retrieveAccount != null){
            if(retrieveAccount.getUsername().equals(username) && retrieveAccount.getPassword().equals(pwd)){
                connectedAccount = retrieveAccount;
            }
        }
        
        return connectedAccount;
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUnauthorized(AuthenticationException ex){
        return ex.getMessage();
    }
}
