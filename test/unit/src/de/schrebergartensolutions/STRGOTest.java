/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.schrebergartensolutions;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author thorsten limbach
 */
public class STRGOTest
{

    STRGO strgo = new STRGO();

    public STRGOTest()
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
     * Test of actionPerformed method, of class STRGO.
     */
    @Test
    public void testIsContained1() throws Exception
    {
        assertTrue( strgo.isContained( "aaa", "aaa" ) );
    }

    @Test
    public void testIsContained2() throws Exception
    {
        assertTrue( strgo.isContained( "aaa", "aaac" ) );
    }

    @Test
    public void testIsContained3() throws Exception
    {
        assertTrue( strgo.isContained( "aaa", "caaa" ) );
    }

    @Test
    public void testIsContained4() throws Exception
    {
        assertFalse( strgo.isContained( "aaa", "aa a" ) );
    }

    @Test
    public void testIsContained5() throws Exception
    {
        assertTrue( strgo.isContained( "a aa", "aaa" ) );
    }

    @Test
    public void testIsContained6() throws Exception
    {
        assertTrue( strgo.isContained( "get n int", "getNextInteger(" ) );
    }

    @Test
    public void testIsContained7() throws Exception
    {
        assertTrue( strgo.isContained( "s p v", "setPropertyValue" ) );
    }
    
    @Test
    public void testIsContained8() throws Exception
    {
        assertTrue( strgo.isContained( "s p v", "set (propertyValue)" ) );
    }
    @Test
    public void testIsContained9() throws Exception
    {
        assertFalse( strgo.isContained( "s d v", "setPropertyValue" ) );
    }
    
    @Test
    public void testIsContained10() throws Exception
    {
        assertTrue( strgo.isContained( "setp v", "setPropertyValue" ) );
    }
    
    @Test
    public void testIsContained11() throws Exception
    {
        assertTrue( strgo.isContained( "setp v", "setPropertyValue" ) );
    }
    
    @Test
    public void testIsContained12() throws Exception
    {
        assertTrue( strgo.isContained( "setp va", "setProperty (String value){" ) );
    }
    
    @Test
    public void testIsContained13() throws Exception
    {
        assertTrue( strgo.isContained( "setp v", "setPropertyValue" ) );
    }
    
    @Test
    public void testIsContained14() throws Exception
    {
        assertFalse( strgo.isContained( "aaasetp v", "setPropertyValue" ) );
    }
    
    @Test
    public void testIsContained15() throws Exception
    {
        assertFalse( strgo.isContained( "setp v b", "setPropertyValue" ) );
    }
    

}
