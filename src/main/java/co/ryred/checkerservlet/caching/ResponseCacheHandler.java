package co.ryred.checkerservlet.caching;

import co.ryred.checkerservlet.CheckerServlet;
import co.ryred.checkerservlet.CheckerServletConfig;
import co.ryred.checkerservlet.spigot.NameResponse;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Created by Cory Redmond on 18/12/15.
 *
 * @author Cory Redmond <ace@ac3-servers.eu>
 */
public class ResponseCacheHandler {

	public static NameResponse readCache( int id, boolean resource ) {

		File file = getFile( id, resource );
		if ( !file.exists() ) return null;

		try ( FileReader fr = new FileReader( file ) ) {
			ResponseCache resp = CheckerServlet.full_gson.fromJson( fr, ResponseCache.class );
			if ( !resp.isExpired() ) return resp.getResponse();
		} catch ( java.io.IOException e ) {
		}

		return null;

	}

	public static void putCache( NameResponse resp, boolean resource ) {

		ResponseCache respCache = new ResponseCache( resp, System.currentTimeMillis() );

		File file = getFile( resp.getId().getId(), resource );
		if ( file.exists() ) file.delete();

		try ( FileWriter fw = new FileWriter( file ) ) {
			CheckerServlet.full_gson.toJson( respCache, fw );
		} catch ( Exception ex ) {
		}

	}

	public static File getFile( int id, boolean resource ) {
		String name = "resp_" + ( resource ? "res" : "usr" ) + "_" + id + ".json";
		File file = new File( new File( CheckerServletConfig.cacheDir, "responses" ), name );
		file.getParentFile().mkdirs();
		return file;
	}

}
