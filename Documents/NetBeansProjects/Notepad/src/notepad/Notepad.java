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
import java.awt.event.KeyEvent;
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
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Sourav
 */
public final class Notepad extends JFrame implements ActionListener{

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
    ArrayList<String> screentext=new ArrayList<String>();
    String copytext=null;
        
    public Notepad() throws IOException
    {
        Toolkit tk=Toolkit.getDefaultToolkit();
        Dimension dm=tk.getScreenSize();
        setLocation(dm.width/16, dm.height/16);
        setSize((7*dm.width)/8,(7*dm.height)/8);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                {
                    int ans=0;
                    ArrayList<String> text=new ArrayList<>(Arrays.asList((String[])npad.getText().split("\\r?\\n")));
                    if (!screentext.equals(text))
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
                    screentext=new ArrayList<>(Arrays.asList((String[])npad.getText().split("\\r?\\n")));
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
        
        JMenu edit=new JMenu("EDIT");
        edit.setMnemonic('E');
        
        JMenuItem cut=new JMenuItem("CUT");
        edit.add(cut);
        cut.addActionListener(this);
        cut.setActionCommand("CUT");
        cut.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        
        JMenuItem copy=new JMenuItem("COPY");
        edit.add(copy);
        copy.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        copy.setActionCommand("COPY");
        copy.addActionListener(this);
        
        
        JMenuItem paste=new JMenuItem("PASTE");
        edit.add(paste);
        paste.setAccelerator(KeyStroke.getKeyStroke('V', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        paste.setActionCommand("PASTE");
        paste.addActionListener(this);
        
        JMenuItem delete=new JMenuItem("DELETE");
        edit.add(delete);
        delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        delete.setActionCommand("DELETE");
        delete.addActionListener(this);
        
        JMenuItem find=new JMenuItem("FIND");
        edit.add(find);
        find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,KeyEvent.CTRL_MASK));
        find.setActionCommand("FIND");
        find.addActionListener(this);
        
        JMenuItem replace=new JMenuItem("REPLACE");
        edit.add(replace);
        replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,KeyEvent.CTRL_MASK));
        replace.setActionCommand("REPLACE");
        replace.addActionListener(this);
        
        JMenuItem selectall=new JMenuItem("SELECT ALL");
        edit.add(selectall);
        selectall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,KeyEvent.CTRL_MASK));
        selectall.setActionCommand("SELECT ALL");
        selectall.addActionListener(this);
        
        menubar.add(edit);
        
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
                    screentext=new ArrayList<>(Arrays.asList((String[])npad.getText().split("\\r?\\n")));
                    try {
                    changeFile(FileOpenAddress,screentext);
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
            ArrayList<String> text=new ArrayList<>(Arrays.asList((String[])npad.getText().split("\\r?\\n")));
                    
            if (!screentext.equals(text))
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
            screentext=new ArrayList<>(Arrays.asList((String[])npad.getText().split("\\r?\\n")));
        }
        if ("NEW".equals(e.getActionCommand()))
        {
            int ans=0;
            ArrayList<String> text=new ArrayList<>(Arrays.asList((String[])npad.getText().split("\\r?\\n")));
            if (!screentext.equals(text))
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
            screentext=new ArrayList<>(Arrays.asList((String[])npad.getText().split("\\r?\\n")));
        }
        if ("EXIT".equals(e.getActionCommand()))
        {
            int ans=0;
            ArrayList<String> text=new ArrayList<>(Arrays.asList((String[])npad.getText().split("\\r?\\n")));
            if (!screentext.equals(text))
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
            screentext=new ArrayList<>(Arrays.asList((String[])npad.getText().split("\\r?\\n")));
            if (ans==0)
                System.exit(0);
        }
        if ("CUT".equals(e.getActionCommand()))
        {
            copytext=npad.getSelectedText();
            npad.setText(npad.getText().replace(npad.getSelectedText(),""));
        }
        if ("COPY".equals(e.getActionCommand()))
        {
            copytext=npad.getSelectedText();
            
        }
        if ("PASTE".equals(e.getActionCommand()))
        {
            npad.insert(copytext, npad.getCaretPosition());
        }
        if ("DELETE".equals(e.getActionCommand()))
        {
            npad.setText(npad.getText().replace(npad.getSelectedText(),""));
        }
        if ("FIND".equals(e.getActionCommand()))
        {
            String text=JOptionPane.showInputDialog("Enter the string", null);
            Highlighter hg=npad.getHighlighter();
            HighlightPainter painter=new DefaultHighlighter.DefaultHighlightPainter(Color.BLUE);
            int index=0,start;
            String x=npad.getText();
            for (int i=0;i<npad.getText().length();i++)
            {
                if ((start=x.indexOf(text, i))!=0)
                    try {
                        hg.addHighlight(start, start+text.length() , DefaultHighlighter.DefaultPainter);
                } catch (BadLocationException ex) {
                    Logger.getLogger(Notepad.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }    
        if ("REPLACE".equals(e.getActionCommand()))
        {
            JTextField find=new JTextField();
            JTextField replace=new JTextField();
            Object[] fandr={"Word to Search",find,"Word to Replace"};
            String text=JOptionPane.showInputDialog(fandr);
            Highlighter hg=npad.getHighlighter();
            int start=npad.getText().indexOf(find.getText());
            npad.replaceRange(text, start, start+find.getText().length());
            
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
