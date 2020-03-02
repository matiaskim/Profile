/*
 * Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Callable;

public class PlayerPart2 implements Callable<String>
{
    private String nameOfPlayer;
    private String move;
    private int numOfPoints;
    private LinkedList<PlayerPart2> players;
    private PlayerPart2 opponent;
    private boolean fail;
    private WinnerPart2 winner;
    Timestamp timeStamp;
    
    public PlayerPart2(String nameOfPlayer, LinkedList<PlayerPart2> players, WinnerPart2 winner)
    {
        numOfPoints = 0;
        this.winner = winner;
        this.nameOfPlayer = nameOfPlayer;
        this.players = players;
        fail = false;
    }
    
    public String nextMove()
    {
        return move;
    }
    
    public int getNumOfPoints()
    {
        return numOfPoints;
    }
    
    public String getNameOfPlayer()
    {
        return nameOfPlayer;
    }
    
    public synchronized void waitUntilDone()
    {
        while(!fail)
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
        fail = false;
    }
    
    public synchronized void done()
    {
        fail = true;
        notify();
    }
    
    public void makeNextMove()
    {
        Random generator = new Random();
        int moveInteger = generator.nextInt(3);
        if(moveInteger == 0)
        {
            move = "rock";
        }
        else if(moveInteger == 1)
        {
            move = "paper";
        }
        else
        {
            move = "scissors";
        }                      
    }
    
    public String call()
    {
        while(players.size() > 1)
        {
            makeNextMove();
            
            System.out.println(getTimeStamp() + " " + nameOfPlayer + ": waiting for opponents.");
            winner.waitPlayersMove();
            
            System.out.println(getTimeStamp() + " " + nameOfPlayer + ": " + move);
            
            for(int i = 0; i < players.size(); i++)
            {
                opponent = players.get(i);
                if(opponent == this)
                {
                    continue;                  
                }
                if(this.move == "rock")
                {
                    if(opponent.nextMove() == "paper")
                    {
                        numOfPoints -= 1;
                    }
                    else if(opponent.nextMove() == "scissors")
                    {
                        numOfPoints += 1;
                    }
                }
                else if(this.move == "paper")
                {
                    if(opponent.nextMove() == "scissors")
                    {
                        numOfPoints -= 1;
                    }
                    else if(opponent.nextMove() == "rock")
                    {
                        numOfPoints += 1;
                    }
                }
                else
                {
                    if(opponent.nextMove() == "rock")
                    {
                        numOfPoints -= 1;
                    }
                    else if(opponent.nextMove() == "paper")
                    {
                        numOfPoints += 1;
                    }
                }
            }
            
            System.out.println(getTimeStamp() + " " + nameOfPlayer + " score: " + numOfPoints);
            
            done();
                        
            winner.waitForWinner();
            
            if(!players.contains(this))
            {
                break;
            }
            
            numOfPoints = 0;
        }
        return(nameOfPlayer + " is finished playing.");
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
