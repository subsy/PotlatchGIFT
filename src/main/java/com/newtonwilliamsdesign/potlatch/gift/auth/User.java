/* 
 **
 ** Copyright 2014, Jules White
 **
 ** 
 */
package com.newtonwilliamsdesign.potlatch.gift.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

import com.newtonwilliamsdesign.potlatch.gift.repository.Gift;

@Entity(name = "USER")
public @Data class User implements UserDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(unique=true)
	private String username;
	private String password;
	
	@ElementCollection
	private Collection<GrantedAuthority> authorities;
	
	private int touchedcount;
	
	@OneToMany(mappedBy = "createdby", cascade=CascadeType.ALL)
	private Set<Gift> createdGifts = new HashSet<Gift>();

	@SuppressWarnings("unchecked")
	public User(String username, String password) {
		this(username, password, Collections.EMPTY_LIST);
	}

	public User(String username, String password,
			String...authorities) {
		this.username = username;
		this.password = password;
		this.authorities = AuthorityUtils.createAuthorityList(authorities);
	}

	public User(String username, String password,
			Collection<GrantedAuthority> authorities) {
		super();
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
