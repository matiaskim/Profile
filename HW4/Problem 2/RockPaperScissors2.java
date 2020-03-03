/*
 * Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RockPaperScissors2 
{
    public static void main(String[] args) throws InterruptedException
    {
        Scanner kb = new Scanner(System.in);
        int totalPlayers = 0;
        
        while(!powerOf2(totalPlayers) || totalPlayers < 2)
        {
            System.out.println("How many players are going to compete? ");
            totalPlayers = kb.nextInt();
            if(!powerOf2(totalPlayers))
            {
                System.out.println("Please enter a number that is a power of 2 so that everyone has an opponent!");               
            }
            if(totalPlayers == 1)
            {
                System.out.println("you need more than one player to play this game!");              
            }
        }
        kb.close();
        
        ExecutorService service = Executors.newCachedThreadPool();
        GameCreator gameCreator = new GameCreator(totalPlayers);
        
        for(int i = 0; i < totalPlayers; i++)
        {
            Player nextPlayer = new Player(("Player" + i), gameCreator);
            service.execute(nextPlayer);
        }       
    }  
    
    public static boolean powerOf2(int n)
        {
            int i = 1;
            while(i < n)
            {
                i *= 2;
            }
            return i == n;
        }
}
