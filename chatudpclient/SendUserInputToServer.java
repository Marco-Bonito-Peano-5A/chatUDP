package chatudpclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendUserInputToServer implements Runnable {

    DatagramSocket socket;
    InetAddress address;
    int UDP_port;

    SendUserInputToServer(DatagramSocket socket, InetAddress address, int UDP_port) {

        this.socket = socket;
        this.address = address;
        this.UDP_port = UDP_port;
    }

    /**
     *
     *
     *
     */
    @Override

    public void run() {
        byte[] buffer;
        String messaggio, username;
        Scanner tastiera = new Scanner(System.in);
        DatagramPacket userDatagram;

        try {
            System.out.print("Enter Username");
            username = tastiera.nextLine();
            System.out.print("> ");

            do {
                messaggio = tastiera.nextLine();
                messaggio = username.concat(": " + messaggio);
                buffer = messaggio.getBytes("UTF-8");
                userDatagram = new DatagramPacket(buffer, buffer.length, address, UDP_port);
                socket.send(userDatagram);
            } while (messaggio.compareTo("quit") != 0);
        } catch (IOException ex) {
            Logger.getLogger(ChatUDPclient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
