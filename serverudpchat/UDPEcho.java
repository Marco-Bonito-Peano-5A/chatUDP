/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverudpclient;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
class Clients {
    InetAddress addr;
    int port;
    public Clients(InetAddress addr, int port) {
        this.addr = addr;
        this.port = port;
    }
}
public class UDPEcho implements Runnable {
    private DatagramSocket socket;
    Clients client = new Clients(InetAddress.getByName("0.0.0.0"), 0);
    public UDPEcho(int port) throws SocketException, UnknownHostException {
    socket = new DatagramSocket(port);
    
    }
    
    public void run() {
        LinkedList<String> ultimiMessaggi = new LinkedList<String>();

        DatagramPacket answer; 
        byte[] buffer = new byte[8192];
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        HashMap<String, Clients> clients = new HashMap<String, Clients>();
        String clientID;
        String message;
        while (!Thread.interrupted()){
            try {
                socket.receive(request); 
                client.addr = request.getAddress();
                client.port = request.getPort(); 
                clientID = client.addr.getHostAddress() + client.port;
                System.out.println(clientID);
                if(clients.get(clientID) == null) {
                    clients.put(clientID, new Clients(client.addr, client.port));
                    for(int i=0; i<ultimiMessaggi.size();i++) {
                    	answer = new DatagramPacket(ultimiMessaggi.get(i).getBytes(), ultimiMessaggi.get(i).getBytes().length, client.addr, client.port);
                        socket.send(answer);
                    }
                }
                System.out.println(clients);
                message = new String(request.getData(), 0, request.getLength(), "ISO-8859-1");
                if(message == "quit") {
                    clients.remove(clientID);
                }
                
                if(ultimiMessaggi.size()<10)
                	ultimiMessaggi.add(message);
                else {
                	ultimiMessaggi.removeLast();
                	ultimiMessaggi.addFirst(message);
                }

                for(Clients clnt: clients.values()) {
                    answer = new DatagramPacket(request.getData(), request.getLength(), clnt.addr, clnt.port);
                    socket.send(answer);
                }
            } catch (IOException ex) {
                Logger.getLogger(UDPEcho.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
