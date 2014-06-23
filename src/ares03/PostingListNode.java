/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ares03;

import java.io.Serializable;


/**
 *
 * @author Terminal
 */
public class PostingListNode implements Serializable {

    private int docID;
    private int tf;    //term frequency
//    private int maxTf; //maximum term frequency
//    private int size;  //sinolo ksexwristwn orwn tou document

    public PostingListNode() {
    }


    /**
     * 
     * @param docID
     * @param tf
     */
    public PostingListNode(int docID, int tf) {
        this.docID = docID;
        this.tf = tf;

    }

//    /**
//     *
//     * @param docID
//     * @param tf
//     * @param maxTf
//     * @param size
//     */
//    public PostingListNode(int docID, int tf,int maxTf,int size){
//        this.docID = docID;
//        this.tf = tf;
//        this.size = size;
//        this.maxTf = maxTf;
//    }

    /**
     * 
     * @return docID
     */
    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }
    
    /**
     * 
     * @return tf
     */
    public int getTF() {
        return tf;
    }
    
//    /**
//     * 
//     * @return max frequency of a word in the document
//     */
//    public int getMaxTf(){
//        return maxTf;
//    }
//    
//    /**
//     * 
//     * @return
//     */
//    public int size(){
//        return size;
//    }

    /**
     * methodos toString gia dokimes
     */
    @Override
    public String toString() {
        return "PostingListNode{" + "docID=" + docID + "tf=" + tf /*+ "maxtf=" + maxTf + "size=" + size*/  +'}';
    }
 
}
