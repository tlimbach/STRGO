/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.schrebergartensolutions;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author thorsten limbach
 */
class MethodeFinder
{

    enum METHOD_TYPE
    {

        NONE( null ), PUBLIC( "public" ), PROTECTED( "protected" ), DEFAULT( "" ), PRIVATE( "private" );

        private String name;

        private METHOD_TYPE( String name )
        {
            this.name = name;
        }

        public String getName()
        {
            return this.name;
        }
    }

    public MethodInfo getMethodForLine( String line )
    {
        MethodInfo mi = searchMethod( METHOD_TYPE.PUBLIC, line );
        if ( mi != null )
        {
            return mi;
        }

        mi = searchMethod( METHOD_TYPE.PRIVATE, line );
        if ( mi != null )
        {
            return mi;
        }

        mi = searchMethod( METHOD_TYPE.PROTECTED, line );
        if ( mi != null )
        {
            return mi;
        }

        mi = searchMethod( METHOD_TYPE.DEFAULT, line );
        if ( mi != null )
        {
            return mi;
        }

        return new MethodInfo( METHOD_TYPE.NONE, null, null );
    }

    private MethodInfo searchMethod( METHOD_TYPE type, String line )
    {
        if ( type == METHOD_TYPE.DEFAULT )
        {
            int counter = 0;
            for ( char c : line.toCharArray() )
            {
                if ( "\t".equals( "" + c ) || " ".equals( "" + c ) )
                {
                    counter++;
                }
            }

            if ( counter < 5 && !line.trim().endsWith( ";" ) )
            {
                if ( line.trim().endsWith( "{" ) || line.trim().endsWith( ")" ) )
                {

                    Matcher matcher = Pattern.compile( ".*\\(.*\\)" ).matcher( line );
                    while ( matcher.find() )
                    {
                        final String group = matcher.group();

                        System.out.printf( "xxxxx*>        %s an Position [%d,%d]%n", group, matcher.start(), matcher.end() );
                        String[] parts = group.split( " " );
                        String methodeName = group.trim();

                        int idxKlammerAuf = methodeName.indexOf( "(" );
                        int idxLeerzeichen = methodeName.indexOf( " " );

                        if ( idxLeerzeichen > -1 && idxLeerzeichen < idxKlammerAuf )
                        {
                            methodeName = methodeName.substring( idxLeerzeichen ).trim();
                        }

                        return new MethodInfo( type, methodeName, null );
                    }
                }
            }
            return null;
        }
        else
        {

            Matcher matcher = Pattern.compile( type.getName() + ".*\\)" ).matcher( line );
            while ( matcher.find() )
            {
                final String group = matcher.group();
                System.out.printf( "**********************>        %s an Position [%d,%d]%n", group, matcher.start(), matcher.end() );
                String[] parts = group.split( " " );
                String returnClassType = parts[1];
                String methodeName = group.substring( parts[0].length() + parts[1].length() + 1 ).trim();
                final MethodInfo methodInfo = new MethodInfo( type, methodeName, returnClassType );
                return methodInfo;
            }

            return null;
        }
    }

    public static class MethodInfo implements Comparable<MethodInfo>
    {

        private final String returnClassName;

        private final METHOD_TYPE type;
        private final String name;

        public MethodInfo( METHOD_TYPE type, String name, String returnClassName )
        {
            this.type = type != null ? type : METHOD_TYPE.NONE;
            this.name = "" + name;
            this.returnClassName = "" + returnClassName;

        }

        @Override
        public int compareTo( MethodInfo o )
        {
            return this.getName().compareTo( o.getName() );
        }

        public String getName()
        {
            return name;
        }

        public METHOD_TYPE getType()
        {
            return type;
        }

        public String getReturnClassName()
        {
            return returnClassName;
        }

        public String getDisplayText()
        {
            return "<HTML>" + this.getType().toString().toLowerCase() + " " + this.getReturnClassName() + " <b>" + this.getName() + "</b></HTML>";
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 79 * hash + Objects.hashCode( this.returnClassName );
            hash = 79 * hash + Objects.hashCode( this.type );
            hash = 79 * hash + Objects.hashCode( this.name );
            return hash;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( obj == null )
            {
                return false;
            }
            if ( getClass() != obj.getClass() )
            {
                return false;
            }
            final MethodInfo other = (MethodInfo) obj;
            if ( !Objects.equals( this.returnClassName, other.returnClassName ) )
            {
                return false;
            }
            if ( this.type != other.type )
            {
                return false;
            }
            if ( !Objects.equals( this.name, other.name ) )
            {
                return false;
            }
            return true;
        }

    }
}
