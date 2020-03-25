/*
 *
 * Author: Matias Kim
 * Assignment: Homework #5
 * Due Date: March 25, 2020
 *
 */

public class SievePrime 
{
    public static void main(String[] args)
    {
        long beginTime = System.currentTimeMillis();
        
        int n = 1000000;
        
        boolean[] primeArray = new boolean[n + 1];
        for(int x = 2; x <= n; x++)
        {
            primeArray[x] = true;
        }
        
        for(int x = 2; x * x <= n; x++)
        {
            if(primeArray[x])
            {
                for(int y = x; x * y <= n; y++)
                {
                    primeArray[x * y] = false;
                }
            }
        }
        
        for(int x = 2; x <= n; x++)
        {
            if(primeArray[x]) 
            {
                System.out.println(x);
            }
        }
        
        long finishTime = System.currentTimeMillis();
        long totalDuration = finishTime - beginTime;
        System.out.println("Total Runtime: " + totalDuration + " milliseconds.");
    }
}
