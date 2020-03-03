/*
 * Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

public class Node 
{
    private Node left;
    private Node right;
    private int rate;
    private char character;
    
    Node()
    {
        rate = -1;
        character = (char)127;
        left = null;
        right = null;
    }
    
    Node(int r, char ch)
    {
        rate = r;
        character = ch;
        left = null;
        right = null;
    }
    
    public void setRate(int r)
    {
        rate = r;
    }
    
    public int getRate()
    {
        return rate;
    }
    
    public void setCharacter(char ch)
    {
        character = ch;
    }
    
    public char getCharacter()
    {
        return character;
    }
    
    public boolean isExternal()
    {
        return (left == null && right == null);
    }
    
    public void setLeft(Node l)
    {
        left = l;       
    }
    
    public void setRight(Node r)
    {
        right = r;
    }
    
    public Node getLeft()
    {
        return left;
    }
    
    public Node getRight()
    {
        return right;
    }
}
