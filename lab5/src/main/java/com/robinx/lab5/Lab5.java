package com.robinx.lab5;

import javax.swing.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.LinkedList;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class Lab5 extends javax.swing.JFrame {
    LinkedList<RecIntegral> list = new LinkedList<>();
    
    public Lab5() {
        initComponents();
        
        int width = 536;
        int height = 305;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // Устанавливаем позицию окна по центру экрана
        int x = (screenSize.width - width) / 2;
        int y = (screenSize.height - height) / 2;
        this.setLocation(x, y);
    }
    
    private class MyThread implements Runnable {
        private int rows[] = null;
        
        public MyThread(int[] rows)
        {
            this.rows = rows;
        }
        
        @Override
        public void run()
        {
            for(int row : rows)
            {
                if (row != -1) {
                    Object s1 = BoundTable.getValueAt(row, 0);
                    Object s2 = BoundTable.getValueAt(row, 1);
                    Object s3 = BoundTable.getValueAt(row, 2);
            
                    if ( s1 != null & s2 != null & s3 != null)
                    {
                        Double a  = (Double) s1;
                        Double b  = (Double) s2;
                        Double h = (Double) s3;
                        double sum = 0;
                
                        for (double x = a; x <= b; x += h) {
                            double f = Math.sqrt(x) + Math.sqrt(x + h); // f(x) + f(x+h)
                            double area = f * (h /2);                   // 1/2( f(x) + f(x+h) )
                            sum += area;
                            if (x + h > b) 
                            {
                                double edge = Math.sqrt(x) + Math.sqrt(b);
                                double edgeArea = (b - x) * edge / 2;
                                sum += edgeArea;
                            }
                        }
                        TableModel model = BoundTable.getModel();
                        model.setValueAt(sum, row, 3);
                        JOptionPane.showMessageDialog(null, "Поток " + Thread.currentThread().getName() + " завершил работу с результатом: " + sum );
                    }
                else JOptionPane.showMessageDialog(null, "Один из параметров строки" + row + " не заполнен.");
                }
            }
        }
    }
    private class NamedThreadFactory implements ThreadFactory {
        private final String namePrefix;

        public NamedThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(namePrefix + "-" + t.getId());
            return t;
        }
}
    
    private boolean Check(double a, double b, double h) throws RecordException
    {
        if (a < 0.000001 || a > 1000000)
            throw new RecordException("The number is less than 0.000001 or more than 1000000", a);
        if (b < 0.000001 || b > 1000000)
            throw new RecordException("The number is less than 0.000001 or more than 1000000", b);
        if (h < 0.000001 || h > 1000000)
            throw new RecordException("The number is less than 0.000001 or more than 1000000", h);
        return true;
    }
    
    public void AddLines() throws RecordException
    {
        javax.swing.table.DefaultTableModel table = (javax.swing.table.DefaultTableModel) BoundTable.getModel();
        
        String s1 = LowerBoundTextField.getText();
        String s2 = HigherBoundTextField.getText();
        String s3 = StepTextField.getText();
        
        s1 = s1.replace(',','.');
        s2 = s2.replace(',','.');
        s3 = s3.replace(',','.');
        
        if (!s1.isEmpty() && !s2.isEmpty() && !s3.isEmpty()) {
            if (s1.matches("-?\\d+(\\.\\d+)?") && s2.matches("-?\\d+(\\.\\d+)?") && s3.matches("-?\\d+(\\.\\d+)?")) 
            {
                Double a = Double.valueOf(s1);
                Double b = Double.valueOf(s2);
                Double h = Double.valueOf(s3);
                
                if (a > b) throw new RecordException("Верхняя граница должна быть больше нижней", b);
                else if ( a + h >= b ) throw new RecordException("Шаг не должен превышать интервал интегрирования", h);
                //RecIntegral rec = new RecIntegral(a, b, h);
                //list.addLast(rec);
                
                Object[] array = {a, b, h};
                table.addRow(array);
                
                HigherBoundTextField.setText("");
                LowerBoundTextField.setText("");
                StepTextField.setText("");
                
                BoundTable.repaint();
            }
            else JOptionPane.showMessageDialog(null, "Один из параметров содержит строковые символы.");
        }
        else JOptionPane.showMessageDialog(null, "Один из параметров не заполнен.");
    }
    public void Calculate()
    {
        int[] tempRows = new int[BoundTable.getRowCount()];
        int count = 0;
        DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) BoundTable.getSelectionModel();
        for (int i = 0; i < BoundTable.getRowCount(); i++) {
            if (selectionModel.isSelectedIndex(i)) {
                tempRows[count++] = i;
            }
        }
        int[] rows = new int[count];
        System.arraycopy(tempRows, 0, rows, 0, count);
        
        if (count > 6)
        {
            JOptionPane.showMessageDialog(null, "Выберите до 6-ти строк для вычисления");
        }
        else 
        {
            ExecutorService executor = Executors.newFixedThreadPool(count > 6 ? 6 : count, new NamedThreadFactory("ВычислительныйПоток"));
            for (int i = 0; i < count; i++) {
                int [] row = new int[1];
                row[0] = rows[i];
                Runnable task = new MyThread(row);
                executor.execute(task);
            }
            executor.shutdown();
        }
    }
    public void DeleteLine()
    {
        int row = BoundTable.getSelectedRow();
        if (row != -1) {
            javax.swing.table.DefaultTableModel table = (javax.swing.table.DefaultTableModel) BoundTable.getModel();
            table.removeRow(row);
        }
        else JOptionPane.showMessageDialog(null, "Ни одна строка не выбрана.");
    }
    
    public void SetRecord(String s1, String s2, String s3, String s4) throws RecordException
    {
        Double a  = Double.valueOf(s1);
        Double b  = Double.valueOf(s2);
        Double h = Double.valueOf(s3);
        Double result = Double.valueOf(s4);
        
        if (Check(a,b,h) == false) return;
        
        RecIntegral rec;
        if (result == null)
        {   rec = new RecIntegral(a, b, h, 0);   }
        else {  rec = new RecIntegral(a, b, h, result); }
        list.addLast(rec);
    }
    public void AddMembersToTable()
    {
        if (list.isEmpty()) 
        {   
            JOptionPane.showMessageDialog(null, "Контейнер пуст");
            return;
        }
        DefaultTableModel table = (DefaultTableModel) BoundTable.getModel();
        for (RecIntegral item : list)
        {
            Double[] arr = item.GetRec();
            table.addRow(arr);
        }
    }
    public void AddMembersToList() throws RecordException
    {
        if (BoundTable.getRowCount() == 0)
        {
            JOptionPane.showMessageDialog(null, "Таблица пуста");
            return;
        }
        for (int i = 0; i < BoundTable.getRowCount(); i++)
        {
            Object s1 = BoundTable.getValueAt(i, 0);
            Object s2 = BoundTable.getValueAt(i, 1);
            Object s3 = BoundTable.getValueAt(i, 2);
            Object s4 = BoundTable.getValueAt(i, 3);
            
            if ( s1 != null & s2 != null & s3 != null)
            {
                Double a  = (Double) s1;
                Double b  = (Double) s2;
                Double h = (Double) s3;
                Double result = (Double) s4;
                
                RecIntegral rec;
                if (result == null)
                {   rec = new RecIntegral(a, b, h, 0);   }
                else {  rec = new RecIntegral(a, b, h, result); }
                list.addLast(rec);
            }
            else JOptionPane.showMessageDialog(null, "Один из параметров не заполнен.");
        }
    }
    
    public void Clear()
    {
        if ("Таблица".equals((String)TableOrListComboBox.getSelectedItem()))
        {
            DefaultTableModel model = (DefaultTableModel) BoundTable.getModel();
            model.setRowCount(0);
        }
        else list.clear();
    }
    public void Fill() throws RecordException
    {
        if ("Таблица".equals((String)TableOrListComboBox.getSelectedItem()))
        {
            AddMembersToTable();
        }
        else AddMembersToList();
    }
    
    public void SaveToFile(char param)
    {
        if (list.isEmpty()) 
        {   
            JOptionPane.showMessageDialog(null, "Контейнер пуст");
            return;
        }
        String path = "";
        {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(fileChooser);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                path = fileChooser.getSelectedFile().getPath();
            }
        }
        if (param == 't')
        {
            String text = null;
            for (RecIntegral item : list)
            {
                String[] str = {"", "", "", ""};
                
                for (int i = 0; i < 4; i++)
                {
                    str[i] = String.valueOf(item.GetRec()[i]);
                }
                text = String.join(" ", str);
            }
            byte[] buffer;
            StringBuilder sb = new StringBuilder();
            sb.append(text).append(System.lineSeparator());
                
            buffer = sb.toString().getBytes();
            try(OutputStream writer = new FileOutputStream(path, false))
            {
                writer.write(buffer);
                writer.close();
            }
            catch(IOException ex){
                JOptionPane.showMessageDialog(null, "Ошибка создания файла.");
            }
        }
        else
        {
            try {
                ObjectOutputStream out = new ObjectOutputStream( new BufferedOutputStream(
                new FileOutputStream(path)));
                out.writeObject(list);
                out.flush();
                out.close();
            } catch ( IOException ex ) {
                JOptionPane.showMessageDialog(null, ex.getMessage() );
            }
        }
        JOptionPane.showMessageDialog(null, "Запись успешно занасена.");
    }
    public void LoadFromFile(char param)
    {
        list.clear();
        String path = "";
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(fileChooser);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            path = fileChooser.getSelectedFile().getPath();
        }
        if (param == 't')
        {
            try 
            {
                FileReader fr = new FileReader(path);
                BufferedReader reader = new BufferedReader(fr);
                // считаем сначала первую строку
                String line = reader.readLine();
                while (line != null) {
                    String[] array = line.split(" ", 0);
                    
                    try {SetRecord(array[0], array[1], array[2], array[3]); }
                    catch (RecordException ex) {
                        String message = ex.getMessage() + " (" + ex.getNumber() + ')';
                        JOptionPane.showMessageDialog(null, message);
                    }
                    // считываем остальные строки в цикле
                    line = reader.readLine();
                }
                JOptionPane.showMessageDialog(null, "Успешно считано", "Ура", JOptionPane.INFORMATION_MESSAGE);
            } 
            catch (FileNotFoundException e) { JOptionPane.showMessageDialog(null, "Файл для чтения не существует"); } 
            catch (IOException e) { JOptionPane.showMessageDialog(null, "Ошибка чтения файла"); }
        }
        else 
        {
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new FileInputStream(path));
                Class<?> recIntegralClass = Class.forName("com.robinx.lab4.RecIntegral");
                list = (LinkedList<RecIntegral>) in.readObject();
                in.close();
                JOptionPane.showMessageDialog(null, "Успешно считано", "Ура", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }
        
    public void Save() {
        if ("Текстовый".equals((String)FormatFileComboBox.getSelectedItem()))
        {
            SaveToFile('t');
        }
        else SaveToFile('b');
    }
    public void Load() {
        if ("Текстовый".equals((String)FormatFileComboBox.getSelectedItem()))
        {
            LoadFromFile('t');
        }
        else LoadFromFile('b');
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new Lab5().setVisible(true);
        });
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        BoundTable = new javax.swing.JTable();
        AddButton = new javax.swing.JButton();
        DeleteButton = new javax.swing.JButton();
        CalculateButton = new javax.swing.JButton();
        LowerBoundTextField = new javax.swing.JTextField();
        HigherBoundTextField = new javax.swing.JTextField();
        StepTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ClearButton = new javax.swing.JButton();
        AddMembersButton = new javax.swing.JButton();
        TableOrListComboBox = new javax.swing.JComboBox<>();
        FormatFileComboBox = new javax.swing.JComboBox<>();
        SaveToFileButton = new javax.swing.JButton();
        LoadFromFileButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Таблица для вычисления интеграла функции √x");
        setBackground(new java.awt.Color(204, 204, 204));
        setForeground(java.awt.Color.darkGray);
        setLocation(new java.awt.Point(0, 0));

        BoundTable.setForeground(new java.awt.Color(153, 153, 153));
        BoundTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {0.001, 1.0, 0.001, null},
                {0.001, 1.0, 0.01, null},
                {0.01, 1.0, 0.001, null},
                {0.01, 1.0, 0.01, null},
                {0.1, 1.0, 0.001, null},
                {0.1, 1.0, 0.01, null}
            },
            new String [] {
                "Нижняя граница", "Верхняя граница", "Шаг интегрирования", "Результат"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        BoundTable.setName(""); // NOI18N
        jScrollPane1.setViewportView(BoundTable);

        AddButton.setText("Добавить");
        AddButton.setToolTipText("Добавить значения текстовых полей в таблицу");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddButtonActionPerformed(evt);
            }
        });

        DeleteButton.setText("Удалить");
        DeleteButton.setToolTipText("Выделите удаляемую из таблицы строку");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });

        CalculateButton.setText("Вычислить");
        CalculateButton.setToolTipText("Выберите до 6-ти строк, чтобы провести вычисление");
        CalculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CalculateButtonActionPerformed(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Шаг интегрировния");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Нижняя граница");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Верхняя граница");

        ClearButton.setText("Очистить");
        ClearButton.setToolTipText("Очистить таблицу или контейнер");
        ClearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearButtonActionPerformed(evt);
            }
        });

        AddMembersButton.setText("Заполнить");
        AddMembersButton.setToolTipText("Таблица: заполнить таблицу элементами из контейнера. \nКонтейнер: заполнить контейнер элементами из таблицы");
        AddMembersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddMembersButtonActionPerformed(evt);
            }
        });

        TableOrListComboBox.setMaximumRowCount(2);
        TableOrListComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Контейнер", "Таблица" }));
        TableOrListComboBox.setToolTipText("Выберите элемент, с которым хотите произвести действия справа");

        FormatFileComboBox.setMaximumRowCount(2);
        FormatFileComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Текстовый", "Двоичный" }));
        FormatFileComboBox.setToolTipText("Выберите формат сохранения или загрузки данных");

        SaveToFileButton.setText("Сохранить");
        SaveToFileButton.setToolTipText("Записать данные в файл");
        SaveToFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveToFileButtonActionPerformed(evt);
            }
        });

        LoadFromFileButton.setText("Загрузить");
        LoadFromFileButton.setToolTipText("Загрузить данные из файла");
        LoadFromFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadFromFileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(LowerBoundTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(TableOrListComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(AddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(FormatFileComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(CalculateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ClearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(SaveToFileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(53, 53, 53)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(AddMembersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(LoadFromFileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                    .addComponent(HigherBoundTextField))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(StepTextField)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LowerBoundTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HigherBoundTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(StepTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddButton)
                    .addComponent(CalculateButton)
                    .addComponent(DeleteButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TableOrListComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ClearButton)
                    .addComponent(AddMembersButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FormatFileComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SaveToFileButton)
                    .addComponent(LoadFromFileButton))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        FormatFileComboBox.getAccessibleContext().setAccessibleDescription("Выберите формат сохранения данных из контейнера или загрузки в него");
        SaveToFileButton.getAccessibleContext().setAccessibleDescription("Записать данные из контейнера в файл");
        LoadFromFileButton.getAccessibleContext().setAccessibleDescription("Загрузить данные из файла в контейнер");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
        try{
            AddLines();
        }
        catch(RecordException ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }//GEN-LAST:event_AddButtonActionPerformed

    private void CalculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CalculateButtonActionPerformed
        Calculate();
    }//GEN-LAST:event_CalculateButtonActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        DeleteLine();
    }//GEN-LAST:event_DeleteButtonActionPerformed

    private void ClearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearButtonActionPerformed
        Clear();
    }//GEN-LAST:event_ClearButtonActionPerformed

    private void AddMembersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddMembersButtonActionPerformed
        try {
            Fill();
        } catch (RecordException ex) {
            String message = ex.getMessage() + " (" + ex.getNumber() + ')';
            JOptionPane.showMessageDialog(null, message);
        }
    }//GEN-LAST:event_AddMembersButtonActionPerformed

    private void SaveToFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveToFileButtonActionPerformed
        Save();
    }//GEN-LAST:event_SaveToFileButtonActionPerformed

    private void LoadFromFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadFromFileButtonActionPerformed
        Load();
    }//GEN-LAST:event_LoadFromFileButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddButton;
    private javax.swing.JButton AddMembersButton;
    private javax.swing.JTable BoundTable;
    private javax.swing.JButton CalculateButton;
    private javax.swing.JButton ClearButton;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JComboBox<String> FormatFileComboBox;
    private javax.swing.JTextField HigherBoundTextField;
    private javax.swing.JButton LoadFromFileButton;
    private javax.swing.JTextField LowerBoundTextField;
    private javax.swing.JButton SaveToFileButton;
    private javax.swing.JTextField StepTextField;
    private javax.swing.JComboBox<String> TableOrListComboBox;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}
