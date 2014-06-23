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

    /**
     * methodos toString gia dokimes
     */
    @Override
    public String toString() {
        return "PostingListNode{" + "docID=" + docID + "tf=" + tf /*+ "maxtf=" + maxTf + "size=" + size*/  +'}';
    }
 
}
