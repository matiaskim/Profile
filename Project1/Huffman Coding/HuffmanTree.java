/*
 * Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

import java.util.PriorityQueue;
import java.util.HashMap;

public class HuffmanTree
{
    private int[] charRateArray;
    private Node root;
    private StringBuilder tree;
    private PriorityQueue<Node> ratePQ;
    private HashMap<Character, String> mapOfCompression;
    
    public HuffmanTree(int[] rateArray)
    {
        charRateArray = rateArray;
        mapOfCompression = new HashMap<>();
        ratePQ = new PriorityQueue<>(256, new NodeComparator());
        root = createTree();
        mapBuildTraversal(root, "", mapOfCompression);
        tree = new StringBuilder();
    }
    
    public void printCharacterCount()
    {
        System.out.println(root.getRate());
    }
    
    private Node createTree()
    {
        int rate;
        for(int i = 0; i < charRateArray.length; i++)
        {
            rate = charRateArray[i];
            if(rate > 0)
            {
                ratePQ.add(new Node(rate, (char)i));
            }
        }
        
        while(ratePQ.size() > 1)
        {
            Node left = ratePQ.poll();
            Node right = ratePQ.poll();
            
            Node par = new Node();
            par.setRate(left.getRate() + right.getRate());
            par.setLeft(left);
            par.setRight(right);
            ratePQ.add(par);
        }
        return ratePQ.poll();
    }
    
    private void mapBuildTraversal(Node root, String line, HashMap<Character, String> map)
    {
        if(!root.isExternal())
        {
            mapBuildTraversal(root.getLeft(), line + "0", map);
            mapBuildTraversal(root.getRight(), line + "1", map);
        }
        else
        {
            map.put(root.getCharacter(), line);
        }
    }
    
    public HashMap<Character, String> getCodeMap()
    {
        return mapOfCompression;
    }
    
    private void getTree(Node node)           
    {
        String line = Integer.toBinaryString(node.getCharacter());
        if(node.isExternal())
        {
            tree.append('1');
            for(int i = 8 - line.length(); i > 0; i--)
            {
                tree.append('0');
            }
            tree.append(line);
            return;
        }
        tree.append('0');
        getTree(node.getLeft());
        getTree(node.getRight());
    }
    
    public StringBuilder treeString()
    {
        getTree(root);
        StringBuilder sizeOfMap = new StringBuilder();
        String binarySize = Integer.toBinaryString(mapOfCompression.size());
        for(int i = binarySize.length() - 8; i > 0; i--)
        {
            sizeOfMap.append('0');
        }
        sizeOfMap.append(binarySize);
        sizeOfMap.append(tree);
        return sizeOfMap;
    }
}
