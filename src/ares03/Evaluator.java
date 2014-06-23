/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ares03;

import ares03.VectorModel.Result;
import java.util.ArrayList;

public class Evaluator {

    private TRECParser qParser = new TRECParser();
    private ArrayList<Document> queries;
    private double precision = 0;
    private double recall = 0;

    public Evaluator() {
    }

    public ArrayList<Document> getQueries(String inputFileName) {
        queries = qParser.readDocs(inputFileName);
        return queries;

    }

    public void getEstimations(ArrayList<Integer> relevant, ArrayList<Result> results) {

        int count = 0; // counts similar documents

        // if a doc is similar the counter increases
        for (int i = 0; i < results.size(); i++) {
            for (int j = 0; j < relevant.size(); j++) {
                int resultDocID = results.get(i).getDocument().getID();
                int relevantDocID = relevant.get(j);

                if (relevantDocID == resultDocID) {
                    count += 1;
                    //continue;
                }
            }
        }

        recall = 100 * count / (double) relevant.size();
        precision = 100 * count / (double) results.size();

    }

    public double getRecall() {
        return this.recall;
    }

    public double getPrecision() {
        return this.precision;
    }
}
