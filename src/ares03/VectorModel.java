/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ares03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * H klasi pou dimiourgei to modelo Vector
 * @author odysseas
 */
public class VectorModel {
    
    private TRECParser queryParser = new TRECParser();  //parser gia na dimiourgisei hashmap me tous orous tou query;
   
    private HashMap queryTerms ;        //ena hashmap pou periexei tous orous tou query kai tis sixnotites tous
    private InvertedIndex idx ;         //o antestrammenos katalogos
    private int numOfDocuments ;        //o ari8mos ton documents
    private int numOfTerms ;            //sinolikos ari8mos ksexoristwn orwn ston index
    private double[] idf;               //pinakas me ta idf twn ksexoristwn orwn twn query;
    private double[] score ;            //pinakas twn score twn eggrafwn;
    private int similarityFunc ;        //sinartisi omoiotitas metaksi eggrafou-query

    /**
     * Constructor pou pairnei san parametro mono ton antestrammeno katalogo
     *
     * @param invertedIndex o antestrammenos katalogos
     */
    public VectorModel(InvertedIndex invertedIndex){
        this.idx = invertedIndex;                       //o antestrammenos katalogos
        this.numOfTerms = idx.getIndex().size();        //o sinolikos ari8mos twn ksexwristwn orwn tou katalogou
        this.numOfDocuments = idx.getDocList().size();  //o ari8mos twn documents pou periexei o katalogos
    }

    /**
     * Constructor pou pairnei san parametro to query kai ton antestrammeno katalogo.Gia xrisi gia ena mono query
     *
     * @param invertedIndex o antestrammenos katalogos
     * @param query to query tou xristi
     */
    public VectorModel(InvertedIndex invertedIndex , String query){
        this.idx = invertedIndex;                           //o antestrammenos katalogos
        this.numOfTerms = idx.getIndex().size();            //o sinolikos ari8mos twn ksexwristwn orwn tou katalogou
        this.queryTerms = queryParser.getTerms(query);      //oi oroi tou query
        this.numOfDocuments = idx.getDocList().size();      //o ari8mos twn documents pou periexei o katalogos
        this.idf = new double[queryTerms.size()];           //dimiourgei ena pinaka gia ta idf pou einai oso einai kai oi ksexoristoi oroi tou query
        
    }
    /**
     *
     * @param similarity
     */
    public void setSimilarityFunc(int similarity){
        this.similarityFunc = similarity;
    }
    /**
     * 
     * @return similarity
     */
    public int getSimilarityFunc(){
        return similarityFunc;
    }

    /**
     * Pairnei to String query kai to kanei parse kai dimiourgei ena pinaka idf me mege8os oso kai oi ksexwristoi oroi tou query
     *
     * @param query to query san String
     */
    public void queryProcessing(String query){
        queryTerms = queryParser.getTerms(query);
        this.idf = new double[queryTerms.size()];           //dimiourgei ena pinaka gia ta idf pou einai oso einai kai oi ksexoristoi oroi tou query
    }

    /**
     * Pairnei to query san document kai kanei parse kai dimiourgei ena pinaka idf me mege8os oso kai oi ksexwristoi oroi
     *
     * @param query to query san document
     */
    public void queryProcessing(Document query){
        queryTerms = queryParser.getTerms(query);
        this.idf = new double[queryTerms.size()];
    }

    /**
     * H basikh me8odos gia na ginei anaktisi olwn twn sxetikwn eggrafwn taksinomimenwn ws pros tin sxetikotita tous
     *
     * @param similarityMeasure sinartisi omoiotitas pou epilegei o xristis
     * @return ArrayList<Result> to opoio periexei ola ta sxetika eggrafa taksinomimena ws pros ti sxetikotita tous
     */
    public ArrayList<Result> runRetrievalStrategy(){
        ArrayList<Result> results = new ArrayList<Result>();
        double currentScore;
        int docID;
        Result currentResult;
        Document currentDoc;

        //arxikopoisi twn score twn eggrafwn
        score = new double[numOfDocuments+1];

        for (int i = 1; i < (numOfDocuments +1) ;i++){
            score[i] = 0.0;
        }
        //gia oles tis sinartiseis omoiotitas 
        runGeneralModel();

        //gia tin jaccard sinartisi omoiotitas xreiazetai na trexei kai auti;
        if(this.getSimilarityFunc() == 5){
            updateJaccardScore();
        }

        for (int i = 1 ; i < (numOfDocuments+1); i++){
            if (score[i] != 0){
                currentScore = score[i];
                docID = i;
                currentDoc = idx.getDocList().get(docID-1);
                currentResult = new Result(currentScore,currentDoc);
                results.add(currentResult);
            }
        }

        Collections.sort(results);
        Collections.reverse(results);

        return results;
    }

