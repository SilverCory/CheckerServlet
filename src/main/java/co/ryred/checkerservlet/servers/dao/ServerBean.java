package co.ryred.checkerservlet.servers.dao;

import co.ryred.checkerservlet.servers.Server;
import co.ryred.checkerservlet.servers.dao.impl.IServerBean;
import co.ryred.checkerservlet.servers.dao.impl.IServerManagment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 18/09/2015.
 */
@Transactional
@Component
public class ServerBean implements IServerBean {

	@Autowired
	private IServerManagment userManager;

	@Transactional
	public void insertServer( Server user ) {
		userManager.insertServer( user );
	}

	@Override
	public void insertServer( Server user, boolean force ) {
		userManager.insertServer( user, force );
	}

	@Transactional
	public Server getServer( String uuidString ) {
		return userManager.getServer( uuidString );
	}

	@Transactional
	public List<Server> getServers() {
		return userManager.getServers();
	}

	@Override
	public int getTotalServers() {
		return userManager.getTotalServers();
	}

	@Override
	public boolean exists( Server user ) {
		return userManager.exists( user );
	}

	@Override
	public void insertOrUpdate( Server server ) {
		userManager.insertOrUpdate( server );
	}

}
