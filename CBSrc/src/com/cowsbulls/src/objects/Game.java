/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cowsbulls.src.objects;

import com.cowsbulls.src.utils.SQLConstants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import org.hibernate.annotations.IndexColumn;

/**
 *
 * @author Alexander
 */
@Entity
@Table(name = "game")
public class Game implements java.io.Serializable 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int     id;
    
    @Column(name = "name",length = 45)
    private String  name;
    
    @Column(name = "game_type")
    private int     type;
    
    @Column(name = "game_status")
    private int     status;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="game_id")
    @IndexColumn(name="game_idx")
    private List<Player>    players;
    
    public Game()
    {
        players = new ArrayList<Player>();
        type    = 0;
        name    = "";
        id      = -1;
        status  = 0;
    }
    
    public Game(int _id, String _name, int _type, List<Player> _players, int _status, int _players_accepted)
    {
        players = _players;
        type    = _type;
        name    = _name;
        id      = _id;
        status  = _status;
    }
    
    public Game(Game _game)
    {
        players = _game.players;
        type    = _game.type;
        name    = _game.name;
        id      = _game.id;
        status  = _game.status;
    }
    
    /**
     * Add player to the game
     */
    public void AddPlayer(Player player)
    {
        getPlayers().add(player);
    }
    /**
     * Kick players from the game
     */
    public void KickPlayers()
    {
        getPlayers().clear();
    }
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * @param players the players to set
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }
    
    public boolean CheckPlayersStatus(int player_status)
    {
        for (Player p : this.getPlayers())  
            if (p.getStatus() != player_status)   return false;
        
        return true;
    }
    
    public Player getOpponent(Player player)
    {
        for (Player p : this.getPlayers())
        {
            if (!p.getLogin().equals(player.getLogin()))
            {
                return p;
            }
        }
        return null;
    }
   
}
