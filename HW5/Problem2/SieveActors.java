/*
 *
 * Author: Matias Kim
 * Assignment: Homework #5
 * Due Date: March 25, 2020
 *
 */

import akka.actor.UntypedAbstractActor;
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.PoisonPill;
      
public class SieveActors 
{
    private static ActorSystem system = ActorSystem.create("Sieve-of-Eratosthenes-System");
    private static int primeCount = 0;
    private static final Object terminationLock = new Object();
    
    private static class Sieve extends UntypedAbstractActor
    {
        public static Props props(int localPrime)
        {
            return Props.create(Sieve.class, () -> new Sieve(localPrime));
        }
        
        private final int localPrime;
        private ActorRef nextSieve;
        
        Sieve(int localPrime)
        {
            this.localPrime = localPrime;
            System.out.println(localPrime + " ");
            primeCount++;
        }
        
        @Override
        public void onReceive(Object msg) throws Exception
        {
            if(msg instanceof Integer)
            {
                int candidate = (Integer)msg;
                if(candidate % localPrime > 0)
                {
                    if(nextSieve == null)
                    {
                        nextSieve = system.actorOf(Sieve.props(candidate), "Sieve-Actor-" + candidate);                  
                    }
                    else
                    {
                        nextSieve.tell(msg, getSelf());
                    }
                }
                else if(nextSieve == null)
                {
                    synchronized(terminationLock)
                    {
                        terminationLock.notify();
                    }         
                }
                else
                {
                    nextSieve.tell("", ActorRef.noSender());
                    getSelf().tell(PoisonPill.getInstance(), ActorRef.noSender());
                }
            }
        }
        
        private static long printPrimes(int n)
        {
            long startTime = System.currentTimeMillis();
            
            System.out.println("1 ");
            
            if(n > 1)
            {
                ActorRef initSieve = system.actorOf(Sieve.props(2), "Sieve-Actor-2");
                synchronized(terminationLock)
                {
                    for(int i = 3; i <= n; i++)
                    {
                        initSieve.tell(i, ActorRef.noSender());
                    }
                    initSieve.tell("", ActorRef.noSender());
                    
                    try
                    {
                        terminationLock.wait(300000);
                    }
                    catch(InterruptedException e)
                    {
                        System.out.println();
                    }
                    system.terminate();
                }
            }
            
            long endTime = System.currentTimeMillis();
            return endTime - startTime;
        }
        
        public static void main(String[] args)
        {
            int n = 1000000;
            System.out.println("Prime numbers up to " + n + ": ");
            long t = printPrimes(n);
            System.out.println("\n" + t+ " ms. -> " + primeCount);
        }
    }
}
