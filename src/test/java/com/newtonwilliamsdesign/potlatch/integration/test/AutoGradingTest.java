package com.newtonwilliamsdesign.potlatch.integration.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import io.magnum.autograder.junit.Rubric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.junit.Test;

import com.newtonwilliamsdesign.potlatch.gift.client.SecuredRestBuilder;
import com.newtonwilliamsdesign.potlatch.gift.client.GiftSvcApi;
import com.newtonwilliamsdesign.potlatch.gift.repository.Gift;
import com.newtonwilliamsdesign.potlatch.testdata.TestData;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;

/**
 * A test for the Asgn2 video service
 * 
 * @author mitchell
 */
public class AutoGradingTest {

	private class ErrorRecorder implements ErrorHandler {

		private RetrofitError error;

		@Override
		public Throwable handleError(RetrofitError cause) {
			error = cause;
			return error.getCause();
		}

		public RetrofitError getError() {
			return error;
		}
	}

	private final String TEST_URL = "https://localhost:8443";

	private final String USERNAME1 = "ben";
	private final String USERNAME2 = "user0";
	private final String PASSWORD = "password";
	private final String CLIENT_ID = "mobile";

	private GiftSvcApi readWriteGiftSvcUser1 = new SecuredRestBuilder()
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
			.setEndpoint(TEST_URL)
			.setLoginEndpoint(TEST_URL + GiftSvcApi.TOKEN_PATH)
			// .setLogLevel(LogLevel.FULL)
			.setUsername(USERNAME1).setPassword(PASSWORD).setClientId(CLIENT_ID)
			.build().create(GiftSvcApi.class);

	private GiftSvcApi readWriteGiftSvcUser2 = new SecuredRestBuilder()
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
			.setEndpoint(TEST_URL)
			.setLoginEndpoint(TEST_URL + GiftSvcApi.TOKEN_PATH)
			// .setLogLevel(LogLevel.FULL)
			.setUsername(USERNAME2).setPassword(PASSWORD).setClientId(CLIENT_ID)
			.build().create(GiftSvcApi.class);

	private Gift gift = TestData.randomGift();


	@Rubric(value = "Video data is preserved", 
			goal = "The goal of this evaluation is to ensure that your Spring controller(s) "
			+ "properly unmarshall Video objects from the data that is sent to them "
			+ "and that the HTTP API for adding videos is implemented properly. The"
			+ " test checks that your code properly accepts a request body with"
			+ " application/json data and preserves all the properties that are set"
			+ " by the client. The test also checks that you generate an ID and data"
			+ " URL for the uploaded video.", 
			points = 20.0, 
			reference = "This test is derived from the material in these videos: "
			+ "https://class.coursera.org/mobilecloud-001/lecture/61 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/97 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/99 ")
	@Test
	public void testAddGiftMetadata() throws Exception {
		Gift received = readWriteGiftSvcUser1.addGift(gift);
		assertEquals(gift.getTitle(), received.getTitle());
		assertTrue(received.getTouches() == 0);
		assertTrue(received.getId() > 0);
	}

	@Rubric(value = "The list of videos is updated after an add", 
			goal = "The goal of this evaluation is to ensure that your Spring controller(s) "
			+ "can add videos to the list that is stored in memory on the server."
			+ " The test also ensure that you properly return a list of videos"
			+ " as JSON.", 
			points = 20.0, 
			reference = "This test is derived from the material in these videos: "
			+ "https://class.coursera.org/mobilecloud-001/lecture/61 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/97 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/99 ")
	@Test
	public void testAddGetGift() throws Exception {
		readWriteGiftSvcUser1.addGift(gift);
		Collection<Gift> stored = readWriteGiftSvcUser1.getGiftList();
		assertTrue(stored.contains(gift));
	}

