/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cowsbulls.server;

import com.cowsbulls.src.network.Message;
import com.cowsbulls.src.utils.CowsBullsException;

/**
 *
 * @author Alexander
 */
public interface Command 
{    
    public Message processMessage(Message msg) throws CowsBullsException;
}
