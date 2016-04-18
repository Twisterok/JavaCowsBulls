/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cowsbulls.src.database.implementation;

import static com.cowsbulls.server.CBServer.factory;
import com.cowsbulls.src.database.interfaces.GameDAI;
import com.cowsbulls.src.objects.Game;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author Alexander
 */
public class GameDAO implements GameDAI{

    @Override
    public void AddGame(Game game) {
        
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction();
            session.save(game);
            session.getTransaction().commit();
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
        }
        finally
        {
            session.close();
        }
    }

    @Override
    public void UpdateGame(Game game) {
        
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction();
            session.update(game);
            session.getTransaction().commit();
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
        }
        finally
        {
            session.close();
        }
    }

    @Override
    public void DeleteGame(Game game) {
                
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction();
            session.delete(game);
            session.getTransaction().commit();
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
        }
        finally
        {
            session.close();
        }
    }

    @Override
    public void DeleteGame(Integer id) {
                
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction();
            Game game = (Game) session.get(Game.class,id);
            session.save(game);
            session.getTransaction().commit();
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
        }
        finally
        {
            session.close();
        }
    }

    @Override
    public Game GetGame(Integer id) {
        Game  game  = null;
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction();
            game = (Game) session.get(Game.class,id);            
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
        }
        finally
        {
            session.close();
        }
        return game;
    }

    @Override
    public List<Game> GetGames() {
        
        List<Game> games = new ArrayList<Game>();
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction(); 
            List<Integer> rows = session.createSQLQuery("select id from game").list();
            for (Integer id : rows)
            {
                games.add(GetGame(id));
            }
            
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
        }
        finally
        {
            session.close();
        }
        return games;
    }

    @Override
    public List<Game> GetGamesByStatus(Integer status) {
        List<Game> games = new ArrayList<Game>();
        Session session = factory.openSession();
        try
        {            
            session.beginTransaction(); 
            List<Integer> rows = session.createSQLQuery("select id from game where game_status = :status")
                                    .setParameter("status", status)
                                    .list();
            for (Integer id : rows)
            {
                games.add(GetGame(id));
            }
            
        }
        catch (org.hibernate.HibernateException he)
        {
            if (session.getTransaction().isActive())    session.getTransaction().rollback();
        }
        finally
        {
            session.close();
        }
        return games;
    }    
}
