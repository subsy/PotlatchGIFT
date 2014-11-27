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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.common.base.Objects;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

/**
 * A simple object to represent a GIFT and its URL for viewing.
 */
@Entity
public @Data class Gift {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private long parentid;

	private String title;
	private String description;
	private String imageurl;
	private String thumburl;
	private long flags;
	private long touches;
	private long createdon;
	private long modifiedon;
	
	@ManyToOne
	@JoinColumn
	@JsonBackReference
	private GiftServiceUser createdby;
	
	@ElementCollection
	private Set<String> touchesUsernames = new HashSet<String>(); 
	
	@ElementCollection 
	private Set<String> flagsUsernames = new HashSet<String>(); 
	
	public Gift() {
		long currTime = System.currentTimeMillis();
		this.parentid = 0;
		this.title = "Title";
		this.description = "Description";
		this.imageurl = "";
		this.thumburl = "";
		this.flags = 0;
		this.touches = 0;
		this.createdon = currTime;
		this.modifiedon = currTime;;
	}

	public Gift(long parentid, String title, String description, String imageurl, String thumburl, long flags, long touches, long created, long modified) {
		super();
		this.parentid = parentid;
		this.title = title;
		this.description = description;
		this.imageurl = imageurl;
		this.thumburl = thumburl;
		this.flags = flags;
		this.touches = touches;
		this.createdon = created;
		this.modifiedon = modified;
	}
	
	/**
	 * Two Gifts will generate the same hashcode if they have exactly the same
	 * values for their title and url.
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(title, imageurl);
	}

	/**
	 * Two Gifts are considered equal if they have exactly the same values for
	 * their title and url.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Gift) {
			Gift other = (Gift) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(title, other.title)
					&& Objects.equal(imageurl, other.imageurl);
		} else {
			return false;
		}
	}

}