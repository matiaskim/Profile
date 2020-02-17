import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

public class MatrixMultiplication
{
    public static void main(String[] args) throws InterruptedException
    {
        Phaser phase = new Phaser();
        float[][] A, B, C;
        int m, n, p;
        
        for(int i = 0; i < 4; i++)
        {
            m = (int) ((Math.random() * 200) + 801);
            n = (int) ((Math.random() * 200) + 801);
            p = (int) ((Math.random() * 200) + 801);
            
            System.out.println("Matrix A: " + m + " by " + n);
            System.out.println("Matrix B: " + n + " by " + p);
            System.out.println("Matrix C: " + m + " by " + n);
            
            A = fillMatrix(m, n);
            B = fillMatrix(n, p);
            
            long start, end;
            int sleepTime = 2000;
            
            C = new float[m][p];
            start = System.currentTimeMillis();
            
            new MatrixMultiplication().matrixMultiplication(phase, A, B, C, 0, m, n, 0, p);
            
            phase.awaitAdvance(phase.getPhase());
            Thread.sleep(sleepTime);
            end = System.currentTimeMillis() - start;
            System.out.println("Time for 1 Threads: " + end + " milliseconds");
            
            C = new float[m][p];
            start = System.currentTimeMillis();
            
            new MatrixMultiplication().matrixMultiplication(phase, A, B, C, 0, m, n, 0, p / 2);
            new MatrixMultiplication().matrixMultiplication(phase, A, B, C, 0, m, n, p / 2, p);
            
            phase.awaitAdvance(phase.getPhase());
            Thread.sleep(sleepTime);
            end = System.currentTimeMillis() - start;
            System.out.println("Time for 2 Threads: " + end + " milliseconds");
            
            C = new float[m][p];
            start = System.currentTimeMillis();
            
            new MatrixMultiplication().matrixMultiplication(phase, A, B, C, 0, m / 2, n, 0, p / 2);
            new MatrixMultiplication().matrixMultiplication(phase, A, B, C, 0, m / 2, n, p / 2, p);
            new MatrixMultiplication().matrixMultiplication(phase, A, B, C, m / 2, m, n, 0, p / 2);
            new MatrixMultiplication().matrixMultiplication(phase, A, B, C, m / 2, m, n, p / 2, p);
            
            phase.awaitAdvance(phase.getPhase());
            Thread.sleep(sleepTime);
            end = System.currentTimeMillis() - start;
            System.out.println("Time for 4 Threads: " + end + " milliseconds");
            
            C = new float[m][p];
            start = System.currentTimeMillis();
            
            new MatrixMultiplication().matrixMultiplication(phase, A, B, C, 0, m / 4, n, 0, p / 2);
            new MatrixMultiplication().matrixMultiplication(phase, A, B, C, m / 4, m / 2, n, 0, p / 2);
            new MatrixMultiplication().matrixMultiplication(phase, A, B, C, m / 4, m / 2, n, p / 2, p);
            new MatrixMultiplication().matrixMultiplication(phase, A, B, C, 0, m / 4, n, p / 2, p);
            new MatrixMultiplication().matrixMultiplication(phase, A, B, C, m / 2, m * 3 / 4, n, 0, p / 2);
            new MatrixMultiplication().matrixMultiplication(phase, A, B, C, m / 2, m * 3 / 4, n, p / 2, p);
            new MatrixMultiplication().matrixMultiplication(phase, A, B, C, m * 3 / 4, m, n, 0, p / 2);
            new MatrixMultiplication().matrixMultiplication(phase, A, B, C, m / 2, m, n, p / 2, p);
            
            phase.awaitAdvance(phase.getPhase());
            Thread.sleep(sleepTime);
            end = System.currentTimeMillis() - start;
            System.out.println("Time for 8 Threads: " + end + " milliseconds");
            
        }
    }
    
    private void matrixMultiplication(Phaser phase, float[][] A, float[][] B, float[][] C, int m1, int m2, int n, int p1, int p2)
    {
        phase.register();
        
        new Thread(() -> {
            for(int row = m1; row < m2; row++)
            {
                for(int col = p1; col < p2; col++)
                {
                    C[row][col] = 0;
                    for(int k = 0; k < n; k++)
                    {
                        C[row][col] += A[row][k] * B[k][col];
                    }
                }
            }
            phase.arriveAndDeregister();
        }).start();
    }
    
    private static float[][] fillMatrix(int row, int col)
    {
        float[][] temp = new float[row][col];
        
        for(int i = 0; i < temp.length; i++)
        {
            for(int j = 0; j < temp[0].length; j++)
            {
                temp[i][j] = ThreadLocalRandom.current().nextFloat();
            }
        }          
        return temp;
    }
}
