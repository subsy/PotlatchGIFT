package com.newtonwilliamsdesign.potlatch.gift.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Objects;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;
import javax.persistence.JoinColumn;

import lombok.Data;

/**
 * A simple object to represent a GIFT and its URL for viewing.
 */
@Entity(name = "GIFTS")
public @Data class Gift {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "GIFT_ID")
	private long giftid;
	
	private long parentid;
	private String createdBy;
	private String title;
	private String description;
	private String url;
	private String imageurl;
	private long flags;
	private long touches;
	private long createdOn;
	private long modifiedOn;
	
	@ElementCollection
	private Set<String> touchesUsernames = new HashSet<String>(); 
	
	@ElementCollection 
	private Set<String> flagsUsernames = new HashSet<String>(); 
	
	public Gift() {
		long currTime = System.currentTimeMillis();
		this.parentid = 0;
		this.title = "Title";
		this.description = "Description";
		this.url = "";
		this.imageurl = "";
		this.flags = 0;
		this.touches = 0;
		this.createdOn = currTime;
		this.modifiedOn = currTime;
		this.createdBy = "";
	}

	public Gift(long parentid, String title, String description, String url, String imageurl, long flags, long touches, long created, long modified, String createdBy) {
		super();
		this.parentid = parentid;
		this.title = title;
		this.description = description;
		this.url = url;
		this.imageurl = imageurl;
		this.flags = flags;
		this.touches = touches;
		this.createdOn = created;
		this.modifiedOn = modified;
		this.createdBy = createdBy;
	}
	
	/**
	 * Two Gifts will generate the same hashcode if they have exactly the same
	 * values for their title and url.
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(title, url);
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
					&& Objects.equal(url, other.url);
		} else {
			return false;
		}
	}

}