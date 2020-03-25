/*
 *
 * Author: Matias Kim
 * Assignment: Homework #5
 * Due Date: March 25, 2020
 *
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Atomics 
{
    static class AtomicNode
    {
        AtomicNode(int data)
        {
            this.data = data;
            this.next = new AtomicReference<AtomicNode>(null);
        }
        public int data;
        public AtomicReference<AtomicNode> next;
    }
    
    private static final int PRODUCERS = 2;
    private static final int CONSUMERS = 5;
    private static final int BUFFERMAX = 10;
    private static final int TOTALPRODUCTION = 100;
    private static final int CONSUMERSLEEPMILLIS = 1000;
    
    private static AtomicInteger count = new AtomicInteger(0);
    public static void main(String[] args)
    {
        long beginTime = System.currentTimeMillis();
        final AtomicInteger activeProducers = new AtomicInteger(PRODUCERS);
        AtomicReference<AtomicNode> bufferBegin = new AtomicReference<AtomicNode>();
        AtomicReference<AtomicNode> bufferFinish = new AtomicReference<AtomicNode>();
        AtomicInteger bufferSize = new AtomicInteger(0);
        for(int x = 0; x < PRODUCERS; x++)
        {
            new Thread(() -> {
                while(true)
                {
                    if(count.get() >= TOTALPRODUCTION)
                    {
                        activeProducers.decrementAndGet();
                        return;
                    }
                    int size = bufferSize.get();
                    if(size < BUFFERMAX)
                    {
                        if(bufferSize.compareAndSet(size, size + 1))
                        {
                            AtomicNode node = new AtomicNode(count.getAndIncrement());
                            
                            while(true)
                            {
                                AtomicNode finish = bufferFinish.get();
                                if((finish == null || finish.next.compareAndSet(null, node)) && bufferFinish.compareAndSet(finish, node))
                                {
                                    bufferBegin.compareAndSet(null,finish);
                                    break;
                                }
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
                    AtomicNode begin = bufferBegin.get();
                    if(begin != null)
                    {
                        if(bufferBegin.compareAndSet(begin, begin.next.get()))
                        {
                            bufferSize.getAndDecrement();
                            System.out.println("CONSUMED: " + begin.data);
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
                    else if(activeProducers.get() == 0)
                    {
                        System.out.println("RUNTIME: " + (System.currentTimeMillis() - beginTime));
                        return;
                    }
                }
            }).start();
        }
    }
}
