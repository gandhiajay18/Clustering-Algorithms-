import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ajay-Pc
 * 
 * This class is used to compute the Jaccard and Rand co-efficients for a given
 * list of GroundTruth and the Output result list
 * 
 */
public class Index {
    double same_same = 0;		
    double diff_diff = 0;
    double same_diff = 0;     
    double diff_same = 0;
    int size;
    int[][] cMatrix;
    int[][] gtMatrix;
    List<Integer> groundTruth;
    List<Integer> finalCentroids;
   
    public Index(List<Integer> groundTruth, List<Integer> finalCentroids)
    {
        int lines = groundTruth.size();
        cMatrix = new int[lines][lines];
        gtMatrix = new int[lines][lines];
        this.groundTruth = groundTruth;
        this.finalCentroids = finalCentroids;
        size=lines;
        
    }
    
    public double[] CalculateIndex()
    {
        //System.out.print("Size = " +size);
        for(int i = 0; i < size; i++)
        {
            for(int j = i; j < size; j++)
            {
                if(finalCentroids.get(i)==null || finalCentroids.get(j)==null)
                    cMatrix[i][j] = cMatrix[j][i] = 0;
                else
                {
                    if(finalCentroids.get(i) == finalCentroids.get(j))
                        cMatrix[i][j] = cMatrix[j][i] = 1;
                    else
                        cMatrix[i][j] = cMatrix[j][i] = 0;
                }
                if(groundTruth.get(i) == groundTruth.get(j))
                    gtMatrix[i][j] = gtMatrix[j][i] = 1;
                else
                    gtMatrix[i][j] = gtMatrix[j][i] = 0;
            }
        }
         for(int i = 0; i < size; i++)
        {
        for(int j = 0; j < size; j++)
            {
                if(cMatrix[i][j] == gtMatrix[i][j] && cMatrix[i][j] == 1) 
                {
                    same_same++;
                }
                if(cMatrix[i][j] == gtMatrix[i][j] && cMatrix[i][j] == 0) 
                {
                    diff_diff++;
                }
                else
                {
                    if(cMatrix[i][j] == 1 && gtMatrix[i][j] == 0)
                        same_diff++;
                    else if(cMatrix[i][j] == 0 && gtMatrix[i][j] == 1)
                        diff_same++;
                }
            }
        }
        //System.out.println("same _ same " + same_same + " diff_same" + diff_same + "same _ diff " + same_diff);
        System.out.println("Jaccard Coefficient: "+same_same/(same_same+same_diff+diff_same));
        System.out.println("Rand Index: "+(same_same+diff_diff)/(same_same+diff_diff+same_diff+diff_same));
        double[] res = new double[2];
//        return new double[double (same_same/(same_same+same_diff+diff_same)),1.0]
        res[0] = (same_same/(same_same+same_diff+diff_same));
        res[1] = (same_same+diff_diff)/(same_same+diff_diff+same_diff+diff_same);
        
        return res;
    }
        
    
}