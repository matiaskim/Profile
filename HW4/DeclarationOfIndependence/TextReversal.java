/*
 * Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.Stack;

public class TextReversal 
{
    static Timestamp timeStamp;
    
    public static void main(String[] args) throws FileNotFoundException
    {
        Scanner file = new Scanner(new File("src/DeclarationOfIndependence.txt"));
        PrintWriter writer = new PrintWriter(new File("ReverseText.txt"));
        Stack<String> stackOfWords = new Stack<>();
        String line;
        Scanner scannerLine;
        
        System.out.println("Single threaded word reversal applcation\n\n");
        System.out.println(getTimeStamp() + " Starting to read file");
        while(file.hasNextLine())
        {
            line = file.nextLine().replaceAll("[^A-Za-z0-9 ]", "");
            scannerLine = new Scanner(line);
            while(scannerLine.hasNext())
            {
                stackOfWords.push(scannerLine.next() + " ");
            }
            if(file.hasNextLine())
            {
                stackOfWords.push("\n");
            }
        }
        
        while(!stackOfWords.isEmpty())
        {
            writer.write(stackOfWords.pop());          
        }
        file.close();
        writer.close();
        System.out.println(getTimeStamp() + " Finished writing file");
    }   
    
    @SuppressWarnings("deprecation")
    private static String getTimeStamp()
    {
        timeStamp = new Timestamp(System.currentTimeMillis());
        return "[" + timeStamp.getHours() + ":"
                        + timeStamp.getMinutes() + ":"
                        + timeStamp.getSeconds() + "."
                        + timeStamp.getNanos()/1000000 + "]";
    }           
    
}
