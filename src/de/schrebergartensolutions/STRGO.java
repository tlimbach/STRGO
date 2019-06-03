/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.schrebergartensolutions;

import de.schrebergartensolutions.MethodeFinder.MethodInfo;
import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    List<? extends Line> lines;

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
        MethodeFinder finder = new MethodeFinder();
        List<MethodInfo> mInfos = new ArrayList<>();

        Node[] arr = TopComponent.getRegistry().getCurrentNodes();
        for ( int i = 0; i < arr.length; i++ )
        {
            ec = arr[i].getLookup().lookup( EditorCookie.class );
            if ( ec != null )
            {
                JEditorPane[] panes = ec.getOpenedPanes();

                if ( panes != null )
                {
                    try
                    {
                        lines = ec.getLineSet().getLines();

                        for ( int t = 0; t < lines.size(); t++ )
                        {
                            Line line = lines.get( t );
                            MethodInfo res = finder.getMethodForLine( line.getText() );

                            if ( !mInfos.contains( res ) )
                            {
                                mInfos.add( res );
                                map.put( res.getDisplayText(), line.getLineNumber() );
                            }
                        }

                    }
                    catch ( Exception ex )
                    {
                        Exceptions.printStackTrace( ex );
                    }
                }
            }
        }

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
            Collections.sort( mInfos );
            for ( MethodInfo m : mInfos )
            {
                if ( m.getType() != MethodeFinder.METHOD_TYPE.NONE )
                {
                    l1.addElement( m.getDisplayText() );
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
                        Collections.sort( mInfos );
                        for ( MethodInfo m : mInfos )
                        {
                            if ( m.getType() != MethodeFinder.METHOD_TYPE.NONE )
                            {
                                if ( isContained( searchTxt, m.getName() ) )
                                {
                                    System.out.println( "ading element .. ." + m.getDisplayText() );
                                    l1.addElement( m.getDisplayText() );
                                }
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

//    protected boolean isContained( String searchTxt, String textLine )
//    {
//        String[] searchies = searchTxt.split( " " );
//
//        for ( int t = 0; t < searchies.length; t++ )
//        {
//            if ( !textLine.toLowerCase().contains( searchies[t].toLowerCase() ) )
//            {
//                return false;
//            }
//        }
//        return true;
//    }
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
