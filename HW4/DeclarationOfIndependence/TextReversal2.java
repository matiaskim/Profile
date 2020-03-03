/*
 * Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class LineReverser implements Callable<String>
{
    private String unprocessedLine;
    private Stack<String> stackOfWords;
    private Scanner scannerLine;
    private StringBuilder processedLine;
    
    LineReverser(String line)
    {
        this.unprocessedLine = line;
        stackOfWords = new Stack<>();
        processedLine = new StringBuilder();
    }
    
    public String call()
    {
        unprocessedLine = unprocessedLine.replaceAll("[^A-Za-z0-9 ]", "");
        scannerLine = new Scanner(unprocessedLine);
        while(scannerLine.hasNext())
        {
            stackOfWords.push(scannerLine.next());
        }
        while(!stackOfWords.isEmpty())
        {
            processedLine.append(stackOfWords.pop() + ' ');
        }
        processedLine.append('\n');
        return processedLine.toString();
    }
}

public class TextReversal2 
{
    static Timestamp timeStamp;
    
    public static void main(String[] args) throws FileNotFoundException
    {
        PrintWriter writer;
        LinkedList<Future<String>> futures;
        
        Scanner file;
        
        for(int i = 0; i < 4; i++)
        {
            writer = new PrintWriter(new File("TextReversal.txt"));
            file = new Scanner(new File("src/DeclarationOfIndependence.txt"));
            
            int numOfThreads = (int)(Math.pow(2, i) + .5);
            futures = new LinkedList<>();
            ExecutorService service = Executors.newFixedThreadPool(numOfThreads);
            
            System.out.println("Processing with " + numOfThreads + " threads\n");
            
            String line;
            System.out.println(getTimeStamp() + " Starting to read file");
            
            while(file.hasNextLine())
            {
                line = file.nextLine();
                futures.add(service.submit(new LineReverser(line)));
            }
            
            for(int j = futures.size() - 1; j> -1; j--)
            {
                try
                {
                    writer.write(futures.get(j).get());
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch(ExecutionException e)
                {
                    e.printStackTrace();
                }
            }
            file.close();
            writer.close();
            System.out.println(getTimeStamp() + " Finished writing file\n\n");
        }
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
