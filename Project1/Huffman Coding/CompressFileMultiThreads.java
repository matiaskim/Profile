/*
 * Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

import java.util.Scanner;
import java.util.LinkedList;
import java.util.HashMap;
import java.sql.Timestamp;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CompressFileMultiThreads 
{
    static Timestamp timeStamp;
    
    public static void main(String[] args) throws FileNotFoundException
    {
        RateCounter counter;
        long treeBuildingDuration = 0;
        long compressFileDuration = 0;
        long beginningTime;
        long endTime;
        int numOfTrials = 1000;
        int numOfRateThreads = 2;
        int numOfEncodedThreads = 8;
        int blockSize;
        int startIndex;
        int finishIndex;
        
        HuffmanTree tree;
        HashMap<Character, String> mapOfCode;
        
        Thread[] threads;
        LinkedList<Future<StringBuilder>> futures;
        ExecutorService service;
        LineEncodingProcessor task;
        LineRateProcessor thread;
        
        Scanner kb;
        StringBuilder line; 
        StringBuilder textCompressed;
        PrintWriter writer;
        
        String newString;
        
        for(int i = 0; i < numOfTrials; i++)
        {
            kb = new Scanner(new File("src/USConstitution.txt"));
            writer = new PrintWriter(new File("USConstitutionCompressed.txt"));
            counter = new RateCounter();
            threads = new Thread[numOfRateThreads];
            line = new StringBuilder();
            textCompressed = new StringBuilder();
            
            futures = new LinkedList<>();
            service = Executors.newFixedThreadPool(8);
        
            while(kb.hasNextLine())
            {
                line.append(kb.nextLine() + '\n');             
            }
        
            blockSize = line.length()/numOfRateThreads;
            startIndex = 0;
            finishIndex = blockSize;
            
            System.out.println("Trial #" + (i+1));
            System.out.println(getTimeStamp() + " Starting to build the tree.");
            beginningTime = System.nanoTime();

            for(int j = 0; j < numOfRateThreads; j++)
            {
                thread = new LineRateProcessor(counter, line, startIndex, finishIndex);
                thread.start();
                threads[j] = thread;;
                startIndex += blockSize;
                finishIndex = startIndex + blockSize;
                if(j == numOfRateThreads - 1)
                {
                    finishIndex = line.length();
                }
            }
            
            for(int y = 0; y < numOfRateThreads; y++)
            {
                try
                {
                    threads[y].join();
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            
            tree = new HuffmanTree(counter.getRates());
            endTime = System.nanoTime();
            treeBuildingDuration += (beginningTime - endTime);

            System.out.println(getTimeStamp() + " Finished building the tree.");
            
            System.out.println(getTimeStamp() + " Starting compressed file.");
            beginningTime = System.nanoTime();
            
            mapOfCode = tree.getCodeMap();
            
            blockSize = line.length()/numOfEncodedThreads;
            startIndex = 0;
            finishIndex = blockSize;
            
            for(int j = 0; j < numOfEncodedThreads; j++)
            {
                task = new LineEncodingProcessor(mapOfCode, line, startIndex, finishIndex);
                futures.add(service.submit(task));
                startIndex += blockSize;
                finishIndex = startIndex + blockSize;
                if(j == numOfEncodedThreads - 1)
                {
                    finishIndex = line.length();
                }
            }
            
            for(int j = 0; j < numOfEncodedThreads; j++)
            {
                try
                {
                    textCompressed.append(futures.get(j).get());
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
            
            newString = tree.treeString().toString();
            
            endTime = System.nanoTime();
            System.out.println(getTimeStamp() + " Finished compressing file.\n");
            
            compressFileDuration += (endTime - beginningTime);
            
            writer.write(newString);
            writer.write(textCompressed.toString());
            kb.close();
            writer.close();
            service.shutdownNow();
        }
        
        System.out.println("Average Tree Building Time: " + (treeBuildingDuration/numOfTrials) + " ns");
        System.out.println("Average File Compression Time: " + (compressFileDuration/numOfTrials) + " ns");
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

class RateCounter
{
    private int[] rateArray;
    private boolean usage;
    int variable;
    
    RateCounter()
    {
        rateArray = new int[256];
        usage = false;
    }
    
    public synchronized void updateRates(int[] rates)
    {
        while(usage)
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
        usage = true;
        for(int i = 0; i < 256; i++)
        {
            variable = rateArray[i];
            rateArray[i] = rates[i] + variable;
        }
        usage = false;
        notifyAll();
    }
    
    public synchronized int[] getRates()
    {
        return rateArray;
    }
}

class LineRateProcessor extends Thread
{
    private int[] tempRates;
    private RateCounter counter;
    private StringBuilder block;
    private int start;
    private int finish;
    
    LineRateProcessor(RateCounter counter, StringBuilder line, int start, int finish)
    {
        block = line;
        this.counter = counter;
        tempRates = new int[256];
        this.start = start;
        this.finish = finish;
    }
    
    public void run()
    {
        for(int i = start; i < finish; i++)
        {
            tempRates[block.charAt(i)]++;
        }
        counter.updateRates(tempRates);
    }
}

class LineEncodingProcessor implements Callable<StringBuilder>
{
    private HashMap<Character, String> map;
    private StringBuilder block;
    private StringBuilder encodedBlock;
    private int start;
    private int finish;
    
    LineEncodingProcessor(HashMap<Character, String> map, StringBuilder line, int start, int finish)
    {
        block = line;
        this.map = map;
        this.start = start;
        this.finish = finish;
        encodedBlock = new StringBuilder();
    }
    
    public StringBuilder call()
    {
        for(int i = start; i < finish; i++)
        {
            encodedBlock.append(map.get(block.charAt(i)));
        }
        return encodedBlock;
    }
}
