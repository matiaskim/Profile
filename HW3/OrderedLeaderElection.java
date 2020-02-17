import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class OrderedLeaderElection 
{
    private static int[] ranks;
    private static int max = Integer.MIN_VALUE;
    private static String leaderName;
    
    public static void main(String[] args) throws InterruptedException
    {
        Phaser phase = new Phaser();
        
        System.out.println("Creating Rank Thread");
        
        int n = (int)((Math.random() * 90) + 10);
        ranks = new int[n];
        
        for(int i = 0; i < n; i++)
        {
            new OrderedLeaderElection().officialThread(phase);
            Thread.sleep(1000);
        }
    }
    
    private void officialThread(Phaser phase)
    {
        phase.register();
        
        new Thread(() -> 
        {
            String name = Thread.currentThread().getName().substring(7);
            ranks[Integer.parseInt(name)] = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            
            if(ranks[Integer.parseInt(name)] > max)
            {
                max = ranks[Integer.parseInt(name)];
                leaderName = name;
                System.out.println("\n~~Changing Leader~~\n");
            }
            
            System.out.println("Name: " + Thread.currentThread().getName() + " Rank: " + ranks[Integer.parseInt(name)] + " Leader: " + leaderName);
        }).start();
    }
    
    private void rankThread(Phaser phase)
    {
        new Thread(() -> {
            
        }).start();
    }
}
