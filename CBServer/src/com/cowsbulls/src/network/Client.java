/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cowsbulls.src.network;

import com.cowsbulls.src.utils.CallbackConstants;
import com.cowsbulls.src.utils.CowsBullsException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexander
 */
public class Client {
    private Socket              socket;
    private ObjectInputStream   ois;
    private ObjectOutputStream  oos;
    private Long                threadId;
    public Client(Socket _socket,Long _threadId) throws CowsBullsException
    {
        this.threadId   = _threadId;
        this.socket     = _socket;
        try {
            ois   = new ObjectInputStream(this.socket.getInputStream());
            oos   = new ObjectOutputStream(this.socket.getOutputStream());
        } 
        catch (IOException ex) 
        {
            throw new CowsBullsException(CallbackConstants.NETWORK_ERROR, "Ошибка создания потоков ввода/вывода для работы с сокетом.");
        }
    }

    /**
     * @return the socket
     */
    public synchronized Socket getSocket() {
        return socket;
    }

    /**
     * @param socket the socket to set
     */
    public synchronized void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * @return the ois
     */
    public synchronized ObjectInputStream getOis() {
        return ois;
    }

    /**
     * @param ois the ois to set
     */
    public synchronized void setOis(ObjectInputStream ois) {
        this.ois = ois;
    }

    /**
     * @return the oos
     */
    public synchronized ObjectOutputStream getOos() {
        return oos;
    }

    /**
     * @param oos the oos to set
     */
    public synchronized void setOos(ObjectOutputStream oos) {
        this.oos = oos;
    }
    
    public synchronized void sendMessage(Message msg) throws CowsBullsException
    {
        try {
            this.getOos().writeObject(msg);
            this.getOos().flush();
        } catch (IOException ex) {
            throw new CowsBullsException(CallbackConstants.NETWORK_ERROR, "Error sending message to client");
        }
    }
   
    public synchronized Message readMessage() throws CowsBullsException
    {
        Message msg = null;
        try {
            msg = (Message) this.getOis().readObject();
        } catch (IOException ex) {
            throw new CowsBullsException(CallbackConstants.NETWORK_ERROR, "Error reading message from client");
        } catch (ClassNotFoundException ex) {
            throw new CowsBullsException(CallbackConstants.NETWORK_ERROR, "Error reading message from client");
        }
        return msg;
    }    
}
