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
 * Ilopoiisi tis anazitisis me vasi to boolean montelo.
 * I klasi periehei methodo gia tin exagogi ton varon kai ton ipologismo ton omoiotiton
 * me vasi dosmeno query kai mia methodo gia tin ektiposi ton apotelesmaton.
 *
 * @author Terminal
 */

public class BooleanModel {
    private InvertedIndex index ;
    private HashMap<String,int[]> weightsmap;
    // pinakas omoiotiton
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
     * Epexergazetai mia query. Exagontai prota ta vari olon ton oron tis query
     * kai epeita ipologizontai oi omoiotites ton meso tis logikis ekfrasis.
     * Ginetai hrisi tis vivliothikis jboolexpr gia tin analisi tis logikis ekfrasis.
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
        
        // 1o perasma tis query
        // exagogi ton varon gia kathe oro
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


        // ipologismos tis omoiotitas gia kathe query-eggrafo
        for(int i=0;i<simtab.length;i++){
            in= new StringTokenizer(query," \n");
            expr = new String();
            //2o perasma tis query

            //i logiki ekfrasi allazei se katallili morfi gia na epexergastei
            //apo tin BooleanExpression tis vivliothikis jboolexpr
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

            //prosdiorismos omoiotitas query-eggrafou meso tis logikis ekfrasis
            //hrisimpooithike i vivliothiki jboolexpr
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
     * Dimiourgei enan pinaka varon enos orou gia kathe eggrafo gia to boolean montelo.
     *
     * @param term
     * @return weights
     */
    public int[] getWeightsTab(String term) {
        int[] weights = new int[index.getNumDocuments()];
        LinkedList<PostingListNode> postlist = new LinkedList<PostingListNode>();
        postlist = index.getPostingList(term);

        // perasma tis posting list gia ton oro tou query
        // osoi docs iparhoun sti lista simionontai me varos 1
        Iterator<PostingListNode> it = postlist.iterator();
        while(it.hasNext()){
            weights[it.next().getDocID()-1] = 1;
        }

        return weights;
    }

    /**
     * Ektiposi apotelematon.
     */
    public void printSimilarDocs(){
        for(int i=0;i<simtab.length;i++){
            if(simtab[i] == 1){
                 System.out.print("\nΒρέθηκε το έγγραφο με ID: ");
                 System.out.println(i+1);
                 System.out.println("\n" + index.getDocList().get(i).toString());
            }
        }
    }
    
}
