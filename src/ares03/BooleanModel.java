/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ares03;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jboolexpr.BooleanExpression;
import jboolexpr.MalformedBooleanException;

/**
 * A search implementation based on boolean model.
 * This class contains a method for the weights and similarity calculation
 * based on given query a print method.
 *
 * @author Terminal
 */

public class BooleanModel {
    private InvertedIndex index ;
    private HashMap<String,int[]> weightsmap;
    // similarity array
    private int[] simtab;

    private String conj = "AND";
    private String disj = "OR";
    private String neg = "NOT";
    private String par1 = "(";
    private String par2 = ")";

    /**
     *
     * @param index
     */
    public BooleanModel(InvertedIndex index) {
        this.index = index;
        weightsmap = new HashMap<String,int[]>();
        simtab = new int[index.getNumDocuments()];
    }

    /**
     * 
     * Query parser. Firstly all weights are calculated and then the similarities throught the boolean expression.
     * Use of the jboolexpr library to parse the boolean expression.
     *
     * @param query
     */
    public void queryProcessing(String query) {
        String expr = "";
        BooleanExpression boolexpr = null;
        Boolean bool = false;
        int[] tmp;
        int curweight = 0;

        StringTokenizer in;

        in= new StringTokenizer(query," \n");
        
        // 1st query parsing
        // weight calculation for every term
        while(in.hasMoreTokens()){
                    String token = in.nextToken();

                    if(token.equals(conj) || token.equals(disj) || 
                       token.equals(neg) || token.equals(par1) || token.equals(par2)){
                        //do nothing
                        continue;
                    }
                    else{
                        tmp = getWeightsTab(token);
                        weightsmap.put(token, tmp);
                    }
        }


        // similarity calculation for every query-document
        for(int i=0;i<simtab.length;i++){
            in= new StringTokenizer(query," \n");
            expr = new String();
            // 2nd query parsing

            // boolean expression transforms for processing
             while(in.hasMoreTokens()){
                        String token = in.nextToken();

                        if(token.equals(conj)){
                            expr += "&&";

                        }
                        else if(token.equals(disj)){
                                expr += "||";

                        }
                        else if(token.equals(neg)){
                                expr += "!";
                        }
                        else if(token.equals(par1) || token.equals(par2)){
                                expr += token;
                        }
                        else{
                            curweight = weightsmap.get(token)[i];
                            if(curweight == 1){
                                expr += "true";
                            }
                            else{
                                expr += "false";
                            } 
                        }
             }

            // similarity calculation for query-document through the boolean expression
            // jboolexpr lib used
             try {
                boolexpr = BooleanExpression.readLeftToRight(expr);
                bool = boolexpr.booleanValue();
             } catch (MalformedBooleanException ex) {
                Logger.getLogger(BooleanModel.class.getName()).log(Level.SEVERE, null, ex);
             }

             if(bool == true){
                 simtab[i] = 1;
             }
             else{
                 simtab[i] = 0;
             }
        }
    }

    /**
     * Creates a weights array of a term for every document.
     *
     * @param term
     * @return weights
     */
    public int[] getWeightsTab(String term) {
        int[] weights = new int[index.getNumDocuments()];
        LinkedList<PostingListNode> postlist = new LinkedList<PostingListNode>();
        postlist = index.getPostingList(term);

        // scanning the posting list for the query term
        // if a docs exist on the list then weight=1
        Iterator<PostingListNode> it = postlist.iterator();
        while(it.hasNext()){
            weights[it.next().getDocID()-1] = 1;
        }

        return weights;
    }

    /**
     * Print results.
     */
    public void printSimilarDocs(){
        for(int i=0;i<simtab.length;i++){
            if(simtab[i] == 1){
                 System.out.print("\nÂñÝèçêå ôï Ýããñáöï ìå ID: ");
                 System.out.println(i+1);
                 System.out.println("\n" + index.getDocList().get(i).toString());
            }
        }
    }
    
}
