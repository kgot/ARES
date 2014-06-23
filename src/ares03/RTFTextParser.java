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
 * Klasi pou ilopoiei tin anagnosi keimenou apo arheio rtf
 * kai to epistrefei se text morfi gia epexergasia.
 *
 * Hrisimopoiei tis vivliothikes RTFEditorKit, Document tou Java Swing.
 *
 * @author Terminal
 */
public class RTFTextParser {

    /**
     * Kanei exagogi keimenou apo arheio rtf to opoio periehei keimeno.
     * Epistrefei to parsarismeno keimeno se String.
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

            //diavazei to reuma arheiou kai to metatrepei se Document
            kit.read(fis, doc, 0);
            //metatrepei to Document se aplo keimeno
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
