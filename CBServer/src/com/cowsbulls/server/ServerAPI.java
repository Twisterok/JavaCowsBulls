/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cowsbulls.server;

import com.cowsbulls.src.database.implementation.GameDAO;
import com.cowsbulls.src.database.implementation.PlayerDAO;
import com.cowsbulls.src.network.Client;
import com.cowsbulls.src.network.ClientFunctions;
import com.cowsbulls.src.network.Message;
import com.cowsbulls.src.network.ServerFunctions;
import com.cowsbulls.src.objects.Game;
import com.cowsbulls.src.objects.Player;
import com.cowsbulls.src.utils.CallbackConstants;
import com.cowsbulls.src.utils.CowsBullsException;
import com.cowsbulls.src.utils.Helper;
import com.cowsbulls.src.utils.SQLConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Alexander
 */
public class ServerAPI {
    
    private PlayerDAO                   m_playerDB;
    private GameDAO                     m_gameDB;
    private static Map<Integer,Command> m_commands;
    
    public ServerAPI()
    {
        m_playerDB  = new PlayerDAO();
        m_gameDB    = new GameDAO();
        m_commands  = new HashMap<Integer,Command>();   
        m_commands.put(ServerFunctions.NO_ACTION,           (Message m) -> this.no_action(m));
        m_commands.put(ServerFunctions.RESPONSE,            (Message m) -> this.response(m));
        m_commands.put(ServerFunctions.SIGN_UP,             (Message m) -> this.sign_up(m));
        m_commands.put(ServerFunctions.SIGN_IN,             (Message m) -> this.sign_in(m));
        m_commands.put(ServerFunctions.SIGN_OUT,            (Message m) -> this.sign_out(m));
        m_commands.put(ServerFunctions.START_SEARCHING,     (Message m) -> this.start_searching(m));
        m_commands.put(ServerFunctions.GAME_ACCEPT,         (Message m) -> this.game_accept(m));
        m_commands.put(ServerFunctions.GAME_DECLINE,        (Message m) -> this.game_decline(m));
        m_commands.put(ServerFunctions.GAME_PLAYER_READY,   (Message m) -> this.player_ready(m));
        m_commands.put(ServerFunctions.TURN ,               (Message m) -> this.turn(m));
        m_commands.put(ServerFunctions.LEAVE_GAME ,         (Message m) -> this.leave_game(m));
        m_commands.put(ServerFunctions.GET_PLAYER ,         (Message m) -> this.get_player(m));
    }

        
    public Command GetCommand(Integer key)
    {
        
        return m_commands.get(key);
    }
    public Message no_action(Message msg)
    {
        return msg;
    }
    public Message response(Message msg) throws CowsBullsException
    {
        msg.setCommandNum(ClientFunctions.RESPONSE);
        return msg;
    }
    //---------------------------------
    //  Authorisation part
    //---------------------------------    
    public Message sign_up(Message msg) throws CowsBullsException
    {
        if (msg.getArgs().size() != 1)  throw new CowsBullsException(CallbackConstants.REGISTRATION_ERROR,"Wrong number of parameters");
        if (!(msg.getArgs().get(0) instanceof Player)) throw new CowsBullsException(CallbackConstants.REGISTRATION_ERROR,"Parameter type missmatch");
        
        Player player = (Player) msg.getArgs().get(0);
        if (!player.CheckValues()) 
        {
            throw new CowsBullsException(CallbackConstants.REGISTRATION_ERROR,"Registration error: Not all required fields are filled");
        }
        else if (m_playerDB.PlayerExist(player))    
        {
            throw new CowsBullsException(CallbackConstants.REGISTRATION_ERROR,"Registration error: User " + player.getLogin() + " already exist.");
        }
        else
        {
            m_playerDB.AddPlayer(player);
            msg.getArgs().clear();
            msg.setCommandNum(ClientFunctions.RESPONSE);
            msg.setErrCode(CallbackConstants.GOOD);
            msg.setErrDescription("Registration of user ["+player.getLogin()+"] complete.");
        }
        return msg;
    }
    
