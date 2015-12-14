package co.ryred.checkerservlet.servers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.io.Charsets;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 15/09/2015.
 */
@Entity
@Table(name = "Servers")
@AllArgsConstructor
@NoArgsConstructor
public class Server
{

	public Server( int userID, int resourceID, long nonce, String serverAddress, int port )
	{
		this.uuid = UUID.nameUUIDFromBytes( ("SexyCory" + userID + "_" + resourceID + "_" + nonce + "_" + serverAddress + "_" + port).getBytes( Charsets.UTF_8 ) ).toString();
		this.userID = userID;
		this.resourceID = resourceID;
		this.nonce = nonce;
		this.serverAddress = serverAddress;
		this.port = port;
	}

	@Column(unique = true, name = "uuid", nullable = false, updatable = false, columnDefinition = "VARCHAR(36) CHARACTER SET utf8 COLLATE utf8_bin")
	@Getter
	@Id
	private String uuid;

	@Column(unique = false, name = "userID", nullable = false, updatable = false)
	@Getter
	private int userID;

	@Column(unique = false, name = "resourceID", nullable = false, updatable = false)
	@Getter
	private int resourceID;

	@Column(unique = false, name = "nonce", nullable = false, updatable = false)
	@Getter
	private long nonce;

	@Column(unique = false, name = "serverAddress", nullable = false, updatable = false, columnDefinition = "VARCHAR(64) CHARACTER SET utf8 COLLATE utf8_bin")
	@Getter
	private String serverAddress;

	@Column(unique = false, name = "port", nullable = false, updatable = false)
	@Getter
	private int port;

	@Column(unique = false, name = "lastUpdated", nullable = false, updatable = true)
	@Getter
	private Date lastUpdated;

	@Column(unique = false, name = "firstNoticed", nullable = false, updatable = false)
	@Getter
	private Date firstNoticed;

	@Column(unique = false, name = "totalCons", nullable = false, updatable = true)
	@Getter
	private int totalConnections = 0;

	@PreUpdate
	@PrePersist
	public void updateTimeStamps() {

		lastUpdated = new Date();
		if ( firstNoticed == null ) {
			firstNoticed = new Date();
			totalConnections = 1;
		} else {
			totalConnections++;
		}

	}

}
