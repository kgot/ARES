/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ares03;

import java.io.Serializable;


/**
 * A class implementing a document.
 * 
 * @author Terminal
 */
public class Document implements Serializable {
    private int id;
    private String title = "";
    private String content = "";
    private int maxTF ; // max term frequency
    private int size;   // number of different terms in a document 


    /**
     *
     * @param id 
     * @param title
     * @param content 
     */
    public Document(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    /**
     *
     * @param id
     * @param title
     * @param content
     */
    public Document(int id, String title, String content, int size, int maxTf ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.maxTF = maxTf;
        this.size = size;
    }

    /**
     *
     * @return id
     */
    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    /**
     *
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @return
     */
    public int getSize(){
        return size;
    }

    public int getMaxTF(){
        return maxTF;
    }

    /**
     *
     * @param size number of different terms in a document
     */
    public void setSize(int size){
        this.size = size;
    }

    /**
     *
     * @param maxTF max term frequency
     */
    public void setMaxTf(int maxTF){
        this.maxTF = maxTF;

    }

    /**
     *  toString for tests
     */
    @Override
    public String toString() {
        return "Τίτλος: " + title + "\n Περιεχόμενο: " + content;
    }

    
    

}
