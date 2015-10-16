package co.ryred.checkerservlet.servers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 15/09/2015.
 */
@Entity
@Table(name = "Servers")
@AllArgsConstructor
@RequiredArgsConstructor
public class Server
{

	@Column(unique = false, name = "userID", nullable = false, updatable = false)
	@Getter
	private final int userID;

	@Column(unique = false, name = "resourceID", nullable = false, updatable = false)
	@Getter
	private final int resourceID;

	@Column(unique = true, name = "nonce", nullable = false, updatable = false)
	@Getter
	@Id
	private final long nonce;

	@Column(unique = false, name = "serverAddress", nullable = false, updatable = false, columnDefinition = "VARCHAR(36) CHARACTER SET utf8 COLLATE utf8_bin")
	@Getter
	private final String serverAddress;

	@Column(unique = false, name = "port", nullable = false, updatable = false)
	@Getter
	private final int port;

	@Column(unique = false, name = "lastUpdated", nullable = false, updatable = true)
	@Getter
	private Date lastUpdated;

	@Column(unique = false, name = "firstNoticed", nullable = false, updatable = false)
	@Getter
	private Date firstNoticed;

	@PreUpdate
	@PrePersist
	public void updateTimeStamps() {
		lastUpdated = new Date();
		if ( firstNoticed == null ) {
			 firstNoticed = new Date();
		}
	}

}
