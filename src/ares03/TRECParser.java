/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ares03;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasi pou ilopoiei oles tis leitourgies parsing pou hreiazetai to project.
 * 
 * @author Terminal
 */
public class TRECParser {

    //identifiers
    private String id = ".I";
    private String content = ".W";
    private String title = ".T";
    private String ignoreA = ".A";
    private String ignoreB = ".B";
    private int maxTF ;  //Briskei tin megisti syxnotita emfanisis kapias leksis  sto ka8e doc
    private String stopwordsFile = "stopwords.txt";
    /**
     * 
     * @return
     */
    public ArrayList<Document> readDocs(String inputFileName) {
        ArrayList<Document> docList = new ArrayList<Document>();
        String words = "";
        String titlewords = "";
        int docID = 0;       
        int mode = 1;        // 1 gia diavasma word, 2 gia diavasma title, 3 gia diavasma ignore
        int firstDoc = 0;


        if(inputFileName.contains(".pdf") || inputFileName.contains(".rtf")){ //an to arheio einai pdf i rtf
            String text = "";
            // eleghos tou format tou arheiou
            if(inputFileName.contains(".pdf")){
                PDFTextParser pdfparser = new PDFTextParser();
                text = pdfparser.pdftoText(inputFileName);
            }
            else if(inputFileName.contains(".rtf")){
                RTFTextParser rtfparser = new RTFTextParser();
                text = rtfparser.rtftoText(inputFileName);
            }
            
            StringTokenizer in;

                //System.out.println(s);
            in= new StringTokenizer(text," \n");

            while(in.hasMoreTokens()){
                String token = in.nextToken();
                //System.out.println(in.nextToken());

                if(token.equals(id)){

                    if(firstDoc == 1){
                        //System.out.println(words);
                        Document doc = new Document(docID, titlewords, words);
                        //doc.toString();
                        docList.add(doc);
                        doc.toString();
                        words = "";
                        titlewords = "";
                    }

                    firstDoc = 1;
                    docID = Integer.parseInt(in.nextToken());

                    }
                    else if(token.equals(content)){
                        mode = 1;
                    }
                    else if(token.equals(title)){
                        mode = 2;
                    }
                    else if(token.equals(ignoreA) || token.equals(ignoreB)){
                        mode = 3;
                    }
                    else{
                        if(mode == 1){
                        //words.add(token);
                            words += token + " ";
                        }
                        else if(mode == 2){
                        //titlewords.add(token);
                            titlewords += token + " ";
                    }
                        else if(mode == 3){
                        continue;
                    }
                }
             }



            Document doc = new Document(docID, titlewords, words);

            docList.add(doc);
            doc.toString();

        }
        else{ //an to arheio einai .txt
            try {

            BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
            StringTokenizer in;

            String line = "";
            while((line = reader.readLine()) != null){
                //System.out.println(s);
                in= new StringTokenizer(line," \n");

                while(in.hasMoreTokens()){
                    String token = in.nextToken();
                    //System.out.println(in.nextToken());

                    if(token.equals(id)){

                        if(firstDoc == 1){
                            //System.out.println(words);
                            Document doc = new Document(docID, titlewords, words);
                            //doc.toString();
                            docList.add(doc);
                            doc.toString();
                            words = "";
                            titlewords = "";
                        }

                        firstDoc = 1;
                        docID = Integer.parseInt(in.nextToken());

                        }
                        else if(token.equals(content)){
                            mode = 1;
                        }
                        else if(token.equals(title)){
                            mode = 2;
                        }
                        else if(token.equals(ignoreA) || token.equals(ignoreB)){
                            mode = 3;
                        }
                        else{
                            if(mode == 1){
                            //words.add(token);
                                words += token + " ";
                            }
                            else if(mode == 2){
                            //titlewords.add(token);
                                titlewords += token + " ";
                        }
                            else if(mode == 3){
                            continue;
                        }
                    }
                 }

                }

                Document doc = new Document(docID, titlewords, words);

                docList.add(doc);
                doc.toString();

                reader.close();


            } catch (FileNotFoundException ex) {
                Logger.getLogger(TRECParser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TRECParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        

        return docList;
    }


    /**
     * epistrefei ena hasmap pou periexei ta sxetika eggrafa gia ka8e query, otan oi sillogh einai anaforas.
     *
     * @param inputFileName to arxeio me ta sxetika eggrafa gia ka8e query
     * @return  ena hasmap pou periexei to id tou query kai mia lista me ta sxetika eggrafa gia ka8e query
     */
    public HashMap<Integer,ArrayList<Integer>> readRelevant(String inputFileName){
        HashMap<Integer,ArrayList<Integer>> relevantDocs = new HashMap<Integer,ArrayList<Integer>>();
        
        ArrayList<Integer> relevantDocList = null;
                       
        try {
  
            BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
            StringTokenizer in;

            String line = "";
            int check = 0;
            while((line = reader.readLine()) != null){
                //System.out.println(s);
                in= new StringTokenizer(line," \n");


                while(in.hasMoreTokens()){
                    String token = in.nextToken();
                                      
                    if(token.equals("0")){
                        continue;
                    }
                    if (relevantDocs.containsKey(Integer.parseInt(token))){
                        relevantDocs.get(Integer.parseInt(token)).add(Integer.parseInt(in.nextToken()));
                        continue;
                    }

                    if (!relevantDocs.containsKey(Integer.parseInt(token))){

                        relevantDocList = new ArrayList<Integer>();
                        relevantDocs.put(Integer.parseInt(token), relevantDocList);
                    }
                    relevantDocs.get(Integer.parseInt(token)).add(Integer.parseInt(in.nextToken()));
                }
            }
            reader.close();
           

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TRECParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TRECParser.class.getName()).log(Level.SEVERE, null, ex);
        }      
        return relevantDocs;

    }


     /**
     * epistrefei hashmap me tous ksexwristous orous tou eggrafou, episis gia ka8e eggrafo dinei to maxtf kai to size tou
     * 
     * @return terms
     */
    public HashMap getTerms(Document d){

        HashMap<String,Integer> terms = new HashMap<String,Integer>();
        StringTokenizer stok;
        String token;
        int tf = maxTF = 0;
        //hash set gia to stopwords
        HashSet<String> stopwords = this.stopWords(stopwordsFile);


        //an to periehomeno tou eggrafou den einai keno
        if(!d.getContent().equals("")){
            //tokenize
            stok = new StringTokenizer(d.getContent(), " ,.\n");
            while(stok.hasMoreTokens()){
                token = stok.nextToken();

                //an to token einai stopword sinexise me to epomeno
                if(stopwords.contains(token)){
                    continue;
                }

                if(terms.containsKey(token)){
                    tf = terms.get(token);
                    tf++;
                    terms.put(token, tf);
                    if(tf>maxTF) maxTF =tf;
                }
                else{
                    terms.put(token, 1);
                }
                
                
            }
        }

        //an o titlos tou eggrafou den einai kenos
        if(!d.getTitle().equals("")){
            //tokenize
            stok = new StringTokenizer(d.getTitle(), " ,.\n");
            while(stok.hasMoreTokens()){
                token = stok.nextToken();

                if(terms.containsKey(token)){
                    tf = terms.get(token);
                    tf++;
                    terms.put(token, tf);
                    if(tf>maxTF) maxTF =tf;
                }
                else{
                    terms.put(token, 1);
                }
            }
        }
         //gia ka8e document briskei to maxTF kai to size tou(ari8mos ksexwristwn orwn)
         d.setMaxTf(maxTF);
         d.setSize(terms.size());

         return terms;
    }

    public int getMaxTF(){
        return maxTF;
    }

     /**
     *gia ta queries
     *
     * @return terms
     */
    public HashMap getTerms(String str){

        HashMap<String,Integer> terms = new HashMap<String,Integer>();
        StringTokenizer stok;
        String token;
        int tf = 0;
        //hash set gia to stopwords
        HashSet<String> stopwords = this.stopWords(stopwordsFile);


        //an to periehomeno tou eggrafou den einai keno
        if(!str.equals("")){
            //tokenize
            stok = new StringTokenizer(str, " ,.\n");
            while(stok.hasMoreTokens()){
                token = stok.nextToken();

                //an to token einai stopword sinexise me to epomeno
                if(stopwords.contains(token)){
                    continue;
                }

                if(terms.containsKey(token)){
                    tf = terms.get(token);
                    tf++;
                    terms.put(token, tf);
                }
                else{
                    terms.put(token, 1);
                }
            }
        }

         return terms;
    }

    /**
     * dimiourgei ena HashSet me stopwords.
     *
     * @param stopwordsFileName to path kai to onoma tou txt arxeiou pou periexei ta stopwords
     * @return  ena HashSet me ta stopwords
     */
    public HashSet stopWords(String stopwordsFileName){
        HashSet <String> stopwords = new HashSet<String>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(stopwordsFileName));
            StringTokenizer in;

            String line = "";
            while((line = reader.readLine()) != null){
                
                in= new StringTokenizer(line," \n");
                
                while(in.hasMoreTokens()){
                    String token = in.nextToken();
                    stopwords.add(token);
                    //System.out.println("i:"+ i +" token: " + token);

                }
            }

                    //System.out.println(in.nextToken());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TRECParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TRECParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stopwords;
    }
}
