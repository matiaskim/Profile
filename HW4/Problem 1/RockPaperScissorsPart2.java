/*
 * Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RockPaperScissorsPart2 
{
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void main(String[] args) throws InterruptedException
    {
        Scanner kb = new Scanner(System.in);
        
        System.out.println("How many players are going to play Rock, Paper, Scissors? ");
        int totalPlayers = kb.nextInt();
        kb.close();
        
        LinkedList<PlayerPart2> players = new LinkedList<>();
        WinnerPart2 winner = new WinnerPart2(players, totalPlayers);
        ExecutorService service = Executors.newCachedThreadPool();
        ArrayList<Future> futurePlayers = new ArrayList<>();
        for(int i = 0; i < totalPlayers; i++)
        {
            PlayerPart2 nextPlayer = new PlayerPart2(("Player" + i), players, winner);
            players.add(nextPlayer);
            Future<String> future = service.submit(nextPlayer);
            futurePlayers.add(future);
        }
        Thread.sleep(100);
        Future<String> result = service.submit(winner);
        Future<String> playerInAction;
        
        for(int i = 0; i < futurePlayers.size(); i++)
        {
            playerInAction = futurePlayers.get(i);
            try
            {
                playerInAction.get();
            }
            catch(ExecutionException e)
            {
                e.printStackTrace();
            }
        }
        
        try
        {
            System.out.println(result.get() + " HAS WON!");
        }
        catch(ExecutionException e)
        {
            e.printStackTrace();
        }
    }
}
