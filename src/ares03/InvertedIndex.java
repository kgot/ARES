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
 * This class implments an inverted index. It also contains the list of docs.
 * The inverted index contains the term dictionary, the posting lists and some some elements
 * used from the vector model (term frequency in a document).
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
     * Puts a doc in the list of docs and adds it's term in the dictionary.
     * It also updates the posting list for new or existing terms.
     * 
     * @param d the document to be added
     */
    public void add(Document d) {

        LinkedList<PostingListNode> postingList;
        HashMap<String,Integer> terms = new HashMap<String,Integer>();
        TRECParser termparser = new TRECParser();
        Set<String> keys;
        int docID = 0;
        int tf = 0;
        int maxTf = 0;  // most used term in a doc
        
        // stopwords
        String stopwordsFileName = "stopwords.txt";
        HashSet<String> stopwordList= termparser.stopWords(stopwordsFileName);

        String token = "";

        // add doc to doc list
        docList.add(d);
        numDocuments++;
        docID = d.getID();
        // parse doc terms
        // unique terms and their frequency
        terms = termparser.getTerms(d);

        // calculate maxTf of doc
        maxTf = termparser.getMaxTF();
        // put maxTf in doc
        d.setMaxTf(maxTf);
        // put term count in doc
        d.setSize(terms.size());

        // set of terms
        keys = terms.keySet();
             
        // term iterator
        Iterator it = keys.iterator();
        
        while(it.hasNext()){
            token = (String) it.next();

            if(stopwordList.contains(token)){
                continue;
            }
            
            // if index contains current term
            if (index.containsKey(token)) {
                // get existing posting list
                postingList = index.get(token);
            } else {
                // else create new posting list
                postingList = new LinkedList<PostingListNode>();
                index.put(token, postingList);
            }

            //tf: term frequency 
            //parser getTerms() returns tf      
            tf = terms.get(token);

            // create new posting list node with doc id and term frequency
            PostingListNode currentNode = new PostingListNode(docID, tf);
            postingList.add(currentNode);
        } 
    }

    /**
     * Removes a doc from index. At first, deletes the doc from the doc list, updates 
     * doc ids and reduces total doc number. Then, checks all terms and posting lists
     * and updates them. 
     *
     * @param delDocID
     */
    public void delete(int delDocID){
        String str = "";
        LinkedList<PostingListNode> tmplist;

        // remove function for docs
        docList.remove(delDocID-1);
        for(int i=delDocID-1;i<numDocuments-1;i++){
            docList.get(i).setID(i+1);
        }
        numDocuments--;

        // remove function for terms
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
     * Returns posting lists of a term.
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
     * Returns the list of docs.
     * 
     * @return docList
     */
    public ArrayList<Document> getDocList() {
        return docList;
    }

    /**
     * Returns inverted index.
     * 
     * @return index
     */
    public HashMap<String,LinkedList<PostingListNode>> getIndex() {
        return index;
    }
    
    /**
     * Clears inverted index and doc list.
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
     * method toString for tests
     */
    @Override
    public String toString() {
        return "size:" + index.size() + " InvertedIndex{" + "index=" + index + '}';
    }
 
}
