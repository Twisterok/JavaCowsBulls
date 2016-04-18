/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cowsbulls.src.network;

import com.cowsbulls.server.CBServer;
import static com.cowsbulls.server.CBServer.serverAPI;
import com.cowsbulls.server.Command;
import com.cowsbulls.src.utils.CallbackConstants;
import com.cowsbulls.src.utils.CowsBullsException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexander
 */
public class ClientThread extends Thread{
    private Client  client;
    
    public ClientThread(Socket socket) throws CowsBullsException
    {
        this.client = new Client(socket,this.getId());
        this.start();
    }
    
    public void SendMessage(Message msg) throws CowsBullsException
    {
        // не повезло...
        client.sendMessage(msg);
    }
    public Message ReadMessage(Message msg) throws CowsBullsException
    {
        // не повезло...
        return client.readMessage();
    }
    
    
    public void run()
    {
        Message networkMessage = new Message();
        networkMessage.setCommandNum(GeneralFunctions.GO_AHEAD);
        try {
            SendMessage(networkMessage);
        } 
        catch (CowsBullsException ex) 
        {
            System.out.println(ex.GetMessage());
            return;
        }
        
        while (true)
        {
            try { 
                
                ReadMessage(networkMessage);
                Command cmd = serverAPI.GetCommand(networkMessage.getCommandNum());
                if (cmd == null)
                {
                    networkMessage.getArgs().clear();
                    networkMessage.setCommandNum(ClientFunctions.RESPONSE);
                    networkMessage.setErrCode(CallbackConstants.COMMAND_NOT_FOUND);
                    networkMessage.setErrDescription("Invoked command doesnt exist");
                    SendMessage(networkMessage);
                }
                else
                {
                    Message returnkMessage = cmd.processMessage(networkMessage);
                    switch (networkMessage.getCommandNum())
                    {
                    case ServerFunctions.SIGN_IN:
                        String newLogin = networkMessage.getArgs().get(0).toString();
                        networkMessage = serverAPI.sign_in(networkMessage);
                        CBServer.clients.put( newLogin, client);
                        client.sendMessage(networkMessage);
                        break;
                    case ServerFunctions.SIGN_OUT:
                        CBServer.clients.remove(networkMessage.getArgs().get(0).toString());
                        break;
                    case ServerFunctions.CLIENT_LEFT:
                        CBServer.threads.remove(this.getId());
                        throw new CowsBullsException(CallbackConstants.CLIENT_LEFT,"Client has left");
                    }
                    SendMessage(returnkMessage);
                }
            } 
            catch (CowsBullsException ex) {
                if (ex.GetErrCode() == CallbackConstants.CLIENT_LEFT)
                {
                    try {
                        client.getSocket().close();
                        CBServer.threads.remove(this.getId());
                    } 
                    catch (IOException ex1) {
                        System.out.println(ex.GetMessage());
                    }
                }
                System.out.println(ex.GetMessage());
            }
        }
    }
}
