/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepad;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javax.accessibility.Accessible;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.*;//FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

/**
 *
 * @author Sourav
 */
public class Notepad extends JFrame implements ActionListener{

    /**
     * @param args the command line arguments
     */
    JMenuBar menubar;
    JTextArea npad;
    JScrollPane scroll;
    JFileChooser chooser;
    boolean isFileOpen;
    String FileOpenAddress;
    boolean isSaved=false;
        
    public Notepad() throws IOException
    {
        Toolkit tk=Toolkit.getDefaultToolkit();
        Dimension dm=tk.getScreenSize();
        setLocation(dm.width/16, dm.height/16);
        setSize((7*dm.width)/8,(7*dm.height)/8);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                {
                    int ans=0;
                    if (!isSaved)
                    {
                        if (isFileOpen)
                            ans=confirmToSave();
                        else {
                            int option=JOptionPane.showConfirmDialog(null, "Do you want to save this file?", "Really Save?", JOptionPane.YES_NO_CANCEL_OPTION);
                            if (option==JOptionPane.OK_OPTION)
                                saveAs();
                            if (option==JOptionPane.CANCEL_OPTION)
                                ans=1;
                            
                            }    
                    }
                    if (ans==0)
                        System.exit(0);
                }        
            }
        });
        setupTextArea();
        add(scroll,BorderLayout.CENTER);
        
        setupMenuBar();
        add(menubar,BorderLayout.NORTH);
        
    }
    
    void setupTextArea()
    {
        npad=new JTextArea();
        scroll=new JScrollPane(npad);
        
    }
    
    void setupMenuBar()
    {
        menubar=new JMenuBar();
        chooser=new JFileChooser();
        
        JMenu file=new JMenu("FILE");
        file.setMnemonic('F');
        
        JMenuItem newoption=new JMenuItem("NEW");
        newoption.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        newoption.addActionListener(this);
        file.add(newoption);
        
        JMenuItem open=new JMenuItem("OPEN");
        open.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        open.addActionListener(this);
        file.add(open);
        
        JMenuItem save=new JMenuItem("SAVE");
        save.setActionCommand("SAVE");
        save.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        save.addActionListener(this);
        file.add(save);
        
        JMenuItem saveas=new JMenuItem("SAVE AS");
        saveas.setActionCommand("SAVE AS");
        saveas.addActionListener(this);
        file.add(saveas);
        
        JMenuItem exit=new JMenuItem("EXIT");
        exit.setActionCommand("EXIT");
        exit.addActionListener(this);
        file.add(exit);
        
        menubar.add(file);
    }
    
    ArrayList<String> readFile(String filename) throws FileNotFoundException, IOException
    {
        
        ArrayList<String> result=new ArrayList<String>();
        
        BufferedReader reader=new BufferedReader(new FileReader(filename));
        String line;
        while ((line=reader.readLine())!=null)
        {
            result.add(line);
        }
        return result;
    }
    
    void changeFile(String address,ArrayList<String> text) throws IOException
    {
        
        BufferedWriter writer=new BufferedWriter(new FileWriter(address));
        for (int i=0;i<text.size();i++)
        {
            writer.write(text.get(i));
            writer.newLine();
        }
        writer.close();
        
    }
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        Notepad npd=new Notepad();
        npd.setVisible(true);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("SAVE AS".equals(e.getActionCommand()) || "SAVE".equals(e.getActionCommand()))
        {
            if ("SAVE".equals(e.getActionCommand()) && isFileOpen)
                {
                    ArrayList<String> put=new ArrayList<>(Arrays.asList((String[])npad.getText().split("\\r?\\n")));
                    try {
                    changeFile(FileOpenAddress,put);
                    } catch (IOException ex) {
                        Logger.getLogger(Notepad.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            else 
            {
                saveAs();
            }
            isSaved=true;
            throw new UnsupportedOperationException("Not supported yet.");
            
        }
        if ("OPEN".equals(e.getActionCommand()))
        {
            int ans=0;
            if (!isSaved)
            {
                if (isFileOpen)
                    ans=confirmToSave();
                else {
                    int option=JOptionPane.showConfirmDialog(this, "Do you want to save this file?", "Really Save?", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (option==JOptionPane.OK_OPTION)
                        saveAs();
                    if (option==JOptionPane.CANCEL_OPTION)
                        ans=1;
                    }
            }
            if (ans==0)
            {    
            chooser.setFileFilter(new FileNameExtensionFilter("TXT files","txt"));
            int val=chooser.showOpenDialog(null);
            if (val==JFileChooser.APPROVE_OPTION) {
                isFileOpen=true;
                File file1 = chooser.getSelectedFile();
                String filename = file1.getAbsolutePath();
                if (!file1.exists())
                {
                    filename=filename+".txt";
                    File createdFile=new File(filename);
                }
                FileOpenAddress=filename;
                ArrayList<String> result=new ArrayList<>();
                try {
                    result = readFile(filename);
                } catch (IOException ex) {
                    Logger.getLogger(Notepad.class.getName()).log(Level.SEVERE, null, ex); 
                }
                npad.setText(null);
                for (int i=0;i<result.size();i++)
                    npad.append(result.get(i)+"\n");
                throw new UnsupportedOperationException("Not supported yet.");
            }
            isSaved=false;
            }
        }
        if ("NEW".equals(e.getActionCommand()))
        {
            int ans=0;
            if (!isSaved)
            {
                if (isFileOpen)
                    ans=confirmToSave();
                else {
                    int option=JOptionPane.showConfirmDialog(this, "Do you want to save this file?", "Really Save?", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (option==JOptionPane.OK_OPTION)
                        saveAs();
                    if (option==JOptionPane.CANCEL_OPTION)
                        ans=1;
                    }
            }
            if (ans==0)
            {
                npad.setText(null);
                isSaved=false;
            }
            
        }
        if ("EXIT".equals(e.getActionCommand()))
        {
            int ans=0;
            if (!isSaved)
            {
                if (isFileOpen)
                    ans=confirmToSave();
                else {
                    int option=JOptionPane.showConfirmDialog(this, "Do you want to save this file?", "Really Save?", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (option==JOptionPane.OK_OPTION)
                        saveAs();
                    if (option==JOptionPane.CANCEL_OPTION)
                        ans=1;
                    
                    }
            }
            if (ans==0)
                System.exit(0);
        }
    }

    int confirmToSave()
    {
        int option=JOptionPane.showConfirmDialog(this, "Do you want to save this file?", "Really Save?", JOptionPane.YES_NO_CANCEL_OPTION);
                if (option==JOptionPane.OK_OPTION)
                {
                    ArrayList<String> put=new ArrayList<>(Arrays.asList((String[])npad.getText().split("\\r?\\n")));
                    try {
                    changeFile(FileOpenAddress,put);
                    } catch (IOException ex) {
                        Logger.getLogger(Notepad.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    isSaved=true;
                }
                if (option==JOptionPane.CANCEL_OPTION)
                    return 1;
        return 0;
    }
    
    void saveAs()
    {
        chooser.setFileFilter(new FileNameExtensionFilter("TXT files","txt"));
            int val=chooser.showSaveDialog(null);
            if (val==JFileChooser.APPROVE_OPTION) {
                File file1 = chooser.getSelectedFile();
                String address = file1.getAbsolutePath();
                if (!file1.exists())
                {
                    address=address+".txt";
                    File createdFile=new File(address);
                    
                }
                String list[] = npad.getText().split("\\r?\\n");
                ArrayList<String>put = new ArrayList<>(Arrays.asList(list)) ;
                try {
                    changeFile(address,put);
                } catch (IOException ex) {
                    Logger.getLogger(Notepad.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        isSaved=true;
    }
}
