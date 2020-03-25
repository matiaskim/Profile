/*
 *
 * Author: Matias Kim
 * Assignment: Homework #5
 * Due Date: March 25, 2020
 *
 */

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Isolated 
{
    public static void main(String[] args)
    {
        final int PRODUCERS = 2;
        final int CONSUMERS = 5;
        final BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(10);
        
        Runnable producer = () -> {
            try
            {
                for(int i = 0; i < 100; i++)
                {
                    blockingQueue.put(i);
                    System.out.println(Thread.currentThread().getName() + " Produced " + i);
                }               
            }
            catch(InterruptedException ignored)
            {               
            }
        };
        Runnable consumer = () -> {
            try{
                while(true) 
                {
                    Integer elementQueue = blockingQueue.poll(1, TimeUnit.SECONDS);
                    if(elementQueue != null)
                    {
                        System.out.println(Thread.currentThread().getName() + " Consumed : " + elementQueue);
                        Thread.sleep(1000);
                    }
                    else if(blockingQueue.peek() == null)
                    {
                        return;
                    }
                }
            } catch(InterruptedException ignored)
            {             
            }
        };
        
        long beginTime = System.currentTimeMillis();
        
        for(int x = 0; x < PRODUCERS; x++)
        {
            new Thread(producer).start();
        }
        for(int x = 0; x < CONSUMERS; x++)
        {
            new Thread(consumer).start();
        }
        
        long finishTime = System.currentTimeMillis();
        long totalDuration = finishTime - beginTime;
        System.out.println("Total Runtime: " + totalDuration + " milliseconds.");
    }
}
