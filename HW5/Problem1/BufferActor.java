/*
 * Author: Matias Kim
 * Assignment: Homework #5
 * Due Date: March 25, 2020
 *
 */

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BufferActor extends AbstractActor
{
    static public Props props(int CAPACITY, int pNums, int cNums, ActorRef[] consumerActor)
    {
        return Props.create(BufferActor.class, () -> new BufferActor(CAPACITY, pNums, cNums, consumerActor));
    }
    
    static class RequestItems {}
    static class AllFinished {}
    
    final BlockingQueue<Integer> buffer;
    final int pNums, cNums;
    int count;
    ActorRef[] consumerActor;
    
    public BufferActor(int CAPACITY, int pNums, int cNums, ActorRef[] consumerActor)
    {
        this.buffer = new ArrayBlockingQueue<>(CAPACITY);
        this.pNums = pNums;
        this.cNums = cNums;
        this.consumerActor = consumerActor;
    }
    
    @Override 
    public Receive createReceive()
    {
        return receiveBuilder()
                .match(Actors.Begin.class, b -> {
        
        }).match(ProducerActor.Done.class, foo -> {
            count++;
            if(count == pNums)
            {
                for(ActorRef actorRef: consumerActor)
                {
                    actorRef.tell(new AllFinished(), ActorRef.noSender());
                }
            }
        }).build();
                
    }
}
