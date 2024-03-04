package com.robinx.lab2;
import java.util.LinkedList;

public class RecIntegral {
    LinkedList<Double> record;
    
    public RecIntegral()
    {
        this.record = new LinkedList<Double>();
    }
    public RecIntegral(double a, double b, double h)
    {
        this.record = new LinkedList<Double>();
        record.addFirst(a);
        record.add(1, b);
        record.addLast(h);
        
    }
    
    public void SetRec(double a, double b, double h)
    {
        record.addFirst(a);
        record.add(1, b);
        record.addLast(h);
    }
    
    public Double[] GetRec()
    {
        Double[] arr = {record.getFirst(), record.get(1), record.getLast()};
        return arr;
    }
}
