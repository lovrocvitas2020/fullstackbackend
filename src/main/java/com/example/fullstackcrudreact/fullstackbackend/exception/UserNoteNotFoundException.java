package com.example.fullstackcrudreact.fullstackbackend.exception;

public class UserNoteNotFoundException extends RuntimeException {
    public UserNoteNotFoundException(Long id){
        super("Could not found the user_note with id "+ id);
    }
}
