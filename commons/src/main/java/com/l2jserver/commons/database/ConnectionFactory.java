/*
 * Copyright © 2004-2026 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.commons.database;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Connection Factory implementation.
 * @author Zoey76
 * @version 2.6.5.0
 */
public class ConnectionFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(ConnectionFactory.class);
	
	private static final int MAX_LIFETIME = 10;
	
	private final HikariDataSource dataSource;
	
	private ConnectionFactory(Builder builder) {
		dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(builder.url);
		dataSource.setUsername(builder.user);
		dataSource.setPassword(builder.password);
		dataSource.setMaximumPoolSize(builder.maxPoolSize);
		dataSource.setIdleTimeout(SECONDS.toMillis(builder.maxIdleTime));
		dataSource.setMaxLifetime(MINUTES.toMillis(MAX_LIFETIME));
	}
	
	public static ConnectionFactory getInstance() {
		return Builder.INSTANCE;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	
	public Connection getConnection() {
		Connection con = null;
		while (con == null) {
			try {
				con = getDataSource().getConnection();
			} catch (SQLException ex) {
				LOG.warn("Unable to get a connection!", ex);
			}
		}
		return con;
	}
	
	public void close() {
		try {
			dataSource.close();
		} catch (Exception e) {
			LOG.warn("There has been a problem closing the data source!", e);
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		protected static volatile ConnectionFactory INSTANCE;
		
		private String url;
		private String user;
		private String password;
		private int maxPoolSize;
		private int maxIdleTime;
		
		private Builder() {
		}
		
		public Builder withUrl(String url) {
			this.url = url;
			return this;
		}
		
		public Builder withUser(String user) {
			this.user = user;
			return this;
		}
		
		public Builder withPassword(String password) {
			this.password = password;
			return this;
		}
		
		public Builder withMaxPoolSize(int maxPoolSize) {
			this.maxPoolSize = maxPoolSize;
			return this;
		}
		
		public Builder withMaxIdleTime(int maxIdleTime) {
			this.maxIdleTime = maxIdleTime;
			return this;
		}
		
		public void build() {
			if (INSTANCE == null) {
				synchronized (this) {
					if (INSTANCE == null) {
						INSTANCE = new ConnectionFactory(this);
					} else {
						LOG.warn("Trying to build another Connection Factory!");
					}
				}
			}
		}
	}
}
