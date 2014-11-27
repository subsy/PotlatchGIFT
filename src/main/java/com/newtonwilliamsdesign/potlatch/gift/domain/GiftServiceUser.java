package com.newtonwilliamsdesign.potlatch.gift.domain;

/***********************************************************************************
 ***********************************************************************************
 ***********************************************************************************
        G I F T
        A Multi-user Web Application and Android Client Application
        for sharing of image gifts.

        Copyright (C) 2014 Newton Williams Design.

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU Affero General Public License as
        published by the Free Software Foundation, either version 3 of the
        License, or (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU Affero General Public License for more details.

        You should have received a copy of the GNU Affero General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***********************************************************************************
 ***********************************************************************************
 ***********************************************************************************/

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Entity
public @Data class GiftServiceUser {

	@Id
	private String username;
	
	private String password;
	private String name;
	private int touchedcount;
	
	@OneToOne
	@JoinColumn
	@JsonManagedReference
	private GiftUserPreferences userprefs;
	
	@OneToMany(mappedBy = "createdby", cascade=CascadeType.ALL)
	@JsonManagedReference
	private Set<Gift> createdGifts = new HashSet<Gift>();

	public GiftServiceUser() {
		this.username = "";
		this.password = "";
		this.userprefs = new GiftUserPreferences();
	}
	
	public GiftServiceUser(String username, String password) {
		this.username = username;
		this.password = password;
		this.userprefs = new GiftUserPreferences();
	}

}
