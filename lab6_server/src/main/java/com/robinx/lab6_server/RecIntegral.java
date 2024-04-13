package com.robinx.lab6_server;
import java.io.Serializable;

public class RecIntegral implements Serializable {
    private double a, b, h, result;
    
    public RecIntegral()
    {
        a = 0;
        b = 0;
        h = 0;
        result = 0;
    }
    public RecIntegral(double a, double b, double h, double result) throws RecordException
    {
        this.a = a;
        this.b = b;
        this.h = h;
        this.result = result;
    }
    
    public void SetRec(double a, double b, double h, double result)
    {
        this.a = a;
        this.b = b;
        this.h = h;
        this.result = result;
    }
    
    public Double[] GetRec()
    {
        Double[] arr = {a, b, h, result};
        return arr;
    }
}
