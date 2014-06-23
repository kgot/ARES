/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ares03;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 * A class that implements a pdf parser that returns palin text.
 *
 * Uses the pdfbox, fontbox, jempbox, commons-logging Apache libraries.
 *
 * @author kgot
 */
public class PDFTextParser {

    /**
     * Imports text from pdf file.
     * Returns a string with the parsed text.
     *
     * @param fileName
     * @return parsedText
     */
    public String pdftoText(String fileName) {
		PDFParser parser;
		String parsedText = "";
		PDFTextStripper pdfStripper = null;
		PDDocument pdDoc = null;
		COSDocument cosDoc = null;
		File file = new File(fileName);
                
		if (!file.isFile()) {
			System.out.println("Το αρχείο " + fileName + " δεν υπάρχει.");
			return null;
		}

		try {
			parser = new PDFParser(new FileInputStream(file));
		} catch (IOException e) {
			Logger.getLogger(PDFTextParser.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
                
		try {
			parser.parse();
			cosDoc = parser.getDocument();
			pdfStripper = new PDFTextStripper();
			pdDoc = new PDDocument(cosDoc);
                        // transform to text
			parsedText = pdfStripper.getText(pdDoc);
		} catch (Exception e) {
			Logger.getLogger(PDFTextParser.class.getName()).log(Level.SEVERE, null, e);
		} finally {
                            try {
                                if (cosDoc != null) {
                                    cosDoc.close();
                                }
                                if (pdDoc != null) {
                                    pdDoc.close();
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(PDFTextParser.class.getName()).log(Level.SEVERE, null, ex);
                            }
		}
		return parsedText;
	}

}