	@Rubric(value = "Requests without authentication token are denied.", 
			goal = "The goal of this evaluation is to ensure that your Spring application "
			+ "properly authenticates queries using the OAuth Password Grant flow."
			+ "Any query that does not contain the correct authorization token"
			+ "should be denied with a 401 error.", 
			points = 20.0, 
			reference = "This test is derived from the material in these videos: "
			+ "https://class.coursera.org/mobilecloud-001/lecture/117 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/127 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/123 ")
	@Test
	public void testDenyGiftAddWithoutOAuth() throws Exception {
		ErrorRecorder error = new ErrorRecorder();

		// Create an insecure version of our Rest Adapter that doesn't know how
		// to use OAuth.
		GiftSvcApi insecureGiftService = new RestAdapter.Builder()
				.setClient(
						new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
				.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL)
				.setErrorHandler(error).build().create(GiftSvcApi.class);
		try {
			// This should fail because we haven't logged in!
			insecureGiftService.addGift(gift);

			fail("Yikes, the security setup is horribly broken and didn't require the user to authenticate!!");

		} catch (Exception e) {
			// Ok, our security may have worked, ensure that
			// we got a 401
			assertEquals(HttpStatus.SC_UNAUTHORIZED, error.getError()
					.getResponse().getStatus());
		}

		// We should NOT get back the video that we added above!
		Collection<Gift> gifts = readWriteGiftSvcUser1.getGiftList();
		assertFalse(gifts.contains(gift));
	}

	@Rubric(value = "A user can like/unlike a video and increment/decrement the like count", 
			goal = "The goal of this evaluation is to ensure that your Spring application "
			+ "allows users to like/unlike videos using the /video/{id}/like endpoint, and"
			+ "and the /video/{id}/unlike endpoint."
			+ "Once a user likes/unlikes a video, the count of users that like that video"
			+ "should be incremented/decremented.", 
			points = 20.0, 
			reference = "This test is derived from the material in these videos: "
			+ "https://class.coursera.org/mobilecloud-001/lecture/99 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/121 ")
	@Test
	public void testTouchedCount() throws Exception {

		// Add the video
		Gift v = readWriteGiftSvcUser1.addGift(gift);

		// Like the video
		readWriteGiftSvcUser1.touchGift(v.getId());

		// Get the video again
		v = readWriteGiftSvcUser1.getGiftById(v.getId());

		// Make sure the like count is 1
		assertTrue(v.getTouches() == 1);

		// Unlike the video
		readWriteGiftSvcUser1.untouchGift(v.getId());

		// Get the video again
		v = readWriteGiftSvcUser1.getGiftById(v.getId());

		// Make sure the like count is 0
		assertTrue(v.getTouches() == 0);
	}

	@Rubric(value = "A user can like/unlike a video and be added to/removed from the \"liked by\" list.", 
			goal = "The goal of this evaluation is to ensure that your Spring application "
			+ "allows users to like/unlike videos using the /video/{id}/like endpoint"
			+ "and the /video/{id}/unlike endpoint."
			+ "Once a user likes/unlikes a video, the username should be added to/removed from the "
			+ "list of users that like that video.", 
			points = 20.0, 
			reference = "This test is derived from the material in these videos: "
			+ "https://class.coursera.org/mobilecloud-001/lecture/99 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/121 ")
	@Test
	public void testTouched() throws Exception {

		// Add the video
		Gift v = readWriteGiftSvcUser1.addGift(gift);

		// Like the video
		readWriteGiftSvcUser1.touchGift(v.getId());

		Collection<String> touched = readWriteGiftSvcUser1.getUsersTouchedByGift(v.getId());

		// Make sure we're on the list of people that like this video
		assertTrue(touched.contains(USERNAME1));
		
		// Have the second user like the video
		readWriteGiftSvcUser2.touchGift(v.getId());
		
		// Make sure both users show up in the like list
		touched = readWriteGiftSvcUser1.getUsersTouchedByGift(v.getId());
		assertTrue(touched.contains(USERNAME1));
		assertTrue(touched.contains(USERNAME2));

		// Unlike the video
		readWriteGiftSvcUser1.untouchGift(v.getId());

		// Get the video again
		touched = readWriteGiftSvcUser1.getUsersTouchedByGift(v.getId());

		// Make sure user1 is not on the list of people that liked this video
		assertTrue(!touched.contains(USERNAME1));
		
		// Make sure that user 2 is still there
		assertTrue(touched.contains(USERNAME2));
	}

