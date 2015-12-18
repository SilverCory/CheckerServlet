package co.ryred.checkerservlet;

import be.maximvdw.spigotsite.SpigotSiteCore;
import be.maximvdw.spigotsite.api.SpigotSite;
import be.maximvdw.spigotsite.api.resource.Resource;
import be.maximvdw.spigotsite.api.user.User;
import co.ryred.checkerservlet.caching.ResponseCache;
import co.ryred.checkerservlet.caching.ResponseCacheHandler;
import co.ryred.checkerservlet.spigot.ID;
import co.ryred.checkerservlet.spigot.NameResponse;
import co.ryred.checkerservlet.servers.Server;
import co.ryred.checkerservlet.servers.dao.impl.IServerBean;
import co.ryred.checkerservlet.util.CooldownUtil;
import co.ryred.checkerservlet.util.Parsing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 15/09/2015.
 */
@SuppressWarnings("unchecked")
public class CheckerServlet extends HttpServlet
{

	public static final Gson full_gson = new Gson();
	private final BeanFactory context;

	private static int mintick = 0;
	private static ArrayList<String> knownIps = new ArrayList<>();

	public CheckerServlet() throws Exception
	{

		CheckerServletConfig.init();

		try {
			new SpigotSiteCore();
		}catch (Exception ex) {}

		/*Configuration cfg = new Configuration();
		cfg.addAnnotatedClass( User.class );
		cfg.configure( PlayerServletConfig.configFile );
		this.sessionFactory = cfg.buildSessionFactory();*/

		new Thread(() -> {

            while ( true ) {

                try {
                    Thread.sleep( TimeUnit.MINUTES.toMillis( 1 ) );
                } catch ( Exception e ) {}

				if( mintick % 5 == 0 ) {
					try {
						CooldownUtil.getCooldown().prune();
					} catch (Exception e) {
					}
				}

				if( mintick % 20 == 0 ) {
					try {
						URL url = new URL( CheckerServletConfig.ips_url );
						knownIps = full_gson.fromJson(new InputStreamReader(url.openStream()), new TypeToken<ArrayList<String>>() {
						}.getType());
					} catch (Exception ex) {}
				}

            }

        }, "CooldownPrune" ).start();

		this.context = new FileSystemXmlApplicationContext( CheckerServletConfig.configFile.getPath() );
		
		try {

			 ((IServerBean) this.context.getBean( "serverBean" )).insertServer( new Server( 0, 0, 0, "127.0.0.1", 25565 ), true );

		} catch ( Exception e ) {
			e.printStackTrace();
		}

	}

	@Override
	protected void doTrace( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
	{
		resp.sendRedirect( "https://ryred.co/" );
	}

	@Override
	protected void doDelete( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
	{
		resp.sendRedirect( "https://ryred.co/" );
	}

	@Override
	protected void doOptions( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
	{
		resp.sendRedirect( "https://ryred.co/" );
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String[] requestArr = request.getRequestURI().substring( 1 ).replace( "/", "\u5584" ).split( "\u5584" );
		String command = "";

		if ( requestArr.length >= 1 ) { command = requestArr[ 0 ]; }

		if( !CheckerServletConfig.password.equals(request.getParameter("pw") ) ) {
			response.setStatus(401);
			response.getOutputStream().print( "{\"error\": \"Unauthorised!\"}" );
			return;
		}

		if(!request.getContentType().toLowerCase().contains( "json" )) {
			response.setStatus(415);
			response.getOutputStream().print( "{\"error\": \"Request not json content type!\"}" );
			return;
		}


		ArrayList<ID> ids = new ArrayList<>();
		try {

			ArrayList< String > receivedIds = full_gson.fromJson(new InputStreamReader(request.getInputStream()), new TypeToken<ArrayList<String>>() {}.getType());

			for( String string : receivedIds ) {

				try {
					ids.add(new ID(string, Integer.parseInt(string), false));
				} catch (NumberFormatException ex) {
					ids.add(new ID(string, -1, true));
				}

			}

		} catch ( Exception ex ) {
			response.setStatus(520);
			response.getOutputStream().print("{\"error\": \"Request not json content type or invalid format!\"}");
			return;
		}

		ArrayList<NameResponse> responses = new ArrayList<>();
		if( command.equalsIgnoreCase("user") ) {

			for( ID id : ids ) {
				if(!id.isError()) {

					try {

						NameResponse nr = ResponseCacheHandler.readCache( id.getId(), false );
						if( nr != null ) {
							responses.add( nr );
						} else {
							User user = SpigotSite.getAPI().getUserManager().getUserById( id.getId() );
							nr = new NameResponse( id, user.getUsername() );
							responses.add( nr );
							ResponseCacheHandler.putCache( nr, false );
						}

					} catch ( Exception ex ) {
						id.setError(true);
						responses.add( new NameResponse( id, null ) );
					}

				} else {
					responses.add( new NameResponse( id, null ) );
				}
			}

		} else if( command.equalsIgnoreCase( "resource" ) ) {

			for( ID id : ids ) {
				if(!id.isError()) {

					try {
						NameResponse nr = ResponseCacheHandler.readCache( id.getId(), true );
						if( nr != null ) {
							responses.add( nr );
						} else {
							Resource resource = SpigotSite.getAPI().getResourceManager().getResourceById( id.getId() );
							nr = new NameResponse( id, resource.getResourceName() );
							responses.add( nr );
							ResponseCacheHandler.putCache( nr, true );
						}
					} catch ( Exception ex ) {
						id.setError(true);
						responses.add( new NameResponse( id, null ) );
					}

				} else {
					responses.add( new NameResponse( id, null ) );
				}
			}

		}

		response.getOutputStream().print( full_gson.toJson( responses ) );

	}

	protected void doGet( HttpServletRequest request, HttpServletResponse response )
			throws ServletException, IOException
	{

		response.setContentType( "application/json" );

		String ipAddress = request.getHeader("X-Real-IP");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}

		if( knownIps.contains( ipAddress ) ) {
			response.getOutputStream().print( "{\"success\": \"Done.\"}" );
			return;
		}

		if( CooldownUtil.getCooldown().isCooldown( ipAddress ) ) {
			response.getOutputStream().print( "{\"error\": \"You're in cooldown.\"}" );
			return;
		} else {
			CooldownUtil.getCooldown().cooldown( ipAddress );
		}

		try {

			IServerBean sb = (IServerBean) this.context.getBean( "serverBean" );

			int port = Parsing.parseInt( request.getParameter( "PORT" ) );
			int uid = Parsing.parseInt( request.getParameter( "UID" ) );
			int rid = Parsing.parseInt( request.getParameter( "RID" ) );
			long nonce = Parsing.parseLong( request.getParameter( "NONCE" ) );

			if( uid == 0 || rid == 0 || nonce == 0 ) {

				//TODO better alerts.
				Logger.getRootLogger().log( Level.FATAL, "Bad request from IP: " + ipAddress );
				throw new Exception( "Badness. Bad request/bad program?" );

			}

			Server server = new Server( uid, rid, nonce, ipAddress, port );
			Server fetchedServer = sb.getServer( server.getUuid() );
			if( fetchedServer != null )
				server = fetchedServer;
			sb.insertOrUpdate( server );

		} catch ( Exception e ) {

			if( CheckerServletConfig.debug ) e.printStackTrace();

			response.getOutputStream().print( "{\"error\": \"Something went wrong.\"}" );
			return;

		}

		response.getOutputStream().print( "{\"success\": \"Done.\"}" );

	}

}
