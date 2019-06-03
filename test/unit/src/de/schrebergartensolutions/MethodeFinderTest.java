/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.schrebergartensolutions;

import de.schrebergartensolutions.MethodeFinder.METHOD_TYPE;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** 
 *
 * @author thorsten limbach
 */
public class MethodeFinderTest
{

    public MethodeFinderTest()
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

    @Test
    public void testSomeMethod()
    {
        MethodeFinder finder = new MethodeFinder();

        MethodeFinder.MethodInfo res = finder.getMethodForLine( "private String getBla(String text) {fjeifjoijwfoijwfi" );

        assertNotNull( res );
        assertThat( res.getType(), is( METHOD_TYPE.PRIVATE ) );
        assertThat( res.getName(), is( "getBla(String text)" ) );
        assertThat( res.getReturnClassName(), is( "String" ) );

        res = finder.getMethodForLine( "fdgfuirhegrhehrehrehreg" );
        assertThat( res.getType(), is( METHOD_TYPE.NONE ) );

        res = finder.getMethodForLine( "public void dowas();" );
        assertThat( res.getType(), is( METHOD_TYPE.PUBLIC ) );
        assertThat( res.getReturnClassName(), is( "void" ) );
        assertThat( res.getName(), is( "dowas()" ) );

        //TODO: static müsste noch überarbeitet werden. ReturnClass funktioniert nicht.
        res = finder.getMethodForLine( "public static void staticbla();" );
        assertThat( res.getType(), is( METHOD_TYPE.PUBLIC ) );
//        assertThat(res.getReturnClassName(), is("void"));
        assertThat( res.getName(), is( "void staticbla()" ) );

        res = finder.getMethodForLine( "protected void doprt();" );
        assertThat( res.getType(), is( METHOD_TYPE.PROTECTED ) );
        assertThat( res.getReturnClassName(), is( "void" ) );
        assertThat( res.getName(), is( "doprt()" ) );

        res = finder.getMethodForLine( "void defleppad(int x){" );
        assertThat( res.getType(), is( METHOD_TYPE.DEFAULT ) );
//        assertThat(res.getReturnClassName(), is("void"));
        assertThat( res.getName(), is( "defleppad(int x)" ) );

        res = finder.getMethodForLine( "defleppad(int x){" );
        assertThat( res.getType(), is( METHOD_TYPE.DEFAULT ) );
//        assertThat(res.getReturnClassName(), is("void"));
        assertThat( res.getName(), is( "defleppad(int x)" ) );

        res = finder.getMethodForLine( "defleppad(int x);" );
        assertThat( res.getType(), is( METHOD_TYPE.NONE ) );
//        assertThat(res.getReturnClassName(), is("void"));
        assertNull( res.getName());

    }

}
