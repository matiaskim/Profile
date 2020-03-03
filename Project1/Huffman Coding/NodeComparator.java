/*
 * Author: Matias Kim
 * Assignment: CS 3700 HW #3
 * Due Date: 03/02/2020
 */

import java.util.Comparator;

public class NodeComparator implements Comparator<Node>
{
    public int compare(Node n1, Node n2)
    {
        if(n1.getRate() > n2.getRate())
        {
            return 1;
        }
        else if(n1.getRate() == n2.getRate())
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }
}
