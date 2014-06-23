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
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;


/**
 * 
 * A class that implements an rtf parser that returns plain text.
 *
 * uses the RTFEditorKit, Document libraries of Java Swing.
 *
 * @author Terminal
 */
public class RTFTextParser {

    /**
     * Imports text from an rtf file.
     * Returns a string with plain text.
     *
     * @param fileName
     * @return plainText
     */
    public String rtftoText(String fileName){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(fileName));
            RTFEditorKit kit = new RTFEditorKit();
            Document doc = kit.createDefaultDocument();

            // reads the input stream and transform it into a document
            kit.read(fis, doc, 0);
            // transfrom the document into plain text
            String plainText = doc.getText(0, doc.getLength());

            return plainText;
        } catch (IOException ex) {
            Logger.getLogger(RTFTextParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadLocationException ex) {
            Logger.getLogger(RTFTextParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(RTFTextParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
