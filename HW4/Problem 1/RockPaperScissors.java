/*
 * Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

import java.util.Scanner;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RockPaperScissors 
{
    public static void main(String[] args) throws InterruptedException
    {
        Scanner kb = new Scanner(System.in);
        
        System.out.println("How many players are going to play Rock, Paper, Scissors? ");
        int totalPlayers = kb.nextInt();
        kb.close();
        
        LinkedList<Player> players = new LinkedList<>();
        Winner winner = new Winner(players, totalPlayers);
        ExecutorService service = Executors.newCachedThreadPool();
        
        for(int i = 0; i < totalPlayers; i++)
        {
            Player nextPlayer = new Player(("Player" + i), players, winner);
            players.add(nextPlayer);
            service.execute(nextPlayer);
        }
        Thread.sleep(100);
        service.execute(winner);
    }
}
