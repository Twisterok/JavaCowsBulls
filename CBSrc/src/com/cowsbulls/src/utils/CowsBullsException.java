/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cowsbulls.src.utils;

/**
 *
 * @author Alexander
 */
public class CowsBullsException extends Exception{
    
    private int errCode;
    private String errDescription;
    public CowsBullsException(int errCode, String errDescription)  
    { 
        super();
        
        this.errCode        = errCode;
        this.errDescription = errDescription;
    }
    
    public String GetMessage()
    {
        return errDescription;
    }        

    public int GetErrCode()
    {
        return errCode;
    }        
}
