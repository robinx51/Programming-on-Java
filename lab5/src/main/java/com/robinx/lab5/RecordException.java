package com.robinx.lab5;

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
