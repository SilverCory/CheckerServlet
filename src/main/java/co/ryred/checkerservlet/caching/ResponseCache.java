package co.ryred.checkerservlet.caching;

import co.ryred.checkerservlet.CheckerServletConfig;
import co.ryred.checkerservlet.spigot.NameResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Cory Redmond on 18/12/15.
 *
 * @author Cory Redmond <ace@ac3-servers.eu>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCache {

	private NameResponse response;
	private long expireTime;

	public boolean isExpired() {
		return ( expireTime + CheckerServletConfig.cacheTime ) - System.currentTimeMillis() < 0;
	}

}
