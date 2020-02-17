import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class SockMatching 
{
    private String[] colors = {"Red", "Green", "Blue", "Orange"};
    private static Stack<Sock> destroyed = new Stack<>();
    private static int socksDone = 0;
    private static int totalNumberOfSocks = 0;
    
    public static void main(String[] args) throws InterruptedException
    {
        Phaser phase1 = new Phaser();
        Phaser phase2 = new Phaser(1);
        Phaser phase3 = new Phaser();
        final int numThreads = 4;
        
        ArrayList<Sock> redSocks = new ArrayList<>();
        ArrayList<Sock> greenSocks = new ArrayList<>();
        ArrayList<Sock> blueSocks = new ArrayList<>();
        ArrayList<Sock> orangeSocks = new ArrayList<>();
        ArrayList<ArrayList<Sock>> socksList = new ArrayList<>(Arrays.asList(
                redSocks,
                greenSocks,
                blueSocks,
                orangeSocks
        ));
        
        for(int i = 0; i < numThreads; i++)
        {
            new SockMatching().generatingSocksThread(phase1, phase2, phase3, socksList.get(i), i);            
        }
        
        new SockMatching().matchingSocksThread(phase1, phase2, phase3, socksList);
        
        phase2.arriveAndAwaitAdvance();
        System.out.println("Red Socks Remaining: " + redSocks.size());
        System.out.println("Green Socks Remaining: " + greenSocks.size());
        System.out.println("Blue Socks Remaining: " + blueSocks.size());
        System.out.println("Orange Socks Remaining: " + orangeSocks.size());
        System.out.println("Total Inside Queue: 0");        
    }
    
    private void generatingSocksThread(Phaser phase1, Phaser phase2, Phaser phase3, ArrayList<Sock> socks, int color)
    {
        phase1.register();
        
        new Thread(new Runnable() 
        {
            @Override
            public void run()
            {
                int socksToBeGenerated = ThreadLocalRandom.current().nextInt(0, 100) + 1;
                
                for(int i = 0; i < socksToBeGenerated; i++)
                {
                    socks.add(new Sock(colors[color]));
                    socksDone++;
                    System.out.println(colors[color] + " Sock: Produced " + (i + 1) + " out of " + socksToBeGenerated + " " + colors[color] + " socks.");
                    if(socks.size() >= 2)
                    {
                        phase1.arrive();
                        try
                        {
                            Thread.sleep(100);
                        }
                        catch(InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        phase3.awaitAdvance(phase3.getPhase());
                    }
                }
                phase1.arriveAndDeregister();     
            }
        }).start();
    }
    
    private void matchingSocksThread(Phaser phase1, Phaser phase2, Phaser phase3, ArrayList<ArrayList<Sock>> socks)
    {
        phase2.register();
        
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                boolean matching = true;
                while(matching)
                {
                    matching = false;
                    phase1.awaitAdvance(phase1.getPhase());
                    
                    for(int i = 0; i < 4; i++)
                    {
                        if(socks.get(i).size() >= 2)
                        {
                            matching = true;
                            destroyed.push(socks.get(i).remove(0));
                            System.out.println("Matching Thread: Send "+ colors[i] + " Socks to Washer. Total socks in existance " + (socks.get(0).size() + socks.get(1).size() + socks.get(2).size() + socks.get(3).size() - 1) + ". Total inside queue " + (destroyed.size() - 1));
                            socks.get(i).remove(0);
                            
                            if(destroyed.size() >= 2)
                            {
                                try
                                {
                                    Thread.sleep(100);
                                }
                                catch(InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                                
                                new SockMatching().washingSocksThread(phase1, phase2, phase3, i);
                                
                                phase3.awaitAdvance(phase3.getPhase());
                            }
                        }
                    }
                    if(socks.get(0).size() < 2 && socks.get(1).size() < 2 && socks.get(2).size() < 2 && socks.get(3).size() < 2)
                    {
                        matching = false;
                    }
                }
                phase2.arriveAndDeregister();
            }
        }).start();
    }
    
    private void washingSocksThread(Phaser phase1, Phaser phase2, Phaser phase3, int color)
    {
        phase3.register();
        
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if(destroyed.size() > 0)
                {
                    destroyed.pop();
                    System.out.println("Washing Thread: Destroyed " + colors[color] + " socks");               
                }
                
                phase3.arriveAndDeregister();
            }
        }).start();
    }
}

