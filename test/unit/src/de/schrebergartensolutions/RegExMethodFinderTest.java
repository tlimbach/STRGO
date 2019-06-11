/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.schrebergartensolutions;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author tlimbach
 */
public class RegExMethodFinderTest
{

    public RegExMethodFinderTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of findMethode method, of class RegExMethodFinder.
     */
    @Test
    public void testFindMethode()
    {
        RegExMethodFinder finder = new RegExMethodFinder();
        String res = finder.findMethode( "public void say(String name) {" );

        System.out.println( "Method found:" + res );
        res = finder.findMethode( "private String getBla(String text) {" );
        System.out.println( "Method found:" + res );
        res = finder.findMethode( "fdgfuirhegrhehrehrehreg" );
        System.out.println( "Method found:" + res );
        res = finder.findMethode( "public void dowas();" );
        System.out.println( "Method found:" + res );
        res = finder.findMethode( "dowas()" );
        System.out.println( "Method found:" + res );
        res = finder.findMethode( "public static void staticbla();" );
        System.out.println( "Method found:" + res );
        res = finder.findMethode( "protected void doprt();" );
        System.out.println( "Method found:" + res );
        res = finder.findMethode( "void defleppad(int x){" );
        System.out.println( "Method found:" + res );
          res = finder.findMethode( "String defleppad(int x){" );
        System.out.println( "Method found:" + res );
        res = finder.findMethode( "defleppad(int x){" );
        System.out.println( "Method found:" + res );
        res = finder.findMethode( "defleppad(int x);" );
        System.out.println( "Method found:" + res );
        res = finder.findMethode( "List<? extends Line> getLinesFor( List<? extends Line> lines ) {" );
        System.out.println( "Method found:" + res );
//        res = finder.findMethode( "private static List<String> getGroupNamesForSubtotalToBeKept( final List<JrToolsParameter> jrToolsParameters ) {" );
        res = finder.findMethode( "private static List<String> getGroupNamesForSubtotalToBeKept( final List<JrToolsParameter> jrToolsParameters ) {" );
        System.out.println( "Method found:" + res );
        res = finder.findMethode( " public GenericDataSet[] callFunction( final String functionName, final List<Object> bindings ) throws TechnicalException  {" );
        System.out.println( "Method found:" + res );
    }

}
