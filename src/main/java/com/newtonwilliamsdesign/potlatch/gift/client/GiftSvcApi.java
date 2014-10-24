package com.newtonwilliamsdesign.potlatch.gift.client;

import java.util.Collection;

import com.newtonwilliamsdesign.potlatch.gift.repository.Gift;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * /**
 * This interface defines an API for a VideoSvc. The
 * interface is used to provide a contract for client/server
 * interactions. The interface is annotated with Retrofit
 * annotations so that clients can automatically convert the
 * interface into a client capable of sending the appropriate
 * HTTP requests.
 * 
 * The HTTP API that you must implement so that this interface
 * will work:
 * 
 * POST /oauth/token
 *    - The access point for the OAuth 2.0 Password Grant flow.
 *    - Clients should be able to submit a request with their username, password,
 *       client ID, and client secret, encoded as described in the OAuth lecture
 *       videos.
 *    - The client ID for the Retrofit adapter is "mobile" with an empty password.
 *    - There must be 2 users, whose usernames are "user0" and "admin". All passwords 
 *      should simply be "pass".
 *    - Rather than implementing this from scratch, we suggest reusing the example
 *      configuration from the OAuth 2.0 example in GitHub by copying these classes over:
 *      https://github.com/juleswhite/mobilecloud-14/tree/master/examples/9-VideoServiceWithOauth2/src/main/java/org/magnum/mobilecloud/video/auth
 *      You will need to @Import the OAuth2SecurityConfiguration into your Application or
 *      other configuration class to enable OAuth 2.0. You will also need to remove one
 *      of the containerCustomizer() methods in either OAuth2SecurityConfiguration or
 *      Application (they are the exact same code). You may need to customize the users
 *      in the OAuth2Config constructor or the security applied by the ResourceServer.configure(...) 
 *      method. You should determine what (if any) adaptations are needed by comparing this 
 *      and the test specification against the code in that class.
 *  
 * GET /gift
 *    - Returns the list of Gifts that have been added to the
 *      server as JSON. The list of Gifts should be persisted
 *      using Spring Data. The list of Gift objects should be able 
 *      to be unmarshalled by the client into a Collection<Gift>.
 *    - The return content-type should be application/json, which
 *      will be the default if you use @ResponseBody
 * 
 *      
 * POST /gift
 *    - The Gift metadata is provided as an application/json request
 *      body. The JSON should generate a valid instance of the 
 *      Gift class when deserialized by Spring's default 
 *      Jackson library.
 *    - Returns the JSON representation of the Gift object that
 *      was stored along with any updates to that object made by the server. 
 *    - **_The server should store the Gift in a Spring Data JPA repository.
 *    	 If done properly, the repository should handle generating ID's._** 
 *    - A Gift should not have any touches when it is initially created.
 *    - You will need to add one or more annotations to the Gift object
 *      in order for it to be persisted with JPA.
 *      
 * GET /gift/chain
 *    - Returns the list of Gifts that have no parent (i.e. are the first Gifts in a Gift chain) that have been added to the
 *      server as JSON. The list of Gifts should be persisted
 *      using Spring Data. The list of Gift objects should be able 
 *      to be unmarshalled by the client into a Collection<Gift>.
 *    - The return content-type should be application/json, which
 *      will be the default if you use @ResponseBody
 * 
 * GET /gift/{id}
 *    - Returns the Gift with the given id or 404 if the Gift is not found.
 *      
 * POST /gift/{id}/touch
 *    - Allows a user to like a Gift. Returns 200 Ok on success, 404 if the
 *      Gift is not found, or 400 if the user has already indicated that the Gift has touched them.
 *    - The service should should keep track of which users have been touched by a Gift and
 *      prevent a user from indicating they have been touched by a Gift twice. A POJO Gift object is provided for 
 *      you and you will need to annotate and/or add to it in order to make it persistable.
 *    - A user is only allowed to indicated that a Gift has touched them once. If a user tries to indicated that a Gift has touched them
 *       a second time, the operation should fail and return 400 Bad Request.
 *      
 * POST /gift/{id}/untouch
 *    - Allows a user to unmark a Gift that he/she previously indicated touched them. Returns 200 OK
 *       on success, 404 if the Gift is not found, and a 400 if the user has not 
 *       previously indicated they have been touched by the specified Gift.
 *       
 * GET /gift/{id}/touched
 *    - Returns a list of the string usernames of the users that have indicated that the specified Gift has touched them. 
 *    	If the Gift is not found, a 404 error should be generated.
 *    
 * POST /gift/{id}/flag
 *    - Allows a user to flag a Gift as obscene/inappropriate. Returns 200 Ok on success, 404 if the
 *      Gift is not found, or 400 if the user has already flagged the Gift.
 *    - The service should should keep track of which users have flagged a Gift and
 *      prevent a user from flagging a Gift twice. A POJO Gift object is provided for 
 *      you and you will need to annotate and/or add to it in order to make it persistable.
 *    - A user is only allowed to flag a Gift once. If a user tries to flag a Gift
 *       a second time, the operation should fail and return 400 Bad Request.
 *
  * POST /gift/{id}/unflag
 *    - Allows a user to unflag a Gift that they previously flagged. Returns 200 OK
 *       on success, 404 if the Gift is not found, and a 400 if the user has not 
 *       previously flagged the specified Gift.
 *
 * GET /gift/{id}/flaggedBy
 *    - Returns a list of the string usernames of the users that have flagged the specified
 *      Gift. If the Gift is not found, a 404 error should be generated.
 * 
 * GET /gift/search/findByTitle?title={title}
 *    - Returns a list of videos whose titles match the given parameter or an empty
 *      list if none are found.
 *     
 *
 */
public interface GiftSvcApi {

	public static final String TITLE_PARAMETER = "title";

	public static final String TOKEN_PATH = "/oauth/token";

	// The path where we expect the VideoSvc to live
	public static final String GIFT_SVC_PATH = "/gift";

	// The path to search videos by title
	public static final String GIFT_TITLE_SEARCH_PATH = GIFT_SVC_PATH + "/search/findByTitle";

	@GET(GIFT_SVC_PATH)
	public Collection<Gift> getGiftList();
	
	@GET(GIFT_SVC_PATH + "/chain")
	public Collection<Gift> getGiftChainList();
	
	@GET(GIFT_SVC_PATH + "/top")
	public Collection<Gift> getTopTenGiftGiversList();
	
	@GET(GIFT_SVC_PATH + "/{id}")
	public Gift getGiftById(@Path("id") long id);
	
	@POST(GIFT_SVC_PATH)
	public Gift addGift(@Body Gift v);
	
	@POST(GIFT_SVC_PATH + "/{id}/touch")
	public Void touchGift(@Path("id") long id);
	
	@POST(GIFT_SVC_PATH + "/{id}/untouch")
	public Void untouchGift(@Path("id") long id);
	
	@POST(GIFT_SVC_PATH + "/{id}/flag")
	public Void flagGift(@Path("id") long id);
	
	@POST(GIFT_SVC_PATH + "/{id}/unflag")
	public Void unflagGift(@Path("id") long id);
	
	@GET(GIFT_TITLE_SEARCH_PATH)
	public Collection<Gift> findByTitle(@Query(TITLE_PARAMETER) String title);
	
	@GET(GIFT_SVC_PATH + "/{id}/touched")
	public Collection<String> getUsersTouchedByGift(@Path("id") long id);
	
	@GET(GIFT_SVC_PATH + "/{id}/flaggedBy")
	public Collection<String> getUsersWhoFlaggedGift(@Path("id") long id);
}
