/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatgui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
/**
 *
 * @author bonito.marco
 */
public class ChatGUI extends JFrame implements ActionListener{

    private static DatagramSocket socket;
    private static String IP_address = "localhost";
    private static InetAddress address;
    private static int UDP_port;
    private String username;
    

    JPanel ContenutoInvio = new JPanel();
    
    JMenuBar menu = new JMenuBar();
    JMenu GestisciChat = new JMenu("Opzioni Chat");
    JMenuItem AggiungiUsername = new JMenuItem("Aggiungi Username");
    

    private static JTextArea areaChat = new JTextArea();
    JScrollPane scroll = new JScrollPane(areaChat); 
    JTextField messaggioField = new JTextField();
    JButton invia = new JButton("Invia");
    
    public ChatGUI()throws InterruptedException{
        
        this.setTitle("Chat di gruppo");
        this.setSize(400, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(new GridLayout(2,1));
        
        menu.add(GestisciChat);
        GestisciChat.add(AggiungiUsername);
        this.setJMenuBar(menu);
        
        ContenutoInvio.setLayout(new GridLayout(1,2));
        ContenutoInvio.add(messaggioField);
        ContenutoInvio.add(invia);
        areaChat.setEditable(false); 
        areaChat.setBorder(new EmptyBorder(20, 20, 20, 20));
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scroll);
        this.add(ContenutoInvio);

        invia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    inviaPacchetto(messaggioField.getText(),username);
                    messaggioField.setText("");
                
            }
        });
        
        
        AggiungiUsername.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               username = JOptionPane.showInputDialog("Inserisci il tuo username: ");
            }
        });
        
        Thread ascolta = new Thread() {
	public void run() {
                  riceviPacchetto();
            }
	};	
        ascolta.start(); 
        areaChat.append("Prima di iniziare, inserisci il tuo Username e l'indirizzo IP del Server.\n"
                + "Per farlo clicca sul menù ''Opzioni Chat'' qui in alto.\n"
                + "Il Server deve esserve avviato per poter usare la chat correttamente.\n"); 
    }
    
    public static void inviaPacchetto(String messaggio, String username){
        byte[] buffer;
        DatagramPacket userDatagram;

        try {
                messaggio = username.concat(": "+ messaggio); 

                buffer = messaggio.getBytes("UTF-8");

               
                userDatagram = new DatagramPacket(buffer, buffer.length, address, UDP_port);
                socket.send(userDatagram);
        } catch (IOException ex) {
            Logger.getLogger(ChatGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void riceviPacchetto(){
        byte[] buffer = new byte[100];
        String received;
        DatagramPacket serverDatagram;

        try {
            serverDatagram = new DatagramPacket(buffer, buffer.length);
            while (!Thread.interrupted()){
                socket.receive(serverDatagram); 
                received = new String(serverDatagram.getData(), 0, serverDatagram.getLength(), "ISO-8859-1");
                areaChat.append(received+"\n");
                areaChat.setCaretPosition(areaChat.getDocument().getLength());
            }
            socket.close();

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ChatGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChatGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException, SocketException {
         address = InetAddress.getByName(IP_address);
        UDP_port = 1077;
        
        socket = new DatagramSocket();
        
        Runnable r = new Runnable() {
             public void run() {
                 try { 
                     new ChatGUI().setVisible(true);
                 } catch (InterruptedException ex) {
                     Logger.getLogger(ChatGUI.class.getName()).log(Level.SEVERE, null, ex);
                 }
             }
         };
         EventQueue.invokeLater(r);
    }

    @Override
    public void actionPerformed(ActionEvent e) { 
    }
}
