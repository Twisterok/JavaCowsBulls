/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cowsbulls.src.database.interfaces;

import com.cowsbulls.src.objects.Game;
import com.cowsbulls.src.objects.Player;
import com.cowsbulls.src.utils.CowsBullsException;
import java.util.List;

/**
 *
 * @author Alexander
 */
public interface PlayerDAI 
{
    //-------------------------------------
    //  Standart operations
    //-------------------------------------
    public void         AddPlayer(Player player) throws CowsBullsException;
    public void         UpdatePlayer(Player player) throws CowsBullsException;
    public void         DeletePlayer(Player player) throws CowsBullsException;
    public void         DeletePlayer(int id) throws CowsBullsException;
    public Player       GetPlayer(int id) throws CowsBullsException;
    public Player       GetPlayer(String login) throws CowsBullsException;
    //-------------------------------------
    
    //-------------------------------------
    //  Get all players
    //-------------------------------------
    public List<Player> GetPlayers() throws CowsBullsException;
    public List<Player> GetSearchingPlayers() throws CowsBullsException;
    //-------------------------------------
    
    
    //-------------------------------------
    //  Get player in game
    //-------------------------------------    
    public List<Player> GetPlayers(int game_id) throws CowsBullsException;
    public List<Player> GetPlayers(Game game) throws CowsBullsException;
    //-------------------------------------   
    
    
    //-------------------------------------
    //  Verification
    //-------------------------------------        
    public boolean PlayerExist(Player p) throws CowsBullsException;
}