    /**
     * H sinartisi pou bazei score se ka8e document tou opoiou o oros isoutai me kapoion apo tous orous tou query
     *
     */
    private void runGeneralModel(){

        String currentTerm;
        LinkedList currentPL ;          //current PostingList
        PostingListNode  currentPLN;    //current PostingListNode
        int queryCTF;                   //query current term frequency
        int df;                         //posa eggrafa perilambanoun ton oro tou query
        int index = 0;                  //index pou boi8a stin simplirosi tou pinaka me ta idf(inverse document frequency)
        int docID ;                     //document id
        int termF ;                     //term frequency tou document
        int maxTF ;
        double queryWeight;
        double docWeight;

        Set querySet = queryTerms.keySet();
        Iterator term = querySet.iterator();

        
        //gia ka8e oro tou query pairnoume tin PostingList
        while (term.hasNext()){
          
            currentTerm = (String) term.next();
            queryCTF = (Integer)queryTerms.get(currentTerm);

            currentPL = idx.getPostingList(currentTerm);
            
            //An i Postinglist einai null eksodos apo ton broxo .
            if (currentPL==null){
                 continue;
            }

            df = currentPL.size();
            idf[index] = Math.log((double)numOfDocuments/df);    //

            //ypologismos barous tou orou tou query afou yparxei o oros ston katalogo
            queryWeight = this.getWeightNorm(queryCTF, 1,idf[index]);
            
            
            //gia ka8e PostingList ypologizoume to baros tou orou gia ka8e doc
            Iterator j = currentPL.iterator();
            while (j.hasNext()){
                currentPLN =(PostingListNode) j.next();
                docID = currentPLN.getDocID();
                termF = currentPLN.getTF();

                //maxTF = idx.getDocList().get(docID-1).getMaxTF();
                //docWeight = this.getWeightNorm(termF,maxTF,idf[index]);

                //upologismos barous tou orou tou eggrafou
                docWeight = this.getWeightNorm(termF,1,idf[index]);
                runSimilarity(docID,queryWeight,docWeight,queryTerms.size());                
            }
            index++;
        }
        
    }

    /**
     * upologizei ta bari gia ka8e document i query
     *
     * @param tf term frequency
     * @param maxTf max term frequency
     * @param idf   ivnerse document frequency
     * @return to baros
     */
    public double getWeightNorm(int tf,int maxTf,double idf){
        double w;
        double normalizedFreq = tf/maxTf;
        double normalizedIdf = idf/Math.log(numOfDocuments);

        w = normalizedFreq*normalizedIdf;

        return w;
    }

    /**
     *Sinartisi gia epilogi sinartisis omoiotitas
     *
     * @param docID to id tou document tou opoiou to score 8a anaba8mistei
     * @param queryWeight   to baros tou query
     * @param docWeight to baros tou document
     * @param similarity i sinartisi omoiotitas pou 8a xrisimopoih8ei analoga me tin epi8imia tou xristi.
     */
    private void runSimilarity(int docID, double queryWeight, double docWeight,int querySize){
        int i = 0;
        switch (similarityFunc){
                    case 1: updateCosineProductScore(docID, queryWeight,docWeight);//den pairnoume to querysize edw giati einai analogo, kai den 8a allaksei tin katataksi
                            break;
                    case 2: updateDotProductScore(docID, queryWeight, docWeight);
                            break;
                    case 3: updateDiceScore(docID, queryWeight, docWeight, querySize);
                            break;
                    case 4: updateOverlapScore(docID, queryWeight, docWeight, querySize);
                            break;
                    case 5: updateDotProductScore(docID, queryWeight, docWeight);//einai i prwti fasi tou jaccard mondelou i sinexeia ilopoiitai parakatw
                            break;
                    default: System.out.println("ERROR");
                        break;
        }
    }

    /**
     * sunartisi pou ulopoiei th sunartisi omoiotitas tou eswterikou ginomenou kai anaba8mizei to score ka8e eggrafou
     *
     * @param docID to id tou document tou opoiou to score 8a anaba8mistei
     * @param queryWeight   to baros tou query
     * @param docWeight to baros tou document
     */
    private void updateDotProductScore(int docID,double queryWeight,double docWeight) {
        score[docID] += (queryWeight * docWeight);
    }


    /**
     * sunartisi pou ulopoiei th sunartisi omoiotitas tou sinimitonou kai anaba8mizei to score ka8e eggrafou
     *
     * @param docID to id tou document tou opoiou to score 8a anaba8mistei
     * @param queryWeight   to baros tou query
     * @param docWeight to baros tou document
     */
    private void updateCosineProductScore(int docID,double queryWeight,double docWeight) {
        //System.out.println("doc id " + docID);
        int docLength = idx.getDocList().get(docID-1).getSize();
        //System.out.println("docLength " + docLength);
        score[docID]+= (queryWeight * docWeight)/docLength;
    }

