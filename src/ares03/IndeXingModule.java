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
    private String colFileName = ""; //arheio me katalixi .col


    /**
     * Auti i methodos htizei enan katalogo diavazontas ta eggrafa tou apo ena arheio.
     */
    public void build() {
        
        index = new InvertedIndex();
        ArrayList<Document> docList = new ArrayList<Document>();

        //parser
        TRECParser docparser = new TRECParser();
        
        //ginetai parsing arheion - ohi oron!
        docList = docparser.readDocs(inputFileName);
        //System.out.println(docList.size());

        //kathe arheio pou vrike o parser prostithetai ston katalogo 
        Iterator<Document> it = docList.iterator();
        while(it.hasNext()){
            index.add(it.next());
        }
    }

    /**
     * Anoigma sillogis gia tin opoia idi iparhei katalogos.
     */
    public void openCollection(){

        try {
            ObjectInputStream collection=new ObjectInputStream(new FileInputStream(colFileName));
            //diavasma tou katalogou os antikeimeno
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
     * Apothikeusi se arheio kai kleisimo tis iparhousas sillogis.
     */
    public void closeCollection(){
        try {
            ObjectOutputStream collection = new ObjectOutputStream(new FileOutputStream(colFileName));
            //eggrafi tou katalogou os antikeimeno
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
