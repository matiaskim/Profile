/* Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public class GameCreator2 
{
    private int readyPlayers;
    private int numOfPlayers;
    private boolean gameOver;
    
    private Player2 opponent;
    private Player2 winner;
    
    private LinkedBlockingQueue<Player2> playerQueue;
    LinkedList<Player2> players;
    HashMap<Player2, String> arena;    
    
    GameCreator2(int beginningPlayers)
    {
        readyPlayers = 0;
        numOfPlayers = beginningPlayers;
        playerQueue = new LinkedBlockingQueue<>();
        players = new LinkedList<Player2>();
        gameOver = beginningPlayers > 1 ? false : true;       
    }
    
    public synchronized void getMatch(Player2 self)
    {
        if(!playerQueue.contains(self))
        {
            return;
        }
        else
        {
            playerQueue.remove(self);
            
            opponent= playerQueue.poll();
            self.setOpponent(opponent);
            
            opponent.setOpponent(self);
            
            GameSynchronizer gameSync = new GameSynchronizer();
            self.setGameSync(gameSync);
            opponent.setGameSync(gameSync);
            
            System.out.println(getTimeStamp() + " " + self.getNameOfPlayer() + ": setting opponent to " + self.getOpponent().getNameOfPlayer() + ".");           
        }
    }
    
    public synchronized void gameReady(Player2 self)
    {
        players.add(self);
        
        System.out.println(getTimeStamp() + " " + self.getNameOfPlayer() + ": " + players.size() + "/" + numOfPlayers + " added to the players list.");
        
        if(players.size() < numOfPlayers)
        {
            try
            {
                wait();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Collections.shuffle(players);
            playerQueue.addAll(players);
            System.out.println("\n\n" + getTimeStamp() + " " + "All players ready to begin the tournament.");
            System.out.println(getTimeStamp() + " " + "PLayers remaining: " + players.size() + ".");
            System.out.println(getTimeStamp() + " " + "PLayers waiting for a match: " + playerQueue.size() + ".\n\n");
            
            notifyAll();
        }
        System.out.println(getTimeStamp() + " " + self.getNameOfPlayer() + ": starting the game.");
    }
    
    public synchronized void roundReady(Player2 p, int numOfPoints)
    {
        readyPlayers++;
        if(numOfPoints != 1)
        {
            remove(p);
        }
        if(readyPlayers < numOfPlayers)
        {
            try
            {
                wait();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            numOfPlayers = players.size();
            readyPlayers = 0;
            
            if(numOfPlayers > 1)
            {
                Collections.shuffle(players);
                playerQueue.addAll(players);
                
                System.out.println("\n\n" + getTimeStamp() + "Players all finished the round.");
                System.out.println(getTimeStamp() + " " + "Players remaining: " + players.size() + ".");
                System.out.println(getTimeStamp() + " " + " Players waiting for a match: " + playerQueue.size() + ".\n\n");
            }
            
            notifyAll();
        }
    }
    
    public synchronized boolean gameOver()
    {
        return gameOver;                      
    }
        
    public synchronized Player2 getWinner()
    {
        return winner;
    }
        
    public synchronized void remove(Player2 p)
    {
        players.remove(p);
        if(players.size() == 1)
        {
            gameOver = true;
            winner = players.getFirst();
        }
    }
    
    @SuppressWarnings("deprecation")
    private String getTimeStamp()
    {
        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        return "[" + timeStamp.getHours() + ":"
                        + timeStamp.getMinutes() + ":"
                        + timeStamp.getSeconds() + "."
                        + timeStamp.getNanos()/1000000 + "]";
    }       
}