    /**
     *sunartisi pou ulopoiei th sunartisi omoiotitas Dice kai anaba8mizei to score ka8e eggrafou
     *
     * @param docID to id tou document tou opoiou to score 8a anaba8mistei
     * @param queryWeight   to baros tou query
     * @param docWeight to baros tou document
     */
    private void updateDiceScore(int docID, double queryWeight, double docWeight,int querySize){
       int docLength = idx.getDocList().get(docID-1).getSize();
       int docLengthSquared = docLength*docLength;
       int querySizeSquared = querySize*querySize;

       score[docID] +=2*(queryWeight * docWeight)/(docLengthSquared+querySizeSquared);
    }


    /**
     *sunartisi pou ulopoiei th sunartisi omoiotitas tis epikalipsis kai anaba8mizei to score ka8e eggrafou
     *
     * @param docID to id tou document tou opoiou to score 8a anaba8mistei
     * @param queryWeight   to baros tou query
     * @param docWeight to baros tou document
     * @param querySize to mege8os tou query
     */
    private void updateOverlapScore(int docID, double queryWeight, double docWeight,int querySize){
        int docLength = idx.getDocList().get(docID-1).getSize();
        int minimum = querySize;

        if (querySize<docLength){
            minimum = docLength;
        }
        int minimumSquared = minimum*minimum;

        score[docID] += (queryWeight * docWeight)/minimumSquared;
    }

    /**
     * sunartisi pou ulopoiei th sunartisi omoiotitas Jaccard kai anaba8mizei to score ka8e eggrafou.
     *
     * @param docID to id tou document tou opoiou to score 8a anaba8mistei
     * @param queryWeight   to baros tou query
     * @param docWeight to baros tou document
     * @param querySize to mege8os tou query
     */
    private void updateJaccardScore(){
        int querySizeSquared = queryTerms.size() * queryTerms.size();
        for (int i = 1 ; i < (numOfDocuments+1); i++){
            if (score[i] != 0){
                double product = score[i];  //exoun score oso to eswteriko ginomeno
                int docLength = idx.getDocList().get(i-1).getSize();
                int docLengthSquared = docLength * docLength;
                score[i] = product/(querySizeSquared + docLengthSquared - product);
            }
        }
    }

    
    /**
     * ektypwnei ola ta sxetika eggrafa
     *
     * @param results ta sxetika eggrafa me ta score tous
     */
    public void printSimilarDocs(ArrayList<Result> results){
        
        System.out.println("ΚΑΤΑΤΑΞΗ ΤΩΝ ΕΓΓΡΑΦΩΝ");
        System.out.println("---------------------");
        int i = 1;
        for(Result result:results){
            System.out.print(i + ") ");
            System.out.println(result);
            System.out.println("");
            i++;
        }
        System.out.println("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
    }
    /**
     * ektypwnei ta top k sxetika eggrafa me ta score tous.
     *
     * @param results ta sxetika eggrafa me ta score tous
     * @param k ta topk
     */
    public void printSimilarDocs(ArrayList<Result> results, int k){
        getTopK(results,k);

        System.out.println("ΚΑΤΑΤΑΞΗ ΤΩΝ ΕΓΓΡΑΦΩΝ");
        System.out.println("---------------------");
        int i = 1;
        for(Result result:results){
            System.out.print(i + ") ");
            System.out.println(result);
            System.out.println("");
            i++;
        }
        System.out.println("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
        
    }

    /**
     * epistrefei ta topk apo ta apotelesmata. xrisimopoieitai gia tin parousiasei kai mono
     * den kladeuei dld ta apotelesmata ola;
     *
     * @param results
     * @param k
     */
    public void getTopK(ArrayList<Result> results, int k){

        try{
            if ( k-1< results.size()){
                while(results.get(k)!=null){
                    results.remove(k);
                }
            }
        }catch(IndexOutOfBoundsException ex){

        }
    }

    /**
     * eswteriki klasi gia na krataei ta apotelesmata me ta score tous
     */
    protected class Result implements Comparable {
        
        private double score;
        private Document currentDocument;
        private static final int MAX_LENGTH = 60;

        /**
         * constuctor
         *
         * @param s double to opoio pairnei tin timi tou score tou eggrafou
         * @param d to eggrafo.
         */
        public Result(double s, Document d) {
            score = s;
            currentDocument = d;
        }

        /**
         * epistrefei to document
         * @return to document
         */
        public Document getDocument () {
        return currentDocument;
        }

        /**
         * yperfortwsh ths compareTo etsi wste ta eggraf na taksinomountai me bash to score tous
         * @param r
         * @return
         */
        public int compareTo (Object r) {
            Result currentResult = (Result) r;
            if (currentResult.score > score)
                return -1;
            if (currentResult.score < score)
                return 1;
        return 0;
        }

        @Override
        public String toString(){
            return "Βρέθηκε το έγγραφο με ID:" + this.getDocument().getID() + "     Βαθμολογία Εγγράφου :" + this.score +" \n\n " + this.getDocument();
        }
                
    
    }
}
