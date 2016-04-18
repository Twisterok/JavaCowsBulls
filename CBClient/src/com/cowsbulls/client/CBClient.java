/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cowsbulls.client;

import com.cowsbulls.src.network.Message;
import com.cowsbulls.src.network.ServerFunctions;
import com.cowsbulls.src.objects.Player;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexander
 */
public class CBClient {

    /**
     * @param args the command line arguments
     */
    
    public void StartClient() throws IOException, ClassNotFoundException
    {
        InetAddress         addr        = InetAddress.getByName("127.0.0.1");
        final int           PORT        = 12345; 
        SocketAddress       sockaddr    = new InetSocketAddress(addr, PORT);
        Socket              socket      = new Socket();//("127.0.01", PORT);
        socket.connect(sockaddr,10000);

        ObjectOutputStream  oos         = new ObjectOutputStream(socket.getOutputStream());        
        ObjectInputStream   ois         = new ObjectInputStream(socket.getInputStream());
        
        Player              player      = new Player();
        
        player.setLogin("ASHOT_ONE_SHOT");
        player.setPassword("123qweASD");
        player.setName("ASHOT");
        player.setSurname("AGANESYAN");
        
        Message msg = null;
        
        msg = (Message) ois.readObject();
        //dont care.
        
        msg.setCommandNum(ServerFunctions.SIGN_UP);
        msg.getArgs().clear();
        msg.getArgs().add(player);
        
        oos.writeObject(msg);
        oos.flush();
        
        while (true)
        {
            msg = (Message) ois.readObject();  
            if (msg != null)
            {
                break;
            }
        }
    }
    
    public static void main(String[] args) {
       CBClient client = new CBClient();
        try {
            client.StartClient();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CBClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
