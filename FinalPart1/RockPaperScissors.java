/*
 * 
 * Author: Matias Kim
 * Assignment: CS 3700 Final Part #1
 * Due Date: April 27, 2020
 * 
 */

import java.io.*;
import java.net.*;
import java.util.Random;

public class RockPaperScissors 
{
	private final static String HOST = "127.0.0.1";
	static int pNumber;
	static int[] gameArray;
	
	public static void main(String args[])
	{
		pNumber =  Integer.parseInt(args[0]) - Tester.port + 1;
		
		try 
		{
			ServerSocket s = new ServerSocket(Integer.parseInt(args[0]), 2);

			Socket p2 = new Socket(HOST, Integer.parseInt(args[1]));
			Socket p3 = new Socket(HOST, Integer.parseInt(args[2]));
			
			ObjectOutputStream player2Output = new ObjectOutputStream(p2.getOutputStream());
			ObjectOutputStream player3Output = new ObjectOutputStream(p3.getOutputStream());
			ObjectInputStream player2Input = new ObjectInputStream(s.accept().getInputStream());
			ObjectInputStream player3Input = new ObjectInputStream(s.accept().getInputStream());
			
			gameArray = playGames(Integer.parseInt(args[3]));
			
			player2Output.writeObject(gameArray);
			player3Output.writeObject(gameArray);

			int[] player2 = (int[]) player2Input.readObject();
			int[] player3 = (int[]) player3Input.readObject();

			int totalScore = getTotalScore(player2, player3);
			System.out.println("Player " + pNumber + " Score: " + totalScore);

			s.close();
			p2.close();
			p3.close();
			
		} 
		catch (ClassNotFoundException | NumberFormatException | IOException e) 
		{
			System.out.println("Player " + pNumber +" Error: " + e.getMessage());
			e.printStackTrace();
		} 
	}
	
	private static int[] playGames(int numGames) 
	{
		Random r = new Random();
		int[] gameArray = new int[numGames];
		for(int x = 0; x < numGames; x++)
		{
			gameArray[x] = r.nextInt(3);
		}
		return gameArray;
	}

	private static int getTotalScore(int[] player2, int[] player3)
	{
		int totalScore = 0;
		for(int x = 0; x < gameArray.length; x++)
		{
			totalScore += getScore(gameArray[x], player2[x], player3[x]);
		}
		return totalScore;
	}

	private static int getScore(int currentGame, int game1, int game2)
	{
		if(checkWin(currentGame, game1))
		{
			if(checkWin(currentGame, game2))
				return 2;

			if (checkWin(game2, game1))
				return 1;
		}
		else if(checkWin(currentGame, game2))
		{
			if(checkWin(game1, game2))
				return 1;
		}
		return 0;
	}

	private static boolean checkWin(int player1, int others)
	{
		switch (player1) 
		{
			case 0: 
				return others == 2;
			case 1:
				return others == 0;
			case 2:
				return others == 1;
			default:
				throw new IllegalArgumentException("Unexpected value: " + player1);
		}
	}
}
