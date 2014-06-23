/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ares03;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * Ilopoiisi tou anestrammenou katalogou. Periehei episis kai ti lista ton eggrafon apo 
 * ta opoia proekipse o anestrammenos katalogos. O anestrammenos katalogos periehei to lexiko ton oron,
 * kathos kai ti lista emfaniseon kai plirofories gia to vector model (sihnotita emfanisis enos orou se ena eggrafo).
 *
 * @author Terminal
 */
public class InvertedIndex implements Serializable {
    private HashMap<String,LinkedList<PostingListNode>> index;
    private ArrayList<Document> docList;
    private int numDocuments;


    public InvertedIndex() {
        index = new HashMap<String,LinkedList<PostingListNode>>();
        docList = new ArrayList<Document>();
        numDocuments = 0;
    }

    /**
     * Prosthetei ena eggrafo sti lista ton eggrafon kai dimiourgei/prosthetei
     * tous orous tou eggrafou sto lexiko. Episis dimiourgei/ananeonei ti lista emfaniseon
     * gia kainourgios/iparhnotes orous.
     * 
     * @param d To document gia prostiki.
     */
    public void add(Document d) {

        LinkedList<PostingListNode> postingList;
        HashMap<String,Integer> terms = new HashMap<String,Integer>();
        TRECParser termparser = new TRECParser();
        Set<String> keys;
        int docID = 0;
        int tf = 0;
        int maxTf = 0;  //o oros pou emfanizetai perissoteres fores sto document
        
        //stopwords
        String stopwordsFileName = "stopwords.txt";
        HashSet<String> stopwordList= termparser.stopWords(stopwordsFileName);

        String token = "";

        //prosthiki tou eggrafou sti lista eggrafon
        docList.add(d);
        numDocuments++;
        docID = d.getID();
        //parsing ton oron tou eggrafou (periehomenou kai titlou)
        //pairnoume tous xehoristous orous tou eggrafou kai ti sihnotita emfanisis tous sto eggrafo
        terms = termparser.getTerms(d);

        //briskei to maxtf gia ka8e doc
        maxTf = termparser.getMaxTF();
        //bazei to maxTF se ka8e eggrafo
        d.setMaxTf(maxTf);
        //bazei to pli8os orwn se ka8e eggrafo;
        d.setSize(terms.size());

        //ena set me tous orous
        keys = terms.keySet();
             
        //epanaliptis gia tous orous
        Iterator it = keys.iterator();
        
        while(it.hasNext()){
            token = (String) it.next();

            //System.out.println(token);

            if(stopwordList.contains(token)){
                //System.out.println(token + "       stopword");
                continue;
            }
            
            //an o index periehei ton trehon oro
            if (index.containsKey(token)) {
                //pernoume ti lista emfaniseon tou orou pou idi iparhei
                postingList = index.get(token);
            } else {
                //allios dimiourgoume mia kainourgia lista emfanisis gia ton kainourgio oro
                postingList = new LinkedList<PostingListNode>();
                index.put(token, postingList);
            }

            //tf: term frequency 
            //epistrafike  apo tin getTerms() tou parser        
            tf = terms.get(token);

            

            //dimiourgia neou komvou tis listas emfanisis me ton arithmo tou eggrafou pou emfanistike o oros
            //kai tis sihnotitas
            PostingListNode currentNode = new PostingListNode(docID, tf);
            //PostingListNode currentNode = new PostingListNode(docID, tf, maxTf, terms.size());
            postingList.add(currentNode);
        } 
    }

    /**
     * Diagrafei ena eggrafo apo ton katalogo. Se proti fasi diagrafei
     * to eggrafo apo ti lista eggrafon, diorthonei ta id ton epomenon
     * kai meionei to sinoliko megethos ton eggrafon. Sti deuteri fasi anatrehei
     * olous tous orous kai exetazei tis listes anaforas. An periehetai to eggrafo
     * pros diagrafi, afaireitai apo tis listes, eno an i trehousa anafora ehei
     * megalitero id, auto diorthonetai.
     *
     * @param delDocID
     */
    public void delete(int delDocID){
        String str = "";
        LinkedList<PostingListNode> tmplist;

        // leitourgia diagrafis gia ta eggrafa
        docList.remove(delDocID-1);
        for(int i=delDocID-1;i<numDocuments-1;i++){
            docList.get(i).setID(i+1);
        }
        numDocuments--;

        // leitourgia diagrafis gia tous orous
        Iterator it = index.keySet().iterator();
        while(it.hasNext()){
            str = (String) it.next();
            tmplist = new LinkedList<PostingListNode>();
            tmplist = index.get(str);

            for(int i=0;i<tmplist.size();i++){
                if(tmplist.get(i).getDocID() == delDocID){
                    tmplist.remove(i);
                }
                else if(tmplist.get(i).getDocID() > delDocID){
                    tmplist.get(i).setDocID(tmplist.get(i).getDocID()-1);
                }
            }            
        }
    }

    /**
     * Epistrefei ti lista emfaniseon enos dosmenou orou.
     * 
     * @param term
     * @return result
     */
    public LinkedList getPostingList(String term) {

        LinkedList<PostingListNode> result = new LinkedList<PostingListNode>();

        if(index.containsKey(term)){
            result = index.get(term);
        }
        else{
            result = null;
        }
        return result;
    }

    /**
     * Epistrefei ti lista ton eggrafon.
     * 
     * @return docList
     */
    public ArrayList<Document> getDocList() {
        return docList;
    }

    /**
     * Epistrefei ton anestrameno katalogo.
     * 
     * @return index
     */
    public HashMap<String,LinkedList<PostingListNode>> getIndex() {
        return index;
    }
    
    /**
     * Adeiazei ton anestrameno katalogo kai ti lista eggrafon.
     */
    public void clear() {
        index.clear();
        docList.clear();
        numDocuments = 0;
    }

     /**
     * @return numDocuments
     */
    public int getNumDocuments() {
        return numDocuments;
    }


    /**
     * methodos toString gia dokimes
     */
    @Override
    public String toString() {
        return "size:" + index.size() + " InvertedIndex{" + "index=" + index + '}';
    }
 
}