	@Rubric(value = "A user is only allowed to like a video once.", 
			goal = "The goal of this evaluation is to ensure that your Spring application "
			+ "restricts users to liking a video only once. "
			+ "This test simply attempts to like a video twice and then checks that "
			+ "the like count is only 1.", 
			points = 20.0, 
			reference = "This test is derived from the material in these videos: "
					+ "https://class.coursera.org/mobilecloud-001/lecture/99 "
					+ "https://class.coursera.org/mobilecloud-001/lecture/121"
	)
	@Test
	public void testTouchingTwice() throws Exception {

		// Add the video
		Gift v = readWriteGiftSvcUser1.addGift(gift);

		// Like the video
		readWriteGiftSvcUser1.touchGift(v.getId());

		// Get the video again
		v = readWriteGiftSvcUser1.getGiftById(v.getId());

		// Make sure the like count is 1
		assertTrue(v.getTouches() == 1);

		try {
			// Like the video again.
			readWriteGiftSvcUser1.touchGift(v.getId());

			fail("The server let us like a video twice without returning a 400");
		} catch (RetrofitError e) {
			// Make sure we got a 400 Bad Request
			assertEquals(400, e.getResponse().getStatus());
		}

		// Get the video again
		v = readWriteGiftSvcUser1.getGiftById(v.getId());

		// Make sure the like count is still 1
		assertTrue(v.getTouches() == 1);
	}

	@Rubric(value = "A user cannot like a non-existant video", 
			goal = "The goal of this evaluation is to ensure that your Spring application "
			+ "won't crash if a user attempts to like a non-existant video. "
			+ "This test simply attempts to like a non-existant video then checks "
			+ "that a 404 Not Found response is returned.", 
			points = 20.0, 
			reference = "This test is derived from the material in these videos: "
					+ "https://class.coursera.org/mobilecloud-001/lecture/99 "
					+ "https://class.coursera.org/mobilecloud-001/lecture/121"
	)
	@Test
	public void testTouchingNonExistantGift() throws Exception {

		try {
			// Like the video again.
			readWriteGiftSvcUser1.touchGift(getInvalidGiftId());

			fail("The server let us like a video that doesn't exist without returning a 404.");
		} catch (RetrofitError e) {
			// Make sure we got a 400 Bad Request
			assertEquals(404, e.getResponse().getStatus());
		}
	}

	@Rubric(value = "A user can find a video by providing its name", 
			goal = "The goal of this evaluation is to ensure that your Spring application "
			+ "allows users to find videos by searching for the video's name.", 
			points = 20.0, 
			reference = "This test is derived from the material in these videos: "
			+ "https://class.coursera.org/mobilecloud-001/lecture/97 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/99 ")
	@Test
	public void testFindByTitle() {

		// Create the names unique for testing.
		String[] names = new String[3];
		names[0] = "The Cat";
		names[1] = "The Spoon";
		names[2] = "The Plate";

		// Create three random videos, but use the unique names
		ArrayList<Gift> gifts = new ArrayList<Gift>();

		for (int i = 0; i < names.length; ++i) {
			gifts.add(TestData.randomGift());
			gifts.get(i).setTitle(names[i]);
		}

		// Add all the videos to the server
		for (Gift v : gifts){
			readWriteGiftSvcUser1.addGift(v);
		}

		// Search for "The Cat"
		Collection<Gift> searchResults = readWriteGiftSvcUser1.findByTitle(names[0]);
		assertTrue(searchResults.size() > 0);

		// Make sure all the returned videos have "The Cat" for their title
		for (Gift v : searchResults) {
			assertTrue(v.getTitle().equals(names[0]));
		}
	}

	private long getInvalidGiftId() {
		Set<Long> ids = new HashSet<Long>();
		Collection<Gift> stored = readWriteGiftSvcUser1.getGiftList();
		for (Gift v : stored) {
			ids.add(v.getId());
		}

		long nonExistantId = Long.MIN_VALUE;
		while (ids.contains(nonExistantId)) {
			nonExistantId++;
		}
		return nonExistantId;
	}

}
