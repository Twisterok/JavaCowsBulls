/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cowsbulls.src.database.implementation;

import static com.cowsbulls.server.CBServer.factory;
import com.cowsbulls.src.database.interfaces.PlayerDAI;
import com.cowsbulls.src.objects.Game;
import com.cowsbulls.src.objects.Player;
import com.cowsbulls.src.utils.CallbackConstants;
import com.cowsbulls.src.utils.CowsBullsException;
import com.cowsbulls.src.utils.SQLConstants;
import java.util.ArrayList;
import java.util.List;
import jdk.nashorn.internal.codegen.CompilerConstants;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;


/**
 *
 * @author Alexander
 */
public class PlayerDAO implements  PlayerDAI{

    @Override
    public void AddPlayer(Player player) throws CowsBullsException{
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction();
            session.save(player);
            session.getTransaction().commit();
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
            throw new CowsBullsException(CallbackConstants.BAD, he.getMessage());
        }
        finally
        {
            session.close();
        }
    }

    @Override
    public void UpdatePlayer(Player player) throws CowsBullsException {
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction();
            session.update(player);
            session.getTransaction().commit();
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
            throw new CowsBullsException(CallbackConstants.BAD, he.getMessage());
        }
        finally
        {
            session.close();
        }
    }

    @Override
    public void DeletePlayer(Player player) throws CowsBullsException {
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction();
            session.delete(player);
            session.getTransaction().commit();
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
            throw new CowsBullsException(CallbackConstants.BAD, he.getMessage());
        }
        finally
        {
            session.close();
        }
    }

    @Override
    public void DeletePlayer(int id) throws CowsBullsException{
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction();
            Player player = (Player) session.get(Player.class,id);
            session.delete(player);
            session.getTransaction().commit();
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
            throw new CowsBullsException(CallbackConstants.BAD, he.getMessage());
        }
        finally
        {
            session.close();
        }
    }

    @Override
    public Player GetPlayer(int id) throws CowsBullsException{
        Player  player  = null;
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction();
            player = (Player) session.get(Player.class,id);
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
            throw new CowsBullsException(CallbackConstants.BAD, he.getMessage());
        }
        finally
        {
            session.close();
        }
        return player;
    }

    @Override
    public Player GetPlayer(String login) throws CowsBullsException {
        Player  player  = null;
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction();
            Criteria cr = session.createCriteria(Player.class).add(Restrictions.eq("login", login));
            player = (Player) cr.uniqueResult();
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
            throw new CowsBullsException(CallbackConstants.BAD, he.getMessage());
        }
        finally
        {
            session.close();
        }
        return player;
    }

    @Override
    public List<Player> GetPlayers() throws CowsBullsException {
        
        List<Player> players = new ArrayList<Player>();
        
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction();
            List<Integer> rows = session.createSQLQuery("select id from player").list();
            session.getTransaction().commit();
            for (Integer id : rows)
            {
                players.add(GetPlayer(id));
            }
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
            throw new CowsBullsException(CallbackConstants.BAD, he.getMessage());
        }
        finally
        {
            session.close();
        }
        return players;
    }

    @Override
    public List<Player> GetPlayers(int game_id) throws CowsBullsException {
       List<Player> players = new ArrayList<Player>();
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction();
            List<Integer> rows = session.createSQLQuery("select id from player where game_id = :game_id")
                                         .setParameter("game_id", game_id)
                                        .list();
            session.getTransaction().commit();
            for (Integer id : rows)
            {
                players.add(GetPlayer(id));
            }
        }
        catch (CowsBullsException cbEx)
        {
            throw cbEx;
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
            throw new CowsBullsException(CallbackConstants.BAD, he.getMessage());
        }
        finally
        {
            session.close();
        }
        return players;
    }

    @Override
    public List<Player> GetPlayers(Game game) throws CowsBullsException {
        return GetPlayers(game.getId());
    }

    @Override
    public boolean PlayerExist(Player p) throws CowsBullsException 
    {
        return GetPlayer(p.getLogin()) != null;
    }
    
    @Override
    public List<Player> GetSearchingPlayers() throws CowsBullsException {
       List<Player> players = new ArrayList<Player>();
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction();
            List<Integer> rows = session.createSQLQuery("select id from player where status = :status limit 1000")
                                         .setParameter("status", SQLConstants.PLAYER_STATUS_SEARCHING)
                                        .list();
            session.getTransaction().commit();
            for (Integer id : rows)
            {
                players.add(GetPlayer(id));
            }
        }
        catch (CowsBullsException cbEx)
        {
            throw cbEx;
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
            throw new CowsBullsException(CallbackConstants.BAD, he.getMessage());
        }
        finally
        {
            session.close();
        }
        return players;
    }
}
