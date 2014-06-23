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
public class Document implements Serializable {
    private int id;
    private String title = "";
    private String content = "";
    //private TRECParser parser = new TRECParser();
    private int maxTF ; //megisti sixnotita emfanisis kapias leksis
    private int size;   //ari8mos diaforetikon orwn pou apoteleitai to eggrafo.


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
        //parser.getTerms(this);
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
     * @param size to pli8os ksexoristwn orwn tou eggrafou
     */
    public void setSize(int size){
        this.size = size;
    }

    /**
     *
     * @param maxTF h megisti sixnotita kapoias leksis sto eggrafo
     */
    public void setMaxTf(int maxTF){
        this.maxTF = maxTF;

    }

//    public String[] getTerms (){
//
//    }

    /**
     *  toString gia dokimes
     */
    @Override
    public String toString() {
        return "Τίτλος: " + title + "\n Περιεχόμενο: " + content;
    }

    
    

}
