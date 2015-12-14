package co.ryred.checkerservlet;

import co.ryred.checkerservlet.servers.Server;
import co.ryred.checkerservlet.servers.dao.impl.IServerBean;
import co.ryred.checkerservlet.util.CooldownUtil;
import co.ryred.checkerservlet.util.Parsing;
import com.google.gson.Gson;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 15/09/2015.
 */
@SuppressWarnings("unchecked")
public class CheckerServlet extends HttpServlet
{

	private static final Gson full_gson = new Gson();
	private final BeanFactory context;

	public CheckerServlet() throws Exception
	{

		CheckerServletConfig.init();

		/*Configuration cfg = new Configuration();
		cfg.addAnnotatedClass( User.class );
		cfg.configure( PlayerServletConfig.configFile );
		this.sessionFactory = cfg.buildSessionFactory();*/
		new Thread( new Runnable() {
			@Override
			public void run()
			{

				while ( true ) {

					try {
						Thread.sleep( TimeUnit.MINUTES.toMillis( 5 ) );
					} catch ( Exception e ) {}

					try {
						CooldownUtil.getCooldown().prune();
					} catch ( Exception e ) {}

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

	protected void doGet( HttpServletRequest request, HttpServletResponse response )
			throws ServletException, IOException
	{

		response.setContentType( "application/json" );

		String ipAddress = request.getHeader("X-Real-IP");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
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
				Logger.getRootLogger().log( Level.FATAL, "Bad request from IP: " + request.getRemoteAddr() );
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
