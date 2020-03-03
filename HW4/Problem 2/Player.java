/*
 * Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

import java.sql.Timestamp;
import java.util.Random;

public class Player implements Runnable
{
    private String nameOfPlayer;
    private String move;
    private int numOfPoints;
    private Player opponent;
    private GameCreator gameCreator;
    private GameSynchronizer gameSync;
    private String result;
    
    private Timestamp timeStamp;
    
    public Player(String nameOfPlayer, GameCreator gameCreator)
    {
        this.nameOfPlayer = nameOfPlayer;
        this.gameCreator = gameCreator;
    }
    
    public void run()
    {
        gameCreator.gameReady(this);
        
        numOfPoints = 1;
        
        while(!gameCreator.gameOver() && numOfPoints == 1)
        {
            gameCreator.getMatch(this);
            
            numOfPoints = 0;
            
            while(numOfPoints == 0)
            {
                makeNextMove();
                
                gameSync.readyStart();
                
                numOfPoints = determine(opponent.nextMove());
                result = numOfPoints == 0 ? "TIE" : numOfPoints > 0 ? "WIN" : "LOSE";
                
                gameSync.readyStop();
                System.out.println(getTimeStamp() + " " + nameOfPlayer + " vs " + opponent.getNameOfPlayer()
                                                        + ": " + move + " vs " + opponent.nextMove() + ": " + result);
            }
            
            if(numOfPoints != 1)
            {
                System.out.println(getTimeStamp() + " " + nameOfPlayer + ": ELIMINATED.");
            }
            
            gameCreator.roundReady(this, numOfPoints);
        }
        if(gameCreator.getWinner() == this)
        {
            System.out.println("\n\n" + getTimeStamp() + " " + "GAME OVER\n\n" + nameOfPlayer + ": WON THE TOURNAMENT.");
        }
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
    
    public int determine(String opponentMove)
    {
        if(move == "rock")
        {
            if(opponentMove == "paper")
            {
                return -1;
            }
            else if(opponentMove == "scissors")
            {
                return 1;                
            }
            else 
            {
                return 0;
            }
        }
        else if(move == "paper")
        {
            if(opponentMove == "scissors")
            {
                return -1;               
            }
            else if(opponentMove == "rock")
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
        else
        {
            if(opponentMove == "rock")
            {
                return -1;
            }
            else if(opponentMove == "paper")
            {
                return 1;               
            }
            else
            {
                return 0;
            }
        }
    }
    
    public void setOpponent(Player opponent)
    {
        this.opponent = opponent;
    }
    
    public Player getOpponent()
    {
        return opponent;
    }
              
    public void setGameSync(GameSynchronizer sync)
    {
        gameSync = sync;
    }
    
    public String nextMove()
    {
        return move;
    }
    
    public String getNameOfPlayer()
    {
        return nameOfPlayer;
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
