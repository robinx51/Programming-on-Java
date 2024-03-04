package com.robinx.lab3;
import com.robinx.lab3.RecordException;
import java.util.LinkedList;

public class RecIntegral {
    LinkedList<Double> record;
    
    public RecIntegral()
    {
        this.record = new LinkedList<>();
    }
    public RecIntegral(double a, double b, double h) throws RecordException
    {
        Check(a,b,h);
        this.record = new LinkedList<>();
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
    
    private void Check(double a, double b, double h) throws RecordException
    {
        if (a < 0.000001 || a > 1000000)
            throw new RecordException("The number is less than 0.000001 or more than 1000000", a);
        if (b < 0.000001 || b > 1000000)
            throw new RecordException("The number is less than 0.000001 or more than 1000000", b);
        if (h < 0.000001 || h > 1000000)
            throw new RecordException("The number is less than 0.000001 or more than 1000000", h);
    }
}
