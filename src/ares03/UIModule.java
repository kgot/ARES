/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ares03;

import ares03.VectorModel.Result;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * User Interface Module
 * 
 * @author Terminal
 */
public class UIModule {
    private static Scanner sc;
    private static IndeXingModule ixm = null;
    private static InvertedIndex index = null;
    private static DecimalFormat df = new DecimalFormat("#.##"); //gia na formarontai ta apotelesmata
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        sc = new Scanner(System.in);
        ixm = new IndeXingModule();

        int choice;                     //i epilgi apo to menu

        do{
            //ektypwsi ton epilogwn
            printMainMenu();
            //apo8ikeusi epilogis san integer
            choice = Integer.parseInt(sc.nextLine());
            switch (choice){
                case 1: Collections();
                        break;

                case 2: vectorModel();
                        break;

                case 3: booleanModel();
                        break;

                case 4: System.out.println("ΓΕΙΑ ΣΑΣ!");
                        break;

                default: System.out.println("Λάθος επιλογή!");
                        break;
            }
        }while (choice != 4);

    }

     //------------------------------------------------------------------//

     //SINARTISEIS VECTOR MONTELOU

     /**
      * synartisi pou kaleitai gia to vector modelo
      */
    private static void vectorModel(){

        int choice ;
        int similarity,topk ;
        String query;
        boolean flag = false;
        long startTime,endTime; //gia metrisi tou xronou 
        ArrayList<Result> results; //apotelesmata

        //elegxos an uparxei anoixti sillogi
        if(index != null) {
            flag = true;
        }

        do{
            printVectorMenu();
            choice = Integer.parseInt(sc.nextLine());
            VectorModel vector;

            switch (choice){
                case 1: openCollection();
                        flag = true;
                        break;

                case 2: if (!flag) openCollection();   //anoigma sillogis an den uparxei idi anixti
                        vector = new VectorModel(index);    //dimiourgia kainourgiou vector modelou
                        System.out.println("Δώσε το ερώτημα:");
                        query = sc.nextLine();
                        vector.queryProcessing(query);
                        System.out.println("Δώσε σύναρτηση ομοιότητας:");
                        similarity = chooseSimilarity();
                        vector.setSimilarityFunc(similarity);
                        startTime = System.currentTimeMillis();
                        results = vector.runRetrievalStrategy();
                        endTime = System.currentTimeMillis();
                        vector.printSimilarDocs(results);
                        System.out.println("\nΧρόνος Επεξεργασίας: " + (endTime-startTime) + " milliseconds");

                        flag = true;
                        break;

                case 3: if (!flag)openCollection();   //anoigma sillogis
                        vector = new VectorModel(index);    //
                        System.out.println("Δώσε το ερώτημα:");
                        query = sc.nextLine();
                        vector.queryProcessing(query);
                        System.out.println("Δώσε το κ");
                        topk = Integer.parseInt(sc.nextLine());
                        System.out.println("Δώσε σύναρτηση ομοιότητας:");
                        similarity = chooseSimilarity();
                        vector.setSimilarityFunc(similarity);
                        startTime = System.currentTimeMillis();
                        results = vector.runRetrievalStrategy();
                        endTime = System.currentTimeMillis();
                        vector.printSimilarDocs(results,topk);
                        System.out.println("\nΧρόνος Επεξεργασίας: " + (endTime-startTime) + " milliseconds");
                        flag = true;
                        break;

                case 4: openCollection();   //anoigma sillogis
                        System.out.println("Δώσε τη διεύθυνση(path) και το όνομα του αρχείου των queries: ");
                        String queries = sc.nextLine();
                        System.out.println("Δώσε τη διεύθυνση(path) και το όνομα του αρχείου των σχετικών εγγράφων: ");
                        String relevant = sc.nextLine();
                        System.out.println("Δώσε topk: (Για όλα δώσε > 2000) ");
                        topk = Integer.parseInt(sc.nextLine());
                        System.out.println("Δώσε σύναρτηση ομοιότητας:");
                        similarity = chooseSimilarity();
                        getResultsfromReferenceCollection(queries,relevant,similarity,topk);
                case 5: break;


                default: System.out.println("Λάθος επιλογή")
                        ;break;

            }
        }while (choice!=5);

    }
    /**
     * sinartisi gia na dialeksei o xristis sinartisi omoiotitas
     * @return ena akeraio pou dilonei tin sinartisi omoiotitas pou epi8imei o xristis
     */
    private static int chooseSimilarity(){
        int similarity;

        printAvailableSimilarityMeasures();
        similarity = Integer.parseInt(sc.nextLine());
        while ((similarity) < 0 || (similarity > 5)){
            System.out.println("Λάθος επιλογή!");
            printAvailableSimilarityMeasures();
            similarity = Integer.parseInt(sc.nextLine());
        }
        return similarity;
    }

    /**
     * sinartisi pou trexei peirama se sillogi anaforas kai ektipwnei ta apotelesmata ka8ws kai tous xronous epeksergasias
     *
     * @param queriesFile   to path kai to onoma tou arxeiou pou periexei ta queries to opoia 8a treksoun gia to peirama
     * @param relevantDocsFile  to path kai to onoma tou arxeiou pou periexei ta sxetika eggrafa gia to ka8e query
     * @param similarityFunc  h sinartisi omoiotitas pou dinei o xristis
     * @param topk ta top k apotelesmata.
     */
    private static void getResultsfromReferenceCollection(String queriesFile, String relevantDocsFile,int similarityFunc,int topk){
        

        //metritis oras ektelesis
        long startTime = System.currentTimeMillis();
        long ignoreTime = 0,ignoreStart,ignoreEnd ;
        //parser
        TRECParser parser = new TRECParser();

        //dimiourgia vector modelou me ton katalogo
        VectorModel vector = new VectorModel(index);
        vector.setSimilarityFunc(similarityFunc);

        //dimiourgia tou hashmap me ta sxetika eggrafa gia ka8e query
        HashMap<Integer,ArrayList<Integer>> relevantDocs = parser.readRelevant(relevantDocsFile);

        Evaluator evaluator = new Evaluator();
        ArrayList<Document> queries = evaluator.getQueries(queriesFile);
        double precision,recall,avgprecision = 0,avgrecall = 0;

        //System.out.println(relevantDocs.get(0));
        int i = 1;
        //gia ka8e query
        for(Document query:queries){
            //pernaei to query sto vector modelo
            vector.queryProcessing(query);
            //trexei to vector modelo kai apo8ikeuei ta apotelesmata sto results
            ArrayList<Result> results = vector.runRetrievalStrategy();
            
            vector.getTopK(results, topk);
            evaluator.getEstimations(relevantDocs.get(i),results);
            precision = evaluator.getPrecision();
            recall = evaluator.getRecall();
            
            //metavliti pou krataei to xrono pou ksekinoun ta i/o
            ignoreStart =System.currentTimeMillis();
            //I/Os
            System.out.println("Ερώτημα με ID: " + query.getID() + "  precision: " + df.format(precision)
                                + "%  recall: " + df.format(recall) + "%\n");
            vector.printSimilarDocs(results);
            //metavliti pou krataei to xrono poy teliwnoun ta i/o
            ignoreEnd = System.currentTimeMillis();
            //metabliti poy pros8etei ton xrono pou xriastikan ta i/o gia na ton afairesei meta
            ignoreTime += ignoreEnd - ignoreStart;

            avgrecall+=recall;
            avgprecision+=precision;
             
            i++;
        }
        avgrecall/=i;
        avgprecision/=i;
        System.out.println("\nΜΕΣΟ precision: " + df.format(avgprecision) + "%\nΜΕΣΟ recall: " + df.format(avgrecall) +"%");
        long endTime = System.currentTimeMillis();

        long timeTaken = endTime - startTime - ignoreTime;
        System.out.println("\nΧρονος επεξεργασίας: " + timeTaken + " milliseconds\n");


    }

    //------------------------------------------------------------------//


    //SINARTISEIS BOOLEAN MONTELOU

    /*i basiki sinartisi pou dimiourgei to boolean modelo*/
    private static void booleanModel(){
        int choice;
        boolean flag = false;
        String query ;

        //elegxos an uparxei anoixti sillogi
        if(index != null) {
            flag = true;
        }

        do{
            printBooleanMenu();
            choice = Integer.parseInt(sc.nextLine());


            switch (choice){
                case 1: openCollection();
                        flag = true;
                    break;

                case 2: if (!flag)  openCollection();   //anoigma sillogis an den uparxei anoixti sillogi
                        BooleanModel booleanModel = new BooleanModel(index);
                        System.out.println("Δώσε το ερώτημα:");
                        query = sc.nextLine();
                        long start = System.currentTimeMillis();
                        booleanModel.queryProcessing(query);
                        long elapsedTime = System.currentTimeMillis() - start;
                        System.out.println("\nΣΧΕΤΙΚΑ ΕΓΓΡΑΦΑ");
                        System.out.println("---------------");
                        System.out.println("Η αναζήτηση διήρκησε περίπου " + elapsedTime + " milliseconds.");
                        booleanModel.printSimilarDocs();
                        flag = true;
                    break;
                case 3:break;
            }

        }while (choice != 3);

    }
    
    //------------------------------------------------------------------//

    //SINARTISEIS SILLOGWN

    private static void Collections(){
        int choice;

        do{
            printCollectionsMenu();
            choice = Integer.parseInt(sc.nextLine());

            switch (choice){

                case 1: newCollection();
                    break;

                case 2: addDocToCollection();
                    break;

                case 3: deleteDocFromCollection();
                    break;

                case 4: saveCollection();
                    break;
                case 5: break;
                default: System.out.println("Λάθος Επιλογή!");
            }

        }while (choice !=5);

    }

    private static void openCollection(){
        String colFilename;
        System.out.println("Δώσε τη διεύθυνση(path) και το όνομα του αρχείου της συλλογής που θες να ανοίξεις: ");
        colFilename = sc.nextLine();
        ixm.setColFilename(colFilename);
        ixm.openCollection();
        index = ixm.getInvertedIndex();
    }

    private static void saveCollection(){
        String colFilename;
        
        System.out.println("Δώσε τη διεύθυνση(path) και το όνομα της συλλογής οπως θες να αποθηκευτεί: ");
        colFilename = sc.nextLine();
        ixm.setColFilename(colFilename);
        ixm.closeCollection();
    }
    private static void newCollection(){
        String colFilename;

        System.out.println("Δώσε τη διεύθυνση(path) και το όνομα του αρχείου το οποίο περιέχει τα έγγραφα(.txt / .pdf / .rtf) : ");
        colFilename = sc.nextLine();
        ixm.setInputFilename(colFilename);
        ixm.build();
        index = ixm.getInvertedIndex();
        //saveCollection();
    }
    private static void addDocToCollection(){
        openCollection();

        String title,content;
        int id;

        System.out.println("Δώσε τον τίτλο:");
        title = sc.nextLine();
        System.out.println("Δώσε το περιεχόμενο:");
        content = sc.nextLine();

        //dinei id ena megalitero apo to teleutaio doc to opoio exei id oso kai to size
        id = index.getDocList().size()+1;

        Document doc = new Document(id,title,content);
        index.add(doc);
        //saveCollection();

    }

    private static void deleteDocFromCollection(){
        //prwta anoigoume tin sillogi stin opoia 8eloume na diagrafei to egrrafo
        openCollection();
        int id;

        System.out.println("Δώσe το ID του εγγράφου που θές να διαγράψεις");
        id = Integer.parseInt(sc.nextLine());
        index.delete(id);
        saveCollection();
    }

    //------------------------------------------------------------------//


    //SINARTISEIS EKTIPWSEIS TWN MENU
    
    //KIRIWS MENU
    private static void printMainMenu(){
        System.out.println("ARES (informAtinon REtrieval System)");
        System.out.println("-----------------------------------");
        System.out.println("1. Συλλογές(Δημιουργία Καινούργιας,Προσθήκη/Διαγραφή εγγράφων)");
        System.out.println("2. Μοντέλο Vector");
        System.out.println("3. Μοντέλο Boolean");
        System.out.println("4. Έξοδος");
        System.out.println("\nΕπιλογή :");
    }

    //MENU SILLOGWN
    private static void printCollectionsMenu(){
        System.out.println("ΣΥΛΛΟΓΕΣ");
        System.out.println("-----------------------------------");
        System.out.println("1. Δημιουργία Νέας Συλλογής Εγγράφων");
        System.out.println("2. Εισαγωγή Εγγράφου Σε Συλλογή");
        System.out.println("3. Διαγραφή Εγγράφου  Απο Συλλογή");
        System.out.println("4. Κλείσιμο/Αποθήκευση Συλλογής");
        System.out.println("5. Έξοδος(Αρχικό Μενού)");
        System.out.println("\nΕπιλογή :");

    }

    //MENU BOOLEAN
    private static void printBooleanMenu(){
        System.out.println("ΜΟΝΤΕΛΟ BOOLEAN");
        System.out.println("--------------");
        System.out.println("1. Eπιλογή/Αλλαγή Συλλογής");
        System.out.println("2. Αναζήτηση όλων των σχετικών εγγράφων απο τη συλλογή με ερώτημα");
        System.out.println("3. Αρχικό Μενού");
        System.out.println("\nΕπιλογή :");

    }

    //MENU VECTOR
    private static void printVectorMenu(){
        System.out.println("ΜΟΝΤΕΛΟ VECTOR");
        System.out.println("--------------");
        System.out.println("1. Επιλογή/Αλλαγή Συλλογής (Για επιλογές 2 & 3 )");
        System.out.println("2. Αναζήτηση όλων των σχετικών εγγράφων απο συλλογή με ερώτημα");
        System.out.println("3. Αναζήτηση των top-κ σχετικών εγγράφων απο συλλογή με ερώτημα");
        System.out.println("4. Εκτέλεση πειράματος σε συλλογή αναφοράς");
        System.out.println("5. Αρχικό Μενού");
        System.out.println("\nΕπιλογή :");

    }

    //MENU SINARTISEWN OMOIOTITAS TOU VECTOR
    private static void printAvailableSimilarityMeasures(){
        System.out.println("Συναρτήσεις Ομοιότητας");
        System.out.println("----------------------");
        System.out.println("1.Συνημίτονο");
        System.out.println("2.Εσωτερικό γινόμενο");
        System.out.println("3.Μέθοδος Dice");
        System.out.println("4.Μέθοδος Επικάλυψης");
        System.out.println("5.Μέθοδος Jaccard");
        System.out.println("\nΕπιλογή:");
    }

}
