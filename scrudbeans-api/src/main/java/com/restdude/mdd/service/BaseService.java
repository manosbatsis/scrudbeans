/**
 *
 * Restdude
 * -------------------------------------------------------------------
 *
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.restdude.mdd.service;

import com.restdude.websocket.message.IActivityNotificationMessage;
import com.restdude.websocket.message.IMessageResource;

import org.springframework.core.convert.ConversionService;

/**
 * Base Service interface, provides access to auth, STOMP etc
 */
public interface BaseService {

	/**
	 * Get the configured type conversion service
	 */
	ConversionService getConversionService();

	/**
	 * Get the configured file persistence service
	 */
	FilePersistenceService getFilePersistenceService();

	// TODO
	/**
	 * Get the current user's details
	 * @return UserDetails getPrincipal();
	 */

	/**
	 * Send an activity message via websockets/STOMP
	 * @param msg the message
	 * @param username the message recipient
	 */
	void sendStompActivityMessage(IActivityNotificationMessage msg, String username);

	/**
	 * Send an activity message via websockets/STOMP
	 * @param msg the message
	 * @param usernames the message recipients
	 */
	void sendStompActivityMessage(IActivityNotificationMessage msg, Iterable<String> usernames);

	/**
	 * Send an state-changed message via websockets/STOMP
	 * @param msg the message
	 * @param username the message recipient
	 */
	void sendStompStateChangeMessage(IMessageResource msg, String username);

	/**
	 * Send an state-changed message via websockets/STOMP
	 * @param msg the message
	 * @param usernames the message recipient
	 */
	void sendStompStateChangeMessage(IMessageResource msg, Iterable<String> usernames);
}
