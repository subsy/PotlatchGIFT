package com.newtonwilliamsdesign.potlatch.gift.domain;

import java.util.HashSet;
import java.util.Set;

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
@Entity(name = "GIFT")
public @Data class Gift {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private long parentid;
	
	@ManyToOne
	@JoinColumn(name="USER_ID")
	private GiftServiceUser createdby;
	
	private String title;
	private String description;
	private String url;
	private String imageurl;
	private String thumburl;
	private long flags;
	private long touches;
	private long createdon;
	private long modifiedon;
	
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
		this.thumburl = "";
		this.flags = 0;
		this.touches = 0;
		this.createdon = currTime;
		this.modifiedon = currTime;;
	}

	public Gift(long parentid, String title, String description, String url, String imageurl, String thumburl, long flags, long touches, long created, long modified, GiftServiceUser createdby) {
		super();
		this.parentid = parentid;
		this.title = title;
		this.description = description;
		this.url = url;
		this.imageurl = imageurl;
		this.thumburl = thumburl;
		this.flags = flags;
		this.touches = touches;
		this.createdon = created;
		this.modifiedon = modified;
		this.createdby = createdby;
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