package co.ryred.checkerservlet.servers.dao;

import co.ryred.checkerservlet.servers.Server;
import co.ryred.checkerservlet.servers.dao.impl.IServerManagment;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 18/09/2015.
 */
@Service
public class ServerManagment implements IServerManagment
{

	@Autowired
	private SessionFactory sessionFactory;

	public void insertServer( Server user )
	{
		insertServer( user, true );
	}

	public void insertServer( Server user, boolean force )
	{
		user.updateTimeStamps();
		if ( !exists( user ) && !force ) { sessionFactory.getCurrentSession().save( user ); }
	}

	public Server getServer( long nonce )
	{
		return sessionFactory.getCurrentSession().get( Server.class, nonce );
	}

	@SuppressWarnings("unchecked")
	public List<Server> getServers()
	{
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria( Server.class );
		return criteria.list();
	}

	@Override
	public int getTotalServers()
	{
		Number num = (Number) sessionFactory.getCurrentSession().createCriteria( Server.class ).setProjection( Projections.rowCount() ).uniqueResult();
		return num.intValue();
	}

	@Override
	public boolean exists( Server server )
	{

		return sessionFactory.getCurrentSession().get( Server.class, server.getNonce() ) != null;

	}

	@Override
	public void insertOrUpdate( Server server )
	{
		server.updateTimeStamps();
		sessionFactory.getCurrentSession().saveOrUpdate( server );
	}

	/*@PostConstruct
	public void registerListeners() {

		// this is pointless.
		final EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);

		//registry.getEventListenerGroup( EventType.SAVE ).appendListener( saveListener );
		//registry.getEventListenerGroup( EventType.SAVE_UPDATE ).appendListener( saveListener );
		//registry.getEventListenerGroup( EventType.UPDATE ).appendListener( saveListener );
		registry.getEventListenerGroup( EventType.PRE_LOAD ).appendListener( event -> {
			if (event.getEntity() instanceof Server) {
				Server server = (Server) event.getEntity();
				server.updateTimeStamps();
			}
		} );

		registry.getEventListenerGroup( EventType.PERSIST ).appendListener( new PersistEventListener() {

			@Override
			public void onPersist( PersistEvent event ) throws HibernateException
			{
				if (event.getObject() instanceof Server) {
					Server server = (Server) event.getObject();
					server.updateTimeStamps();
				}
			}

			@Override
			public void onPersist( PersistEvent event, Map map ) throws HibernateException
			{
				if (event.getObject() instanceof Server) {
					Server server = (Server) event.getObject();
					server.updateTimeStamps();
				}
			}

		});

	}*/

}
