/*
 * Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Callable;

public class PlayerPart3 implements Callable<String>
{
    private String nameOfPlayer;
    private String move;
    private int numOfPoints;
    private LinkedList<PlayerPart3> players;
    private PlayerPart3 opponent;
    private boolean fail;
    private WinnerPart3 winner;
    private HashMap<String, Integer> memoization;
    Timestamp timeStamp;
    
    public PlayerPart3(String nameOfPlayer, LinkedList<PlayerPart3> players, WinnerPart3 winner)
    {
        numOfPoints = 0;
        this.winner = winner;
        this.nameOfPlayer = nameOfPlayer;
        this.players = players;
        fail = false;
        memoization = new HashMap<>();
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
            
            String opponentMove;
            Integer temporaryScore;
            for(int i = 0; i < players.size(); i++)
            {
                opponent = players.get(i);
                if(opponent == this)
                {
                    continue;                  
                }
                opponentMove = opponent.nextMove();
                temporaryScore = memoization.get(move + opponentMove);
                if(temporaryScore != null)
                {
                    numOfPoints += temporaryScore;
                }
                else
                {
                    if(this.move == "rock")
                    {
                        if(opponent.nextMove() == "paper")
                        {
                            memoization.put(move + opponentMove, -1);
                            numOfPoints -=1;
                        }
                        else if(opponent.nextMove() == "scissors")
                        {
                            memoization.put(move + opponentMove, 1);
                            numOfPoints += 1;
                        }
                        else
                        {
                            memoization.put(move + opponentMove, 0);
                        }
                    }
                    else if(this.move == "paper")
                    {
                        if(opponent.nextMove() == "scissors")
                        {
                            memoization.put(move + opponentMove, -1);                        
                            numOfPoints -= 1;
                        }
                        else if(opponent.nextMove() == "rock")
                        {
                            memoization.put(move + opponentMove, 1);
                            numOfPoints += 1;
                        }
                        else
                        {
                            memoization.put(move + opponentMove, 0);
                        }
                    }
                    else
                    {
                        if(opponent.nextMove() == "rock")
                        {
                            memoization.put(move + opponentMove, -1);
                            numOfPoints -= 1;
                        }
                        else if(opponent.nextMove() == "paper")
                        {
                            memoization.put(move + opponentMove, 1);
                            numOfPoints += 1;
                        }
                        else
                        {
                            memoization.put(move + opponentMove, 0);
                        }
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
