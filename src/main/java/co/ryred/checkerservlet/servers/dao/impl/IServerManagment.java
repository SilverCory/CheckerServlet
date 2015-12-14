package co.ryred.checkerservlet.servers.dao.impl;

import co.ryred.checkerservlet.servers.Server;

import java.util.List;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 18/09/2015.
 */
public interface IServerManagment
{

	void insertServer( Server user );

	void insertServer( Server user, boolean force );

	Server getServer( String uuidString );

	List<Server> getServers();

	int getTotalServers();

	boolean exists( Server user );

	void insertOrUpdate( Server server );

}
