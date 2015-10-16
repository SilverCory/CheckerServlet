package co.ryred.checkerservlet.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 24/03/2015.
 */
public class CooldownUtil
{

	private static CooldownUtil instance;

	private HashMap<String, Long> cooldownMap = new HashMap<String, Long>();

	public static CooldownUtil getCooldown()
	{
		return instance == null ? ( instance = new CooldownUtil() ) : instance;
	}

	public boolean isCooldown( String loc )
	{
		return cooldownMap.containsKey( loc ) && ( cooldownMap.get( loc ) - System.currentTimeMillis() ) > 0;
	}

	public void cooldown( String loc )
	{
		long cooldowntime = 5000;
		cooldownMap.put( loc, System.currentTimeMillis() + cooldowntime );
	}

	public void clear()
	{
		cooldownMap = new HashMap<String, Long>();
	}

	public synchronized void prune()
	{
		for ( Map.Entry<String, Long> entry : cooldownMap.entrySet() )
			if ( !isCooldown( entry.getKey() ) ) cooldownMap.remove( entry.getKey() );
	}

}
