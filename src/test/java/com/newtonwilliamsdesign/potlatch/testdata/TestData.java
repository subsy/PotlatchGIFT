package com.newtonwilliamsdesign.potlatch.testdata;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newtonwilliamsdesign.potlatch.gift.domain.Gift;
import com.newtonwilliamsdesign.potlatch.gift.domain.GiftServiceUser;

/**
 * This is a utility class to aid in the construction of
 * Video objects with random names, urls, and durations.
 * The class also provides a facility to convert objects
 * into JSON using Jackson, which is the format that the
 * VideoSvc controller is going to expect data in for
 * integration testing.
 * 
 * @author jules
 *
 */
public class TestData {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	public static GiftServiceUser setupUser(String username) {
	
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
	
		GiftServiceUser createdBy = new GiftServiceUser(username, "password", authorities);
		
		return createdBy;
	}
	
	/**
	 * Construct and return a Gift object with a
	 * random title and url
	 * 
	 * @return
	 */
	public static Gift randomGift(GiftServiceUser user) {
		// Information about the video
		// Construct a random identifier using Java's UUID class
		String id = UUID.randomUUID().toString();
		long parentid = 0;
		String title = "Gift-"+id;
		String description = "This is a dummy description";
		String url = "https://sle.pt/gift/"+id;
		String imageurl = "https://sle.pt/gift/" + id + "/image";
		long currTime = System.currentTimeMillis();
		
		return new Gift(parentid, title, description, url, imageurl, 0, 0, currTime, currTime, user);
	}
	
	/**
	 *  Convert an object to JSON using Jackson's ObjectMapper
	 *  
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public static String toJson(Object o) throws Exception{
		return objectMapper.writeValueAsString(o);
	}
}