    public Message sign_in(Message msg) throws CowsBullsException
    {   
        if (msg.getArgs().size() != 2)  throw new CowsBullsException(CallbackConstants.AUTHORISATION_ERROR,"Wrong number of parameters");
        if (!(msg.getArgs().get(0) instanceof String) || 
            !(msg.getArgs().get(1) instanceof String)) throw new CowsBullsException(CallbackConstants.AUTHORISATION_ERROR,"Parameters type missmatch");
        
        String login    = (String) msg.getArgs().get(0);
        String password = (String) msg.getArgs().get(1);
        //-------------------------
        //  AUTHORISATION
        //-------------------------
        Player p        = m_playerDB.GetPlayer(login);
        if (p == null)
        {
            throw new CowsBullsException(CallbackConstants.AUTHORISATION_ERROR, "Authorisation error: There user ["+ login +"] hasn't been found.");
        }
        else if (p.getStatus() != SQLConstants.PLAYER_STATUS_OFFLINE)
        {
            throw new CowsBullsException(CallbackConstants.AUTHORISATION_ERROR, "Authorisation error: User ["+ login +"] is online.");
        }
        else if (!p.getPassword().equals(password))
        {
            throw new CowsBullsException(CallbackConstants.AUTHORISATION_ERROR, "Authorisation error: Wrong password.");
        }
        else
        {   
            p.setStatus(SQLConstants.PLAYER_STATUS_ONLINE);
            m_playerDB.UpdatePlayer(p);
            msg.getArgs().clear();
            msg.getArgs().add(p);
            msg.setCommandNum(ClientFunctions.SET_PLAYER);
            msg.setErrCode(CallbackConstants.GOOD);
            msg.setErrDescription("You have just signed in as ["+p.getLogin()+"].");
        }
        return msg;
        //------------------------
    }
    public Message sign_out(Message msg) throws CowsBullsException
    {
        if (msg.getArgs().size() != 1)                  throw new CowsBullsException(CallbackConstants.AUTHORISATION_ERROR,"Wrong number of parameters");
        if (!(msg.getArgs().get(0) instanceof Player))  throw new CowsBullsException(CallbackConstants.AUTHORISATION_ERROR,"Parameter type missmatch");
        
        Player player = (Player) msg.getArgs().get(0);
        player.setStatus(SQLConstants.PLAYER_STATUS_OFFLINE);
        m_playerDB.UpdatePlayer(player);
        msg.getArgs().clear();
        msg.setCommandNum(ClientFunctions.RESPONSE);
        msg.setErrCode(CallbackConstants.GOOD);
        msg.setErrDescription("You have signed out.");
        return msg;
    }
    
    
    //---------------------------------
    //  Start game part
    //---------------------------------  
    public Message start_searching(Message msg) throws CowsBullsException
    {
        if (msg.getArgs().size() != 1)                  throw new CowsBullsException(CallbackConstants.GAME_SEARCH_ERROR,"Wrong number of parameters");
        if (!(msg.getArgs().get(0) instanceof Player))  throw new CowsBullsException(CallbackConstants.GAME_SEARCH_ERROR,"Parameter type missmatch");
        
        Player player = (Player) msg.getArgs().get(0);
        player.setStatus(SQLConstants.PLAYER_STATUS_SEARCHING);
        m_playerDB.UpdatePlayer(player);
        
        msg.getArgs().clear();
        msg.setCommandNum(ClientFunctions.RESPONSE);
        msg.setErrCode(CallbackConstants.GOOD);
        msg.setErrDescription("Searching game for player ["+player.getLogin()+" has started");
        return null;
    }
    
