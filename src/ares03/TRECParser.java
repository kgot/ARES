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
 * A class that implements parsing methods.
 *
 * @author Terminal
 */
public class TRECParser {

    // identifiers
    private String id = ".I";
    private String content = ".W";
    private String title = ".T";
    private String ignoreA = ".A";
    private String ignoreB = ".B";
    private int maxTF;  //Briskei tin megisti syxnotita emfanisis kapias leksis  sto ka8e doc
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
        int mode = 1;        // 1 read word, 2 read title, 3 read ignore
        int firstDoc = 0;


        if (inputFileName.contains(".pdf") || inputFileName.contains(".rtf")) { //an to arheio einai pdf i rtf
            String text = "";
            // chack file format
            if (inputFileName.contains(".pdf")) {
                PDFTextParser pdfparser = new PDFTextParser();
                text = pdfparser.pdftoText(inputFileName);
            } else if (inputFileName.contains(".rtf")) {
                RTFTextParser rtfparser = new RTFTextParser();
                text = rtfparser.rtftoText(inputFileName);
            }

            StringTokenizer in;

            in = new StringTokenizer(text, " \n");

            while (in.hasMoreTokens()) {
                String token = in.nextToken();

                if (token.equals(id)) {

                    if (firstDoc == 1) {

                        Document doc = new Document(docID, titlewords, words);

                        docList.add(doc);
                        doc.toString();
                        words = "";
                        titlewords = "";
                    }

                    firstDoc = 1;
                    docID = Integer.parseInt(in.nextToken());

                } else if (token.equals(content)) {
                    mode = 1;
                } else if (token.equals(title)) {
                    mode = 2;
                } else if (token.equals(ignoreA) || token.equals(ignoreB)) {
                    mode = 3;
                } else {
                    if (mode == 1) {
                        words += token + " ";
                    } else if (mode == 2) {
                        titlewords += token + " ";
                    } else if (mode == 3) {
                        continue;
                    }
                }
            }



            Document doc = new Document(docID, titlewords, words);

            docList.add(doc);
            doc.toString();

        } else { // if txt
            try {

                BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
                StringTokenizer in;

                String line = "";
                while ((line = reader.readLine()) != null) {

                    in = new StringTokenizer(line, " \n");

                    while (in.hasMoreTokens()) {
                        String token = in.nextToken();

                        if (token.equals(id)) {

                            if (firstDoc == 1) {
                                Document doc = new Document(docID, titlewords, words);
                                docList.add(doc);
                                doc.toString();
                                words = "";
                                titlewords = "";
                            }

                            firstDoc = 1;
                            docID = Integer.parseInt(in.nextToken());

                        } else if (token.equals(content)) {
                            mode = 1;
                        } else if (token.equals(title)) {
                            mode = 2;
                        } else if (token.equals(ignoreA) || token.equals(ignoreB)) {
                            mode = 3;
                        } else {
                            if (mode == 1) {
                                words += token + " ";
                            } else if (mode == 2) {
                                titlewords += token + " ";
                            } else if (mode == 3) {
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
     * Returns a hashmap with query similar documents.
     *
     * @param inputFileName a file with query similar docs
     * @return a hashmap with query similar docs for every query id
     */
    public HashMap<Integer, ArrayList<Integer>> readRelevant(String inputFileName) {
        HashMap<Integer, ArrayList<Integer>> relevantDocs = new HashMap<Integer, ArrayList<Integer>>();

        ArrayList<Integer> relevantDocList = null;

        try {

            BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
            StringTokenizer in;

            String line = "";
            int check = 0;
            while ((line = reader.readLine()) != null) {

                in = new StringTokenizer(line, " \n");

                while (in.hasMoreTokens()) {
                    String token = in.nextToken();

                    if (token.equals("0")) {
                        continue;
                    }
                    if (relevantDocs.containsKey(Integer.parseInt(token))) {
                        relevantDocs.get(Integer.parseInt(token)).add(Integer.parseInt(in.nextToken()));
                        continue;
                    }

                    if (!relevantDocs.containsKey(Integer.parseInt(token))) {

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
     * Returns a hashmap with the unique doc terms. Also calculates max term frequency and size.
     *
     * @return terms
     */
    public HashMap getTerms(Document d) {

        HashMap<String, Integer> terms = new HashMap<String, Integer>();
        StringTokenizer stok;
        String token;
        int tf = maxTF = 0;
        // hash set for stopwords
        HashSet<String> stopwords = this.stopWords(stopwordsFile);


        // if doc content is not empty
        if (!d.getContent().equals("")) {
            // tokenize
            stok = new StringTokenizer(d.getContent(), " ,.\n");
            while (stok.hasMoreTokens()) {
                token = stok.nextToken();

                // if doc is a stopword continue with the next one
                if (stopwords.contains(token)) {
                    continue;
                }

                if (terms.containsKey(token)) {
                    tf = terms.get(token);
                    tf++;
                    terms.put(token, tf);
                    if (tf > maxTF) {
                        maxTF = tf;
                    }
                } else {
                    terms.put(token, 1);
                }


            }
        }

        // if title is not empty
        if (!d.getTitle().equals("")) {
            //tokenize
            stok = new StringTokenizer(d.getTitle(), " ,.\n");
            while (stok.hasMoreTokens()) {
                token = stok.nextToken();

                if (terms.containsKey(token)) {
                    tf = terms.get(token);
                    tf++;
                    terms.put(token, tf);
                    if (tf > maxTF) {
                        maxTF = tf;
                    }
                } else {
                    terms.put(token, 1);
                }
            }
        }
        // set max term frequency and size
        d.setMaxTf(maxTF);
        d.setSize(terms.size());

        return terms;
    }

    public int getMaxTF() {
        return maxTF;
    }

    /**
     * For queries.
     *
     * @return terms
     */
    public HashMap getTerms(String str) {

        HashMap<String, Integer> terms = new HashMap<String, Integer>();
        StringTokenizer stok;
        String token;
        int tf = 0;
        // hash set for stopwords
        HashSet<String> stopwords = this.stopWords(stopwordsFile);


        // if doc content is not empty
        if (!str.equals("")) {
            //tokenize
            stok = new StringTokenizer(str, " ,.\n");
            while (stok.hasMoreTokens()) {
                token = stok.nextToken();

                // if doc is a stopword continue with the next one
                if (stopwords.contains(token)) {
                    continue;
                }

                if (terms.containsKey(token)) {
                    tf = terms.get(token);
                    tf++;
                    terms.put(token, tf);
                } else {
                    terms.put(token, 1);
                }
            }
        }

        return terms;
    }

    /**
     * Creates a hashset with stopwords.
     *
     * @param stopwordsFileName filename and path containing stopwords
     * @return a hashset with stopwords
     */
    public HashSet stopWords(String stopwordsFileName) {
        HashSet<String> stopwords = new HashSet<String>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(stopwordsFileName));
            StringTokenizer in;

            String line = "";
            while ((line = reader.readLine()) != null) {

                in = new StringTokenizer(line, " \n");

                while (in.hasMoreTokens()) {
                    String token = in.nextToken();
                    stopwords.add(token);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TRECParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TRECParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stopwords;
    }
}
