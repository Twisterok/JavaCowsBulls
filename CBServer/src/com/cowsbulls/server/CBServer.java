/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cowsbulls.server;

import com.cowsbulls.src.database.CowsBullsHibernateUtil;
import com.cowsbulls.src.database.implementation.GameDAO;
import com.cowsbulls.src.database.implementation.PlayerDAO;
import com.cowsbulls.src.network.Client;
import com.cowsbulls.src.network.ClientThread;
import com.cowsbulls.src.network.Message;
import com.cowsbulls.src.network.ServerFunctions;
import com.cowsbulls.src.objects.Game;
import com.cowsbulls.src.objects.Player;
import com.cowsbulls.src.utils.CallbackConstants;
import com.cowsbulls.src.utils.CowsBullsException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.cowsbulls.src.network.Message;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author Alexander
 */
public class CBServer {
    
    public static   final int                   PORT            = 12345;
    public static   SessionFactory              factory         = CowsBullsHibernateUtil.getSessionFactory();
    public static   Map<String, Client>         clients         = new HashMap<String, Client>();
    public static   Map<Long, ClientThread>     threads         = new HashMap<Long, ClientThread>();
    public static   ServerAPI                   serverAPI       = new ServerAPI();
    public          ServerSocket                serverSocket;
    private         boolean                     m_shouldInterrupt;
    
    void StartServer()
    {
        try
        {
            Resume();
            serverSocket    = new ServerSocket(PORT);
            
            while (!shouldInterrupt())
            {
                Socket clientSocket = serverSocket.accept();
                
                System.out.println("new client");
                ClientThread thread = new ClientThread(clientSocket);
                thread.start();
                if (shouldInterrupt())  break;
                threads.put(thread.getId(), thread);
            }
        }
        catch (CowsBullsException cbEx) 
        {
            System.out.println(cbEx.GetMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        finally
        {
            for (Map.Entry<Long, ClientThread> entry : threads.entrySet())
            {
                entry.getValue().interrupt();
            }
            clients.clear();
            
            try {
                serverSocket.close();
            } 
            catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
    }
    
    public static void main(String[] args) 
    {
        CBServer server = new CBServer();
        server.StartServer();
    }   

    /**
     * @return the shouldInterrupt
     */
    public synchronized boolean shouldInterrupt() {
        return m_shouldInterrupt;
    }

    /**
     * @param shouldInterrupt the shouldInterrupt to set
     */
    public synchronized void Stop()
    {
        this.m_shouldInterrupt = true;
    }
    public synchronized void Resume()
    {
        this.m_shouldInterrupt = false;
    }
    
}
