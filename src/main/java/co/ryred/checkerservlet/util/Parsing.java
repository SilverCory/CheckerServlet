package co.ryred.checkerservlet.util;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 16/10/2015.
 */
public class Parsing
{

	public static int parseInt( String string ) {

		int integer;
		
		try {
			integer = Integer.parseInt( string );
		} catch ( Exception e ) {
			integer = 0;
		}

		return integer;

	}

	public static long parseLong( String string ) {

		long longVal;

		try {
			longVal = Long.parseLong( string );
		} catch ( Exception e ) {
			longVal = 0;
		}

		return longVal;

	}

}