    public Message make_games(Message msg) throws CowsBullsException
    {
        if (msg.getArgs().size() != 0)  throw new CowsBullsException(CallbackConstants.BAD,"Wrong number of parameters");
        
        List<Player>    players = m_playerDB.GetSearchingPlayers();
        List<Game>      games   = new ArrayList<Game>();
        for (int i = 0; i < players.size(); i += 2)
        {
            if ( (players.size() - i) < 2)  break;

            Player p1 = players.get(i);
            Player p2 = players.get(i+1);

            Game game = new Game();
            game.AddPlayer(p1);
            game.AddPlayer(p2);
            game.setStatus(SQLConstants.GAME_STATUS_NOT_STARTED);
            game.setType(SQLConstants.GAME_TYPE_DEFAULT);
            game.setName(p1.getLogin() + " vs. " + p2.getLogin()); 
            games.add(game);
            m_gameDB.AddGame(game);
            
            msg.getArgs().clear();
            msg.setCommandNum(ClientFunctions.GAME_READY);
            msg.setErrCode(CallbackConstants.GOOD);
            msg.setErrDescription("Game is ready");
            
            CBServer.clients.get(p1.getLogin()).sendMessage(msg);
            CBServer.clients.get(p2.getLogin()).sendMessage(msg);
        }
        
        msg.getArgs().clear();
        msg.setCommandNum(ClientFunctions.RESPONSE);
        msg.setErrCode(CallbackConstants.GOOD);
        msg.setErrDescription("Games pack created");
        return msg;
    }
    
    
    public Message game_accept(Message msg) throws CowsBullsException
    {
        if (msg.getArgs().size() != 2)                  throw new CowsBullsException(CallbackConstants.BAD,"Wrong number of parameters");
        if (!(msg.getArgs().get(0) instanceof Player))  throw new CowsBullsException(CallbackConstants.BAD,"Parameter type missmatch");
        if (!(msg.getArgs().get(1) instanceof Integer)) throw new CowsBullsException(CallbackConstants.BAD,"Parameter type missmatch");
        
        Player  p       = (Player) msg.getArgs().get(0);
        Integer game_id = (Integer) msg.getArgs().get(1);
        p.setStatus(SQLConstants.PLAYER_STATUS_WAITING_OPPONENT);
        m_playerDB.UpdatePlayer(p);
        
        Game game = m_gameDB.GetGame(game_id);
        
        //TO DO: show count of players accepted the game
        
        if (game.CheckPlayersStatus(SQLConstants.PLAYER_STATUS_WAITING_OPPONENT))
        {
            msg.getArgs().clear();
            msg.setCommandNum(ClientFunctions.GAME_START);
            msg.setErrCode(CallbackConstants.GOOD);
            msg.setErrDescription("All players accepted the game");

            Player player = game.getPlayers().get(0); // игрок 1
            player.setStatus(SQLConstants.PLAYER_STATUS_PREGAME);
            m_playerDB.UpdatePlayer(player);
            CBServer.clients.get(player.getLogin()).sendMessage(msg);           
            
            player = game.getPlayers().get(1);       // игрок 2
            player.setStatus(SQLConstants.PLAYER_STATUS_PREGAME);
            m_playerDB.UpdatePlayer(player);
            CBServer.clients.get(player.getLogin()).sendMessage(msg);           
            
            game.setStatus(SQLConstants.GAME_STATUS_STARTED);
            m_gameDB.UpdateGame(game);
        }
       
        msg.getArgs().clear();
        msg.setCommandNum(ClientFunctions.RESPONSE);
        msg.setErrCode(CallbackConstants.GOOD);
        msg.setErrDescription("Player accepted the game");
        return msg;
    }
    
    public Message game_decline(Message msg) throws CowsBullsException
    {
        if (msg.getArgs().size() != 2)                  throw new CowsBullsException(CallbackConstants.BAD,"Wrong number of parameters");
        if (!(msg.getArgs().get(0) instanceof Player))  throw new CowsBullsException(CallbackConstants.BAD,"Parameter type missmatch");
        if (!(msg.getArgs().get(1) instanceof Integer)) throw new CowsBullsException(CallbackConstants.BAD,"Parameter type missmatch");
        
        Player  p       = (Player) msg.getArgs().get(0);
        Integer game_id = (Integer) msg.getArgs().get(1);
        
        Game game = m_gameDB.GetGame(game_id);
        
        msg.getArgs().clear();
        msg.setCommandNum(ClientFunctions.GAME_FINISHED);
        msg.setErrCode(CallbackConstants.GOOD);
        msg.setErrDescription("");

        for (Player player : game.getPlayers())
        {
            player.setStatus(SQLConstants.PLAYER_STATUS_ONLINE);
            m_playerDB.UpdatePlayer(player);
            CBServer.clients.get(player.getLogin()).sendMessage(msg);           
        }
            
        m_gameDB.DeleteGame(game);
       
        msg.getArgs().clear();
        msg.setCommandNum(ClientFunctions.RESPONSE);
        msg.setErrCode(CallbackConstants.GOOD);
        msg.setErrDescription("Player accepted the game");
        return msg;
    }
    
