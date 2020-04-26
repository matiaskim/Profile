/*
 * 
 * Author: Matias Kim
 * Assignment: CS 3700 Final Part #1
 * Due Date: April 27, 2020
 * 
 */
 
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Tester
{
	final static int totalPlayers = 3;
	final static Integer port = 0; 
	static Integer numGames;
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		try
		{
			numGames = Integer.parseInt(args[0]);
		} 
		catch (Exception e) 
		{
			System.out.println("Enter number of games to play: ");
			Scanner kb = new Scanner(System.in);
			numGames = kb.nextInt();
			kb.close();
		}
		
		try {
			Process process = new ProcessBuilder("javac", "RockPaperScissors.java").start();
			process.waitFor(10000, TimeUnit.MILLISECONDS);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	
		ArrayDeque<Integer> deque = new ArrayDeque<Integer>();
		for(int x = 0; x < totalPlayers; x++)
			deque.add(port + x);
		
		Process[] processes = new Process[totalPlayers];
		for(int x = 0; x < totalPlayers; x++)
		{
			ArrayList<String> argList = new ArrayList<String>();
			argList.add("java");
			argList.add("RockPaperScissors");
			
			for(int y = 0; y < totalPlayers; y++)
			{
				Integer port = deque.poll();
				argList.add(port.toString());
				deque.add(port);
				
			}
			argList.add(numGames.toString());
			processes[x] = new ProcessBuilder(argList).inheritIO().start();
			deque.add(deque.poll());
        }
        
		for(int x = 0; x < totalPlayers; x++)
		{
			processes[x].waitFor(10000, TimeUnit.MILLISECONDS);
		}
	}
}
