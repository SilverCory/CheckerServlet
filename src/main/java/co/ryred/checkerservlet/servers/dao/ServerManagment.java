package co.ryred.checkerservlet.servers.dao;

import co.ryred.checkerservlet.servers.Server;
import co.ryred.checkerservlet.servers.dao.impl.IServerManagment;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
		sessionFactory.getCurrentSession().saveOrUpdate( server );
	}

}