    public Message player_ready(Message msg) throws CowsBullsException
    {
        if (msg.getArgs().size() != 2)                  throw new CowsBullsException(CallbackConstants.BAD,"Wrong number of parameters");
        if (!(msg.getArgs().get(0) instanceof Player))  throw new CowsBullsException(CallbackConstants.BAD,"Parameter type missmatch");
        if (!(msg.getArgs().get(1) instanceof Integer)) throw new CowsBullsException(CallbackConstants.BAD,"Parameter type missmatch");
        
        Player  p       = (Player) msg.getArgs().get(0);
        Integer game_id = (Integer) msg.getArgs().get(1);
        p.setStatus(SQLConstants.PLAYER_STATUS_READY_TO_MOVE);
        m_playerDB.UpdatePlayer(p);
        
        Game game = m_gameDB.GetGame(game_id);
        
        if (game.CheckPlayersStatus(SQLConstants.PLAYER_STATUS_READY_TO_MOVE))
        {
            Random rnd  = new Random(System.currentTimeMillis());
            int rnd_idx = rnd.nextInt(1);
            
            msg.getArgs().clear();
            msg.setErrCode(CallbackConstants.GOOD);
            
            Player player = game.getPlayers().get(rnd_idx);
            msg.setCommandNum(ClientFunctions.TURN_MAKE);
            msg.setErrDescription("Your turn!");
            player.setStatus(SQLConstants.PLAYER_STATUS_PLAYING);
            m_playerDB.UpdatePlayer(player);
            CBServer.clients.get(player.getLogin()).sendMessage(msg);
            
            player = game.getPlayers().get( (rnd_idx + 1) %2); // если был 0, станет 1 и наоборот
            msg.setCommandNum(ClientFunctions.TURN_WAIT);
            msg.setErrDescription("Enemy's turn!");
            player.setStatus(SQLConstants.PLAYER_STATUS_PLAYING);
            m_playerDB.UpdatePlayer(player);
            CBServer.clients.get(player.getLogin()).sendMessage(msg);            
        }
        msg.getArgs().clear();
        msg.setCommandNum(ClientFunctions.RESPONSE);
        msg.setErrCode(CallbackConstants.GOOD);
        msg.setErrDescription("Player is ready to play");
        return msg;
    }
    
    
    public Message turn(Message msg) throws CowsBullsException
    {
        if (msg.getArgs().size() != 3)                  throw new CowsBullsException(CallbackConstants.BAD,"Wrong number of parameters");
        if (!(msg.getArgs().get(0) instanceof Player))  throw new CowsBullsException(CallbackConstants.BAD,"Parameter type missmatch");
        if (!(msg.getArgs().get(1) instanceof Integer)) throw new CowsBullsException(CallbackConstants.BAD,"Parameter type missmatch");
        if (!(msg.getArgs().get(2) instanceof String))  throw new CowsBullsException(CallbackConstants.BAD,"Parameter type missmatch");
        
        Player  p       = (Player) msg.getArgs().get(0);
        Integer game_id = (Integer) msg.getArgs().get(1);
        String  number  = (String) msg.getArgs().get(2);
        int bulls;
        int cows;
        Game game = m_gameDB.GetGame(game_id);
        
        Player opponent = game.getOpponent(p);
        bulls   = Helper.countBulls(opponent.getNumber(), number);
        cows    = Helper.countCows(opponent.getNumber(), number);
        
        if (bulls == 4)
        {
            // GAME FINISHED
            msg.getArgs().clear();
            msg.setCommandNum(ClientFunctions.GAME_FINISHED);
            msg.setErrCode(CallbackConstants.GOOD);
            msg.setErrDescription("");
            
            for (Player player : game.getPlayers())
            {
                if (player.getLogin().equals(p.getLogin())) player.setWins(player.getWins() + 1);
                else                                        player.setLoses(player.getLoses()+ 1);
                player.setStatus(SQLConstants.PLAYER_STATUS_ONLINE);
                m_playerDB.UpdatePlayer(player);
                CBServer.clients.get(player.getLogin()).sendMessage(msg);
            }
        }
        else
        {            
            for (Player player : game.getPlayers())
            {
                msg.setCommandNum(ClientFunctions.TURN_RESULT);
                msg.setErrCode(CallbackConstants.GOOD);
                msg.setErrDescription("");         
                
                msg.getArgs().clear();
                msg.getArgs().add(number);
                msg.getArgs().add(cows);
                msg.getArgs().add(bulls);
                if (player.getLogin().equals(p.getLogin())) msg.getArgs().add(true);   //TURN OWNER
                else                                        msg.getArgs().add(true);   //TURN RECEIVER
                CBServer.clients.get(player.getLogin()).sendMessage(msg);
            }
            msg.getArgs().clear();
            msg.setErrCode(CallbackConstants.GOOD);
            
            msg.setCommandNum(ClientFunctions.TURN_MAKE);
            msg.setErrDescription("Your turn!");
            CBServer.clients.get(opponent.getLogin()).sendMessage(msg);
            
            msg.setCommandNum(ClientFunctions.TURN_WAIT);
            msg.setErrDescription("Enemy's turn!");
            CBServer.clients.get(p.getLogin()).sendMessage(msg);
        }
        
        msg.getArgs().clear();
        msg.setCommandNum(ClientFunctions.RESPONSE);
        msg.setErrCode(CallbackConstants.GOOD);
        msg.setErrDescription("turn is maken");
        return msg;
    }
    
