/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cowsbulls.src.database.interfaces;

import com.cowsbulls.src.objects.Game;
import com.cowsbulls.src.objects.Player;
import java.util.List;

/**
 *
 * @author Alexander
 */
public interface GameDAI {
    
    //-------------------------------------
    //  Standart operations
    //-------------------------------------
    public void         AddGame(Game game);
    public void         UpdateGame(Game game);
    public void         DeleteGame(Game game);
    public void         DeleteGame(Integer id);
    public Game         GetGame(Integer id);
    //-------------------------------------
    
    //-------------------------------------
    //  Get all games
    //-------------------------------------
    public List<Game> GetGames();
    //-------------------------------------
    
    //-------------------------------------
    //  Get games.
    //-------------------------------------
    public List<Game> GetGamesByStatus(Integer status);
    //-------------------------------------
}
