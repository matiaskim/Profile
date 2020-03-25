/*
 *
 * Author: Matias Kim
 * Assignment: Homework #5
 * Due Date: March 25, 2020
 *
 */

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import akka.actor.*;

public class Actors 
{
    static long finishTime;
    
    static class Begin {};
    
    public static void main(String[] args) throws IOException
    {
        final ActorSystem system = ActorSystem.create("PC");
        final int CAPACITY = 10;
        final int PRODUCERS = 5;
        final int CONSUMERS = 2;
        ActorRef[] producerActors = new ActorRef[PRODUCERS];
        ActorRef[] consumerActors = new ActorRef[CONSUMERS];
        final BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(10);
        
        long beginTime = System.currentTimeMillis();
        
        ActorRef buffer = system.actorOf(BufferActor.props(CAPACITY, PRODUCERS, CONSUMERS, consumerActors));
        
        for(int x = 0; x < PRODUCERS; x++)
        {
            producerActors[x] = system.actorOf(ProducerActor.props(blockingQueue, buffer));
        }
        
        for(int x = 0; x < CONSUMERS; x++)
        {
            consumerActors[x] = system.actorOf(ConsumerActor.props(blockingQueue, buffer));
        }
        
        buffer.tell(new Begin(), ActorRef.noSender());
        
        System.in.read();
        long totalDuration = finishTime - beginTime;
        System.out.println("Total Runtime: " + totalDuration + " milliseconds.");
    }
}
