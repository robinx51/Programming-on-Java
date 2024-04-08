package com.robinx.lab4;
import java.io.*;
import java.util.LinkedList;
import javax.swing.JOptionPane;

public class Serialize implements Serializable {
    ObjectOutputStream out = null;
    public void Write(LinkedList<RecIntegral> list)
    {
        try {
        out = new ObjectOutputStream(new BufferedOutputStream(
            new FileOutputStream("Serialize.ser")));
        out.writeObject(list);
        } catch ( IOException ex ) {
            JOptionPane.showMessageDialog(null, ex.getMessage() );
        }
    }
    public LinkedList<RecIntegral> Read()
    {
        ObjectInputStream in = null;
        LinkedList<RecIntegral> list = null;
        try {
            in = new ObjectInputStream(new BufferedInputStream(
                new FileInputStream("Serialize.ser")));
            //list = (LinkedList<RecIntegral>)in.readObject();
        } catch ( IOException ex ) {
            JOptionPane.showMessageDialog(null, ex.getMessage() );
        }
        return list;
    }
}
