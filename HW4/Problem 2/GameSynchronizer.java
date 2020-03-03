/*
 * Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

public class GameSynchronizer 
{
    private int startReadyPlayers;
    private int stopReadyPlayers;
    
    GameSynchronizer()
    {
        startReadyPlayers = 0;
        stopReadyPlayers = 0;
    }
    
    public synchronized void readyStart()
    {
        startReadyPlayers++;
        if(startReadyPlayers < 2)
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
            startReadyPlayers = 0;
            notify();
        }
    }
    
    public synchronized void readyStop()
    {
        stopReadyPlayers++;
        if(stopReadyPlayers < 2)
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
            stopReadyPlayers = 0;
            notify();
        }
            
    }
}
