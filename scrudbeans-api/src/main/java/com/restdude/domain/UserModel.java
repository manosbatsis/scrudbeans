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
package com.restdude.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.security.core.GrantedAuthority;

/**
 * Created by manos on 4/2/2017.
 */
public interface UserModel extends Model<String> {

	Locale getLocaleObject();

	boolean hasRole(String roleName);

	Boolean getActive();

	void setActive(Boolean active);

	// serialize user name to response
	@JsonProperty
	String getUsername();

	// but ignore when de-serializing from request
	@JsonIgnore
	void setUsername(String userName);

	@JsonGetter("fullName")
	String getFullName();

	String getName();

	void setName(String name);

	String getFirstName();

	void setFirstName(String firstName);

	String getLastName();

	void setLastName(String lastName);

	String getDescription();

	void setDescription(String description);

	String getEmailHash();

	void setEmailHash(String emailHash);

	String getAvatarUrl();

	void setAvatarUrl(String avatarUrl);

	String getBannerUrl();

	void setBannerUrl(String bannerUrl);

	LocalDateTime getLastVisit();

	void setLastVisit(LocalDateTime lastVisit);

	String getLocale();

	void setLocale(String locale);

	List<? extends GrantedAuthority> getRoles();
/*
    UserCredentials getCredentials();

    void setCredentials(UserCredentials credentials);

    void setRoles(List<Role> roles);

    List<Friendship> getFriendships();

    void setFriendships(List<Friendship> friendships);

    ContactDetails getContactDetails();

    void setContactDetails(ContactDetails contactDetails);
    */
}
