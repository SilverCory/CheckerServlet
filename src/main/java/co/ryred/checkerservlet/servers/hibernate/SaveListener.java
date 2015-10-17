package co.ryred.checkerservlet.servers.hibernate;

import co.ryred.checkerservlet.servers.Server;
import org.hibernate.event.internal.DefaultSaveEventListener;
import org.hibernate.event.internal.DefaultSaveOrUpdateEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 17/10/2015.
 */
public class SaveListener extends DefaultSaveEventListener
{

	@Override
	public void onSaveOrUpdate( SaveOrUpdateEvent event )
	{

		if (event.getObject() instanceof Server) {
			Server server = (Server) event.getObject();
			server.updateTimeStamps();
		}

		super.onSaveOrUpdate( event );

	}
}
