/*
 * Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

import java.util.Scanner;
import java.util.HashMap;
import java.sql.Timestamp;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.File;

public class CompressFile 
{
    static Timestamp timeStamp;
    
    public static void main(String[] args) throws FileNotFoundException
    {
        int[] newArray;
        long treeBuildingDuration = 0;
        long compressFileDuration = 0;
        long beginningTime;
        long endTime;
        int numOfTrials = 1000;
        
        Scanner kb;
        StringBuilder line; 
        StringBuilder textCompressed;
        PrintWriter writer;
        
        HuffmanTree tree;
        HashMap<Character, String> mapOfCode;
        
        String newString;
        
        for(int i = 0; i < numOfTrials; i++)
        {
            kb = new Scanner(new File("src/USConstitution.txt"));
            line = new StringBuilder();
            textCompressed = new StringBuilder();
            writer = new PrintWriter(new File("USConstitutionCompressed.txt"));
            newArray = new int[256];
        
        
            while(kb.hasNextLine())
            {
                line.append(kb.nextLine());
                if(kb.hasNextLine())
                {
                    line.append('\n');
                }                     
            }
        
            System.out.println("Trial #" + (i+1));
            System.out.println(getTimeStamp() + " Starting to build the tree.");
            beginningTime = System.nanoTime();

            for(int x = 0; x < line.length(); x++)
            {
                newArray[line.charAt(i)]++;
            }

            tree = new HuffmanTree(newArray);

            endTime = System.nanoTime();
            treeBuildingDuration += (endTime - beginningTime);
            System.out.println(getTimeStamp() + " Finished building the tree.");

            System.out.println(getTimeStamp() + " Starting to encode the file.");
            beginningTime = System.nanoTime();

            mapOfCode = tree.getCodeMap();

            for(int j = 0; j < line.length(); j++)
            {
                textCompressed.append(mapOfCode.get(line.charAt(i)));
            }

            newString = tree.treeString().toString();

            endTime = System.nanoTime();
            System.out.println(getTimeStamp() + " Finished encoding file.\n");
            compressFileDuration += (endTime - beginningTime);

            writer.write(newString);
            writer.write(textCompressed.toString());

            kb.close();
            writer.close();
        }
        
        System.out.println("Average Tree Building Time: " + (treeBuildingDuration/numOfTrials) + " ns");
        System.out.println("Average File Compression Time: " + (compressFileDuration/numOfTrials) + " ns");
    }
       
    @SuppressWarnings("deprecation")
    private static String getTimeStamp()
    {
        timeStamp = new Timestamp(System.currentTimeMillis());
        return "[" + timeStamp.getHours() + ":"
                        + timeStamp.getMinutes() + ":"
                        + timeStamp.getSeconds() + "."
                        + timeStamp.getNanos()/1000000 + "]";
    }    
}
