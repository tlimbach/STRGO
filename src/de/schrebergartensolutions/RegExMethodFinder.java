/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.schrebergartensolutions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author tlimbach
 */
public class RegExMethodFinder
{
    String regEx ="^[ \\t]*(?:(?:public|protected|private)\\s+)?(?:(static|final|native|synchronized|abstract|threadsafe|transient|(?:<[?\\w\\[\\] ,&]+>)|(?:<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>)|(?:<[^<]*<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>[^>]*>))\\s+){0,}(?!return)\\b([\\w.]+)\\b(?:|(?:<[?\\w\\[\\] ,&]+>)|(?:<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>)|(?:<[^<]*<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>[^>]*>))((?:\\[\\]){0,})\\s+\\b\\w+\\b\\s*\\(\\s*(?:\\b([\\w.]+)\\b(?:|(?:<[?\\w\\[\\] ,&]+>)|(?:<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>)|(?:<[^<]*<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>[^>]*>))((?:\\[\\]){0,})(\\.\\.\\.)?\\s+(\\w+)\\b(?![>\\[])\\s*(?:,\\s+\\b([\\w.]+)\\b(?:|(?:<[?\\w\\[\\] ,&]+>)|(?:<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>)|(?:<[^<]*<[^<]*<[?\\w\\[\\] ,&]+>[^>]*>[^>]*>))((?:\\[\\]){0,})(\\.\\.\\.)?\\s+(\\w+)\\b(?![>\\[])\\s*){0,})?\\s*\\)(?:\\s*throws [\\w.]+(\\s*,\\s*[\\w.]+))?\\s*(?:\\{|;)[ \\t]*$";

    private String removeThrows( String line )
    {
        String res = line;
        int idx = line.indexOf( "throws");
        
        if (idx > -1) {
            res = res.substring( 0, idx) + "{";
        }
        
        return res;
    }
    
    protected String findMethode( String line )
    {
        line = line.replaceAll( "final", ""); // packt die RegEx nicht
        
        line = removeThrows(line); // packt die RegEx auch nicht .. 
        
        Matcher matcher = Pattern.compile( regEx ).matcher( line );
        return matcher.find() ? matcher.group() : null;
    }
}
