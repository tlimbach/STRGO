/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.schrebergartensolutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.text.Line;

/**
 *
 * @author tlimbach
 */
class LineFinder
{

    private List<Line> lines = new ArrayList<>();

    public LineFinder( List<Line> lines )
    {
        this.lines = lines;
    }

    public Map<Line, String> getSourcecodeLinesFor()
    {
        Map<Line, String> lineMap = new HashMap<>();

        for ( int t = 0; t < lines.size(); t++ )
        {
            String methodStart = findNextMethodStart( t );
            if ( methodStart != null )
            {
                lineMap.put( lines.get( t ), methodStart );
            }
        }

        return lineMap;
    }

    private String findNextMethodStart( int lineNumber )
    {
        String code = "";

        int no = 0;

        // nur die nächsten 20 Zeilen prüfen. Wenn die öffende Klammer noch später kommt, wtf....
        while ( no < 20 && lineNumber + no < lines.size() - 1 )
        {
            code += lines.get( lineNumber + no ).getText();

            if ( code.contains( "{" ) )
            {
                return code;
            }

            no++;
        }

        return null;
    }

}
