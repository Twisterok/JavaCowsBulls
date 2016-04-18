package com.cowsbulls.src.utils;

public class SQLConstants {
        //------------------------------------
        //  Player constants
        //------------------------------------
        public static final int PLAYER_STATUS_OFFLINE           = 0;
        public static final int PLAYER_STATUS_ONLINE            = 1;
        public static final int PLAYER_STATUS_SEARCHING         = 2;
        public static final int PLAYER_STATUS_WAITING_OPPONENT  = 3;
        public static final int PLAYER_STATUS_PREGAME           = 4;
        public static final int PLAYER_STATUS_READY_TO_MOVE     = 5;
        public static final int PLAYER_STATUS_PLAYING           = 6;
        //------------------------------------

        //------------------------------------
        //  Game constants
        //------------------------------------
        public static final int GAME_STATUS_NOT_STARTED = 0;
        public static final int GAME_STATUS_STARTED     = 1;
        
        public static final int GAME_TYPE_DEFAULT       = 0;
        //------------------------------------
	public static final int PNG     = 4;
	public static final int JPG     = 5;
	public static final int BMP     = 6;
	
	public static final int NOTYPE	= -1;
	
	public static final int MALE	= 7;
	public static final int FEMALE	= 8;
	
	
	public static String Type_toStr(Integer _type)
	{
		switch (_type)
		{
		case PNG:
			return "png";
		case JPG:
			return "jpg";
		case BMP:
			return "bmp";
		}
		return "";
	}
}