    public Message leave_game(Message msg) throws CowsBullsException
    {
        if (msg.getArgs().size() != 2)                  throw new CowsBullsException(CallbackConstants.BAD,"Wrong number of parameters");
        if (!(msg.getArgs().get(0) instanceof Player))  throw new CowsBullsException(CallbackConstants.BAD,"Parameter type missmatch");
        if (!(msg.getArgs().get(1) instanceof Integer)) throw new CowsBullsException(CallbackConstants.BAD,"Parameter type missmatch");
        
        Player  p       = (Player) msg.getArgs().get(0);
        Integer game_id = (Integer) msg.getArgs().get(1);
        Game game       = m_gameDB.GetGame(game_id);
        
        msg.getArgs().clear();
        msg.setCommandNum(ClientFunctions.GAME_FINISHED);
        msg.setErrCode(CallbackConstants.GOOD);
        msg.setErrDescription("");
        
        for (Player player : game.getPlayers())
        {
                if (player.getLogin().equals(p.getLogin())) player.setLoses(player.getLoses()+ 1);
                else                                        player.setWins(player.getWins() + 1);
                player.setStatus(SQLConstants.PLAYER_STATUS_ONLINE);
                m_playerDB.UpdatePlayer(player);
                CBServer.clients.get(player.getLogin()).sendMessage(msg);
        } 
        
        msg.getArgs().clear();
        msg.setCommandNum(ClientFunctions.RESPONSE);
        msg.setErrCode(CallbackConstants.GOOD);
        msg.setErrDescription("turn is maken");
        return msg;
    }
    
    public Message get_player(Message msg) throws CowsBullsException
    {
        if (msg.getArgs().size() != 1)                  throw new CowsBullsException(CallbackConstants.BAD,"Wrong number of parameters");
        if (!(msg.getArgs().get(0) instanceof Player))  throw new CowsBullsException(CallbackConstants.BAD,"Parameter type missmatch");
        
        Player  p       = (Player) msg.getArgs().get(0);
        
        p = m_playerDB.GetPlayer(p.getLogin());
        msg.getArgs().clear();
        msg.getArgs().add(p);
        msg.setCommandNum(ClientFunctions.GET_PLAYER_RESPONSE);
        msg.setErrCode(CallbackConstants.GOOD);
        msg.setErrDescription("");
        CBServer.clients.get(p.getLogin()).sendMessage(msg);
        
        msg.getArgs().clear();
        msg.setCommandNum(ClientFunctions.RESPONSE);
        msg.setErrCode(CallbackConstants.GOOD);
        msg.setErrDescription("turn is maken");
        return msg;
    }
    
    public Message Client_left(Message msg) throws CowsBullsException
    {   
        /*if (msg.getArgs().size() != 1)                  throw new CowsBullsException(CallbackConstants.BAD,"Wrong number of parameters");
        if (!(msg.getArgs().get(0) instanceof Player))  throw new CowsBullsException(CallbackConstants.BAD,"Parameter type missmatch");
        
        Player  p       = (Player) msg.getArgs().get(0);*/
        
        msg.getArgs().clear();
        msg.setErrCode(CallbackConstants.CLIENT_LEFT);
        msg.setErrDescription("Client left");
        return msg;
    }
}
