/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cowsbulls.src.network;

/**
 *
 * @author Alexander
 */
public class ServerFunctions extends GeneralFunctions
{
    public static final int SIGN_UP             = 100;
    public static final int SIGN_IN             = 101;
    public static final int SIGN_OUT            = 102;
    public static final int GET_PLAYER          = 103;
    public static final int START_SEARCHING     = 200;
    public static final int GAME_ACCEPT         = 201;
    public static final int GAME_DECLINE        = 202;
    public static final int GAME_PLAYER_READY   = 203;
    public static final int TURN                = 204;
    public static final int LEAVE_GAME          = 205;    
    public static final int CLIENT_LEFT         = 300;
}
