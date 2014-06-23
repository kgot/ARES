/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ares03;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IndeXing Module
 * 
 * @author Terminal
 */
public class IndeXingModule {

    private InvertedIndex index;
    private String inputFileName = "";
    private String colFileName = ""; // file with .col extension


    /**
     * This method builds an index by scanning the documents of a file.
     */
    public void build() {
        
        index = new InvertedIndex();
        ArrayList<Document> docList = new ArrayList<Document>();

        //parser
        TRECParser docparser = new TRECParser();
        
        // file parsing - not term parsing!
        docList = docparser.readDocs(inputFileName);

        // every document is added in index
        Iterator<Document> it = docList.iterator();
        while(it.hasNext()){
            index.add(it.next());
        }
    }

    /**
     * Open a collection for existing index.
     */
    public void openCollection(){

        try {
            ObjectInputStream collection=new ObjectInputStream(new FileInputStream(colFileName));
            // read index as object
            index = (InvertedIndex) collection.readObject();
            System.out.println("");

            collection.close();
        } catch (IOException ex) {
            Logger.getLogger(IndeXingModule.class.getName()).log(Level.SEVERE, null, ex);
        } catch(ClassNotFoundException ex1){
            Logger.getLogger(IndeXingModule.class.getName()).log(Level.SEVERE, null, ex1);
        }

    }

    /**
     * Store in file and close the collection.
     */
    public void closeCollection(){
        try {
            ObjectOutputStream collection = new ObjectOutputStream(new FileOutputStream(colFileName));
            // write index ti file through stream
            collection.writeObject(index);
            System.out.println("");

            collection.close();
        } catch (IOException ex) {
            Logger.getLogger(IndeXingModule.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            index.clear();
        }

    }

    public void setInputFilename(String filePath){
        this.inputFileName = filePath;
    }

    public void setColFilename(String colFilePath){
        this.colFileName = colFilePath;
    }

    public InvertedIndex getInvertedIndex(){
        return index;
    }

}
