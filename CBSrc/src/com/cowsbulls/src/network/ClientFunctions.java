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
public class ClientFunctions extends GeneralFunctions
{
    public static final int SET_PLAYER          = 100;
    public static final int GET_PLAYER_RESPONSE = 101;
    public static final int SET_MAIN_WINDOW     = 102;
    public static final int GAME_READY          = 200;
    public static final int GAME_START          = 201;
    public static final int TURN_MAKE           = 202;
    public static final int TURN_WAIT           = 203;
    public static final int TURN_RESULT         = 204;
    public static final int GAME_FINISHED       = 205;
}
