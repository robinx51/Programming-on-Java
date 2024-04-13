package com.robinx.lab6_server;

public class RecordException extends Exception {
    private Double number;
    
    public Double getNumber()
    {
        return number;
    }
    
    public RecordException(String message, Double num){
        super(message);
        number = num;
    }
    
}
