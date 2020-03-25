/*
 *
 * Author: Matias Kim
 * Assignment: Homework #5
 * Due Date: March 25, 2020
 *
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Locks 
{
    private static final int PRODUCERS = 2;
    private static final int CONSUMERS = 5;
    private static final int BUFFERMAX = 10;
    private static final int TOTALPRODUCTION = 100;
    private static final int CONSUMERSLEEPMILLIS = 1000;
    
    private static int count = 0;    
    public static void main(String[] args) 
    {
        long beginTime = System.currentTimeMillis();
        final AtomicInteger activeProducers = new AtomicInteger(PRODUCERS);
        LinkedList<Integer> bufferList = new LinkedList<Integer>();
        for(int x = 0; x < PRODUCERS; x++)
        {
            new Thread(() -> {
                while(true)
                {
                    synchronized(bufferList)
                    {
                        if(count >= TOTALPRODUCTION)
                        {
                            activeProducers.decrementAndGet();
                            return;
                        }
                        if(bufferList.size() < BUFFERMAX)
                        {
                            bufferList.add(count++);
                            bufferList.notifyAll();
                        }
                        else
                        {
                            try
                            {
                                bufferList.wait(1000);
                            }
                            catch(InterruptedException e)
                            {
                                
                            }
                        }
                    }
                }
            }).start();
        }
        
        for(int x = 0; x < CONSUMERS; x++)
        {
            new Thread(() -> {
                while(true)
                {
                    boolean consumed = false;
                    synchronized(bufferList)
                    {
                        if(bufferList.size() > 0)
                        {
                            System.out.println("GOT LAST: " + bufferList.removeFirst());
                            consumed = true;
                            bufferList.notifyAll();
                        }
                        else
                        {
                            if(activeProducers.get() == 0)
                            {
                                System.out.println("RUNTIME: " + (System.currentTimeMillis() - beginTime));
                                return;
                            }
                            try
                            {
                                bufferList.wait(1000);
                            }
                            catch(InterruptedException e)
                            {
                                
                            }
                        }
                    }
                    
                    if(consumed)
                    {
                        try
                        {
                            Thread.sleep(CONSUMERSLEEPMILLIS);
                        }
                        catch(InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
    
}
