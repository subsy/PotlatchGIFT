package com.newtonwilliamsdesign.potlatch.testdata;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newtonwilliamsdesign.potlatch.gift.repository.Gift;

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
	
	/**
	 * Construct and return a Gift object with a
	 * random title and url
	 * 
	 * @return
	 */
	public static Gift randomGift() {
		// Information about the video
		// Construct a random identifier using Java's UUID class
		String id = UUID.randomUUID().toString();
		long parentid = 0;
		String title = "Gift-"+id;
		String description = "This is a dummy description";
		String url = "http://coursera.org/some/gift-"+id;
		String imageurl = "http://coursera.org/some/gift-" + id + "/image";
		String createdBy = "ben";
		long currTime = System.currentTimeMillis();
		return new Gift(parentid, title, description, url, imageurl, 0, 0, currTime, currTime, createdBy);
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
