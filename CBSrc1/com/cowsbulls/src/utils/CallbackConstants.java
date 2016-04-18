package com.cowsbulls.src.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CallbackConstants {
	public static final int GOOD			=0;		//ОК
        public static final int CLIENT_LEFT     	=1;		//Клиент отключился
	public static final int REGISTRATION_ERROR	=300;		//Ошибка регистрации
	public static final int AUTHORISATION_ERROR	=301;		//Ошибка авторизации
        public static final int GAME_SEARCH_ERROR	=302;		//Ошибка поиска игры
	public static final int BAD			=500;		//Плохо
        public static final int NETWORK_ERROR   	=501;		//Ошибка работы с сетью.
        public static final int COMMAND_NOT_FOUND   	=502;		//Запрашиваемая команда не найдена
	
        
	public static String toMD5(String pass)
	{
		 MessageDigest md;
		 try 
		 {
			md = MessageDigest.getInstance("MD5");
                        return md.digest(pass.getBytes()).toString();
		} 
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
}	
