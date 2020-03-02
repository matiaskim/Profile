/*
 * Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Random;

public class Winner implements Runnable
{
    private LinkedList<Player> players;
    private int minimumScore;
    private int numOfPlayers;
    private int playersFinished;
    private int playersWaiting;
    private Timestamp timeStamp;
    private boolean tied;
    
    public Winner(LinkedList<Player> players, int numOfPlayers)
    {
        this.players = players;
        this.numOfPlayers = numOfPlayers;
        playersFinished = 0;
        playersWaiting = 0;
    }
    
    public void run()
    {
        Player playerInAction;
        LinkedList<Player> loserPool = new LinkedList<>();
        
        while(players.size() > 1)
        {
            waitPlayersMove();
            
            minimumScore = Integer.MAX_VALUE;
            
            for(int i = 0; i < players.size(); i++)
            {
                playerInAction = players.get(i);
                playerInAction.waitUntilDone();
                
                if(playerInAction.getNumOfPoints() < minimumScore)
                {
                    loserPool.clear();
                    loserPool.add(playerInAction);
                    minimumScore = playerInAction.getNumOfPoints();
                }
                else if(playerInAction.getNumOfPoints() == minimumScore)
                {
                    loserPool.add(playerInAction);
                }                          
            }
            
            if(loserPool.size() < players.size())
            {
                Player loser;
                if(loserPool.size() > 1)
                {
                    Random generator = new Random();
                    loser = loserPool.get(generator.nextInt(loserPool.size()));
                    System.out.println(getTimeStamp() + " " + loser.getNameOfPlayer() + " was randomly selected to be taken out of " + loserPool.size() + " lowest scoring players.");
                }
                else
                {
                    loser = loserPool.getFirst();
                    System.out.println(getTimeStamp() + " " + loser.getNameOfPlayer() + ": HAS BEEN ELIMINATED.");
                }
                
                players.remove(loser);
                tied = false;               
            }
            else
            {
                tied = true;
            }
            
            loserPool.clear();
            waitForWinner();
            System.out.println("\n\n" + getTimeStamp() + "GAME OVER\n" + players.getFirst().getNameOfPlayer() + ": IS VICTORIOUS.");
        }
    }
    
    public synchronized void waitPlayersMove()
    {
        playersFinished++;
        if(playersFinished <= numOfPlayers)
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
            playersFinished = 0;
            notifyAll();
        }
    }
    
    public synchronized void waitForWinner()
    {
        playersWaiting++;
        if(playersWaiting <= numOfPlayers)
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
            playersWaiting = 0;
            if(!tied)
            {
                numOfPlayers--;
            }
            notifyAll();
        }
    }
   
    @SuppressWarnings("deprecation")
    private String getTimeStamp()
    {
        timeStamp = new Timestamp(System.currentTimeMillis());
        return "[" + timeStamp.getHours() + ":"
                        + timeStamp.getMinutes() + ":"
                        + timeStamp.getSeconds() + "."
                        + timeStamp.getNanos()/1000000 + "]";
    }         
}

