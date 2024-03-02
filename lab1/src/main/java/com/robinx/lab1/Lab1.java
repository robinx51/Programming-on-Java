package com.robinx.lab1;
import javax.swing.table.TableModel;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Toolkit;

public class Lab1 extends javax.swing.JFrame {
 public Lab1() {
        initComponents();
        
        int width = 536;
        int height = 305;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // Устанавливаем позицию окна по центру экрана
        int x = (screenSize.width - width) / 2;
        int y = (screenSize.height - height) / 2;
        this.setLocation(x, y);
    }
 
    public void AddLines()
    {
        javax.swing.table.DefaultTableModel table = (javax.swing.table.DefaultTableModel) BoundTable.getModel();
        
        String s1 = LowerBoundTextField.getText();
        String s2 = HigherBoundTextField.getText();
        String s3 = StepTextField.getText();
        
        if (!s1.isEmpty() && !s2.isEmpty() && !s3.isEmpty()) {
            if (s1.matches("-?\\d+(\\.\\d+)?") && s2.matches("-?\\d+(\\.\\d+)?") && s3.matches("-?\\d+(\\.\\d+)?")) 
            {
                Double f1 = Double.valueOf(s1);
                Double f2 = Double.valueOf(s2);
                Double f3 = Double.valueOf(s3);
                
                Object[] array = {f1, f2, f3};
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
        int row = BoundTable.getSelectedRow();
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
            }
            else JOptionPane.showMessageDialog(null, "Один из параметров не заполнен.");
        }
        else JOptionPane.showMessageDialog(null, "Ни одна строка не выбрана.");
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
    
   
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Lab1().setVisible(true);
            }
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Таблица для вычисления интеграла функции \\/¯x");
        setBackground(new java.awt.Color(204, 204, 204));
        setForeground(java.awt.Color.darkGray);
        setLocation(new java.awt.Point(0, 0));

        BoundTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {0.0, 1.0, 0.001, null}
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
        BoundTable.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                BoundTableComponentAdded(evt);
            }
        });
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
        CalculateButton.setToolTipText("Выделите строку в таблице, чтобы провести вычисление");
        CalculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CalculateButtonActionPerformed(evt);
            }
        });

        LowerBoundTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LowerBoundTextFieldActionPerformed(evt);
            }
        });

        HigherBoundTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HigherBoundTextFieldActionPerformed(evt);
            }
        });

        StepTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StepTextFieldActionPerformed(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Шаг интегрировния");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Нижняя граница");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Верхняя граница");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(LowerBoundTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(AddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(17, 17, 17)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(HigherBoundTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(StepTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(CalculateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                                .addComponent(DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(23, 23, 23)))))
                .addContainerGap())
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
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void LowerBoundTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LowerBoundTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_LowerBoundTextFieldActionPerformed

    private void HigherBoundTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HigherBoundTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_HigherBoundTextFieldActionPerformed

    private void StepTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StepTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_StepTextFieldActionPerformed

    private void BoundTableComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_BoundTableComponentAdded
        
    }//GEN-LAST:event_BoundTableComponentAdded

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
        AddLines();
    }//GEN-LAST:event_AddButtonActionPerformed

    private void CalculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CalculateButtonActionPerformed
        Calculate();
    }//GEN-LAST:event_CalculateButtonActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        DeleteLine();
    }//GEN-LAST:event_DeleteButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddButton;
    private javax.swing.JTable BoundTable;
    private javax.swing.JButton CalculateButton;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JTextField HigherBoundTextField;
    private javax.swing.JTextField LowerBoundTextField;
    private javax.swing.JTextField StepTextField;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
