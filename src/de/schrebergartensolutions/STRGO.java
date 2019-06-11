/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.schrebergartensolutions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@ActionID(
        category = "Edit",
        id = "de.schrebergartensolutions.STRGO"
)
@ActionRegistration(
        iconBase = "de/schrebergartensolutions/lupe.png",
        displayName = "#CTL_STRGO"
)
@ActionReferences(
        {
            @ActionReference(path = "Menu/Source", position = 200),
            @ActionReference(path = "Shortcuts", name = "DA-P"),
            @ActionReference(path = "Shortcuts", name = "DA-M")
        })
@Messages("CTL_STRGO=STRGO")
public final class STRGO implements ActionListener
{

    List<Line> lines;

    JScrollPane spList;
    JScrollPane spPreview;
    private JTextArea searchText;
    JDialog dialog;
    EditorCookie ec = null;
    JList list;
    Map<String, Integer> map = new HashMap<>();
    JTextPane preview = new JTextPane();

    @Override
    public void actionPerformed( ActionEvent e )
    {
        map = new HashMap<>();
        doit();

    }

    private void buildUi( List<String> mInfos )
    {
        try
        {
            dialog = new JDialog();
            JPanel innerPanel = new JPanel();
            innerPanel.setLayout( new BorderLayout() );
            dialog.setModal( true );
            dialog.setLayout( new BorderLayout() );
            DefaultListModel<String> l1 = new DefaultListModel<>();
            list = new JList( l1 );
//            list.setFont( new Font( Font.MONOSPACED, Font.PLAIN, 14 ) );
            list.setFont( list.getFont().deriveFont( 12f ) );

            for ( String m : mInfos )
            {
                if ( !l1.contains( m ) )
                {
                    l1.addElement( m );
                }
            }

            list.addMouseListener( new MouseAdapter()
            {

                @Override
                public void mouseClicked( MouseEvent e )
                {
                    if ( e.getClickCount() == 2 )
                    {
                        showCode();
                    }
                }

            } );

            list.addListSelectionListener( new ListSelectionListener()
            {

                @Override
                public void valueChanged( ListSelectionEvent e )
                {
                    final Object selectedValue = list.getSelectedValue();

                    if ( selectedValue == null )
                    {
                        return;
                    }

                    String res = (String) selectedValue;

                    if ( res.equals( "null" ) )
                    {
                        return;
                    }

                    int line = -1;
                    try
                    {
                        line = map.get( res );
                    }
                    catch ( Exception ex )
                    {
                        System.err.println( "Keine Zeile in Map für >" + res + "<" );
                        ex.printStackTrace();
                    }

                    if ( line == -1 )
                    {
                        return;
                    }

                    int lowerBound = line - 12;
                    if ( lowerBound < 0 )
                    {
                        lowerBound = 0;
                    }

                    String previewText = "<HTML>";
                    previewText += "<i>(Zeile " + ( line + 1 ) + ")</i><BR>";

                    for ( int t = 0; t < 24; t++ )
                    {

                        if ( lowerBound + t >= lines.size() )
                        {
                            break;
                        }

                        String text = lines.get( lowerBound + t ).getText();
                        text = text.replaceAll( " ", "&nbsp;" );

                        if ( t + lowerBound == line )
                        {
                            previewText += "<b>" + text + "</b>";
                        }
                        else
                        {
                            previewText += text;
                        }

                        previewText += "<BR>";
                    }

                    previewText += "</HTML>";

                    preview.setText( previewText );

                }
            } );

            list.addKeyListener( new KeyAdapter()
            {

                @Override
                public void keyPressed( KeyEvent e )
                {
                    if ( e.getKeyCode() == 27 ) // Escape
                    {
                        dialog.dispose();
                    }
                    if ( e.getKeyCode() == 10 ) // Enter
                    {
                        showCode();
                    }

                    if ( e.getKeyCode() != 38 && e.getKeyCode() != 40 )
                    {
                        searchText.requestFocus();
                        searchText.setText( searchText.getText() + e.getKeyChar() );
                    }
                    super.keyPressed( e );
                }

            } );
            spList = new JScrollPane( list );

            innerPanel.add( spList, BorderLayout.CENTER );
            preview.setContentType( "text/html" );
            preview.setEditable( false );

            spPreview = new JScrollPane( preview );
            innerPanel.add( spPreview, BorderLayout.EAST );
            searchText = new JTextArea();
//            searchText.setPreferredSize( new Dimension(400, 800));
            searchText.addKeyListener( new KeyAdapter()
            {

                @Override
                public void keyReleased( KeyEvent e )
                {
                    if ( e.getKeyCode() == 10 )
                    {
                        if ( l1.getSize() == 1 )
                        {
                            list.setSelectedIndex( 0 );
                            showCode();
                        }
                    }
                    if ( e.getKeyCode() == 27 )
                    {
                        dialog.dispose();
                    }
                    if ( e.getKeyCode() == 38 || e.getKeyCode() == 40 )
                    {
                        list.requestFocus();

                        int idx = list.getSelectedIndex();

                        if ( idx == -1 )
                        {
                            idx = 0;
                        }
                        else
                        {
                            idx += e.getKeyCode() == 38 ? 1 : -1;
                        }

                        if ( idx < 0 )
                        {
                            idx = 0;
                        }

                        if ( idx > l1.getSize() - 1 )
                        {
                            idx = l1.getSize() - 1;
                        }

                        list.setSelectedIndex( idx );
                    }
                    else
                    {
                        String searchTxt = searchText.getText();
                        System.out.println( "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx " + mInfos.size() );
                        l1.clear();
                        sort( mInfos );
                        for ( String m : mInfos )
                        {
                            if ( isContained( searchTxt, m ) )
                            {
                                System.out.println( "ading element .. ." + m );
                                l1.addElement( m );
                            }
                        }
                    }
                }

            } );
            innerPanel.add( searchText, BorderLayout.SOUTH );
            searchText.requestFocus();
            spList.setPreferredSize( new Dimension( 800, 500 ) );
            spPreview.setPreferredSize( new Dimension( 500, 500 ) );
            dialog.setUndecorated( true );
            Border veryVeryOuterBorder = BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( Color.lightGray, 1 ), BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
            Border veryOuterBorder = BorderFactory.createCompoundBorder( veryVeryOuterBorder, BorderFactory.createLineBorder( Color.GRAY, 2 ) );
            Border outerBorder = BorderFactory.createCompoundBorder( veryOuterBorder, BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
            innerPanel.setBorder( BorderFactory.createTitledBorder( outerBorder, "Search method...", TitledBorder.CENTER, TitledBorder.DEFAULT_JUSTIFICATION ) );
            dialog.add( innerPanel, BorderLayout.CENTER );
            dialog.pack();

            dialog.setLocation( 300, 200 );
            dialog.setVisible( true );
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }
    }

    private String doHtml( String methodRumpf )
    {
        String newMethodRumpf = "<html>";
        int idxKlammerAuf = methodRumpf.indexOf( "(" );
        int idxMethodStart = idxKlammerAuf;
        while ( true )
        {
            idxMethodStart--;
            if ( methodRumpf.charAt( idxMethodStart ) == ' ' )
            {
                newMethodRumpf += methodRumpf.substring( 0, idxMethodStart + 1 );
                newMethodRumpf += "<b>";
                newMethodRumpf += methodRumpf.substring( idxMethodStart + 1, methodRumpf.length() );
                newMethodRumpf += "</b>";
                newMethodRumpf += "</html>";
                break;
            }

        }
        return newMethodRumpf;
    }

    Component componentForCursor = null;

    private void sort( List<String> mInfos )
    {
        Collections.sort( mInfos, new Comparator<String>()
        {

            @Override
            public int compare( String o1, String o2 )
            {
                return o1.substring( o1.indexOf( "<b>" ) ).toLowerCase().compareTo( o2.substring( o2.indexOf( "<b>" ) ).toLowerCase() );
            }
        } );
    }

    private void doit()
    {

        Node[] arr = TopComponent.getRegistry().getCurrentNodes();
        for ( int i = 0; i < arr.length; i++ )
        {
            ec = arr[i].getLookup().lookup( EditorCookie.class );
            if ( ec != null )
            {
                JEditorPane[] panes = ec.getOpenedPanes();

                if ( panes != null )
                {
                    componentForCursor = panes[0];
                    componentForCursor.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
                    try
                    {
                        new Thread()
                        {
                            public void run()
                            {
                                final List<String> mInfos = readMethods();

                                sort( mInfos );

                                SwingUtilities.invokeLater( new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        componentForCursor.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                                        buildUi( mInfos );
                                    }
                                } );
                            }

                        }.start();

                    }
                    catch ( Exception ex )
                    {
                        Exceptions.printStackTrace( ex );
                        componentForCursor.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                    }
                }
            }
        }

    }

    public List<String> readMethods()
    {
        RegExMethodFinder finder = new RegExMethodFinder();
        List<String> mInfos = new ArrayList<>();
        lines = (List<Line>) ec.getLineSet().getLines();
        Map<Line, String> lineTexts = new LineFinder( lines ).getSourcecodeLinesFor();

        Set<Map.Entry<Line, String>> entrySet = lineTexts.entrySet();
        Iterator<Map.Entry<Line, String>> it = entrySet.iterator();
        while ( it.hasNext() )
        {
            Map.Entry<Line, String> entry = it.next();
            Line line = entry.getKey();
            String sourceCodeLine = entry.getValue();

            if ( sourceCodeLine == null )
            {
                continue;
            }

            String methodRumpf = finder.findMethode( sourceCodeLine );

            if ( methodRumpf != null && !mInfos.contains( methodRumpf ) )
            {
                methodRumpf = methodRumpf.replace( "{", "" ).trim();
                methodRumpf = doHtml( methodRumpf );
                mInfos.add( methodRumpf );

                if ( !map.containsKey( methodRumpf ) )
                {
                    map.put( methodRumpf, line.getLineNumber() );
                }
            }
        }

        return mInfos;
    }

    protected boolean isContained( String searchTxt, String textLine )
    {

        return Arrays.asList( searchTxt.split( " " ) ).stream().map( s -> s.toLowerCase() ).filter( s -> !textLine.toLowerCase().contains( s ) ).collect( Collectors.toSet() ).size() == 0;

    }

    private void showCode()
    {
        int line = map.get( (String) list.getSelectedValue() );
        ec.getLineSet().getCurrent( line ).show( Line.SHOW_TRY_SHOW );
        dialog.dispose();
    }
}
