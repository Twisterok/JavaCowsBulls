/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cowsbulls.src.network;

import com.cowsbulls.src.utils.CallbackConstants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

/**
 *
 * @author Alexander
 */
public class Message implements Serializable{
    
    private String          projectName;
    private int             commandNum;
    private List<Object>    args;
    
    private int             errCode;
    private String          errDescription;

    public Message()
    {
        commandNum      = GeneralFunctions.NO_ACTION;
        errCode         = CallbackConstants.GOOD;
        errDescription  = "";
        args            = new ArrayList<Object>();
        projectName     = "Cows and Bulls";
    }
    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return the commandNum
     */
    public int getCommandNum() {
        return commandNum;
    }

    /**
     * @param commandNum the commandNum to set
     */
    public void setCommandNum(int commandNum) {
        this.commandNum = commandNum;
    }

    /**
     * @return the args
     */
    public List<Object> getArgs() {
        return args;
    }

    /**
     * @param args the args to set
     */
    public void setArgs(List<Object> args) {
        this.args = args;
    }

    /**
     * @return the errCode
     */
    public int getErrCode() {
        return errCode;
    }

    /**
     * @param errCode the errCode to set
     */
    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    /**
     * @return the errDescription
     */
    public String getErrDescription() {
        return errDescription;
    }

    /**
     * @param errDescription the errDescription to set
     */
    public void setErrDescription(String errDescription) {
        this.errDescription = errDescription;
    }
}
