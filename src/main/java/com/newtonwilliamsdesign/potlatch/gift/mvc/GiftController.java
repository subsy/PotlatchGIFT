/*
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.newtonwilliamsdesign.potlatch.gift.mvc;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.newtonwilliamsdesign.potlatch.gift.GiftFileManager;
import com.newtonwilliamsdesign.potlatch.gift.client.GiftSvcApi;
import com.newtonwilliamsdesign.potlatch.gift.domain.Gift;
import com.newtonwilliamsdesign.potlatch.gift.domain.GiftServiceUser;
import com.newtonwilliamsdesign.potlatch.gift.domain.GiftStatus;
import com.newtonwilliamsdesign.potlatch.gift.domain.GiftStatus.GiftState;
import com.newtonwilliamsdesign.potlatch.gift.repository.GiftRepository;
import com.newtonwilliamsdesign.potlatch.gift.repository.UserRepository;

@RestController
public class GiftController {
	
	// The GiftRepository that we are going to store our Gifts
	// in. We don't explicitly construct a GiftRepository, but
	// instead mark this object as a dependency that needs to be
	// injected by Spring. Our Application class has a method
	// annotated with @Bean that determines what object will end
	// up being injected into this member variable.
	//
	// Also notice that we don't even need a setter for Spring to
	// do the injection.
	//
	@Autowired
	private GiftRepository gifts;
	
	@Autowired
	private UserRepository usrs;
	
	@Autowired
	private GiftFileManager giftDataMgr;

	private String getImageUrl(long giftId) {
		String url = getUrlBaseForLocalServer() + "/gift/" + giftId + "/image";
		return url;
	}
	
	private String getThumbUrl(long giftId) {
		String url = getUrlBaseForLocalServer() + "/gift/" + giftId + "/image";
		return url;
	}

	private String getUrlBaseForLocalServer() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String base = "https://" + request.getServerName() + ((request.getServerPort() != 80) ? ":" + request.getServerPort() : "");
		return base;
	}

	public void saveGift(Gift g, MultipartFile giftData) throws IOException {
		giftDataMgr.saveGiftData(g, giftData.getInputStream());
	}

	// Receives POST requests to /gift and converts the HTTP
	// request body, which should contain json, into a Gift
	// object before adding it to the list. The @RequestBody
	// annotation on the Gift parameter is what tells Spring
	// to interpret the HTTP request body as JSON and convert
	// it into a Gift object to pass into the method. The
	// @ResponseBody annotation tells Spring to convert the
	// return value from the method back into JSON and put
	// it into the body of the HTTP response to the client.
	//
	// The GIFT_SVC_PATH is set to "/gift" in the GiftSvcApi
	// interface. We use this constant to ensure that the 
	// client and service paths for the GiftSvc are always
	// in synch.
	//

	@RequestMapping(value = GiftSvcApi.GIFT_SVC_PATH, method = RequestMethod.POST)
	public Gift addGift(@RequestBody Gift g) {
		Gift savedGift = gifts.save(g);
		savedGift.setImageurl(getImageUrl(savedGift.getId()));
		savedGift.setThumburl(getThumbUrl(savedGift.getId()));
		return gifts.save(savedGift);
	}
	
	@RequestMapping(value = GiftSvcApi.GIFT_IMG_PATH, method = RequestMethod.POST)
	public GiftStatus setGiftData(
			@PathVariable(GiftSvcApi.ID_PARAMETER) long id,
			@RequestParam(GiftSvcApi.DATA_PARAMETER) MultipartFile giftData,
			HttpServletResponse response) throws IOException {
		// if id != null save the gift data
		if (null == gifts.findOne(id)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} else {
			saveGift(gifts.findOne(id), giftData);
		}
		return new GiftStatus(GiftState.READY);
	}
	
	// Receives GET requests to /video and returns the current
	// list of videos in memory. Spring automatically converts
	// the list of videos to JSON because of the @ResponseBody
	// annotation.
	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH, method=RequestMethod.GET)
	public Collection<Gift> getGiftList(){
		return Lists.newArrayList(gifts.findAll());
	}
	
	// Gift Chain List is a list of Gifts where parentId = 0
	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH + "/chain", method=RequestMethod.GET)
	public Collection<Gift> getGiftChainList(){
		return Lists.newArrayList(gifts.findAll());
	}
	
	// Get List of Top Ten Gift Givers
	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH + "/top", method=RequestMethod.GET)
	public ArrayList<GiftServiceUser> getTopTenGiftGiversList(){
		//return Lists.newArrayList(usrs.findTop10ByTouchedcount());
		return new ArrayList<GiftServiceUser>();
	}
	
	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH + "/{id}", method=RequestMethod.GET)
	public Gift getGiftById(@PathVariable long id,
											HttpServletResponse response) throws IOException {
		if (null == gifts.findOne(id)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		return gifts.findOne(id);
	}
	
	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH + "/{id}/touch", method=RequestMethod.POST)
	public Boolean touchVideo(@PathVariable long id, 
										Principal p, 
										HttpServletResponse response) throws IOException {
		Gift g = gifts.findOne(id);
		
		if (null == g) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} else {
			Set<String> touched = g.getTouchesUsernames();
			String username = p.getName();
			GiftServiceUser giftowner = usrs.findByUsername(g.getCreatedby().getUsername());
			int giftownerTouchedCount;
			
			if (touched.contains(username)) {
				// already liked, return 400
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			} else {
				// perform like, return 200
				Long touchedNum = g.getTouches();
				touched.add(username);
				g.setTouchesUsernames(touched);
				g.setTouches(++touchedNum);
				//gifts.save(g);
				giftownerTouchedCount = giftowner.getTouchedcount();
				giftowner.setTouchedcount(++giftownerTouchedCount);
				usrs.save(giftowner);
				response.setStatus(HttpServletResponse.SC_OK);
				return true;
			}
		}
		return false;
	}
	
	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH + "/{id}/untouch", method=RequestMethod.POST)
	public Boolean untouchVideo(@PathVariable long id, 
										  Principal p, 
										  HttpServletResponse response) throws IOException {
		Gift g = gifts.findOne(id);
		
		if (null == g) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} else {
			Set<String> touched = g.getTouchesUsernames();
			String username = p.getName();
			GiftServiceUser giftowner = usrs.findByUsername(g.getCreatedby().getUsername());
			int giftownerTouchedCount;
			
			if (touched.contains(username)) {
				// perform unlike, return 200
				Long touchedNum = g.getTouches();
				touched.remove(username);
				g.setTouchesUsernames(touched);
				g.setTouches(--touchedNum);
				//gifts.save(g);
				giftownerTouchedCount = giftowner.getTouchedcount();
				giftowner.setTouchedcount(--giftownerTouchedCount);
				usrs.save(giftowner);
				response.setStatus(HttpServletResponse.SC_OK);
				return true;
			} else {
				// user hasn't liked this video, return 400
				
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
		return false;
			
	}
	
	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH + "/{id}/touched", method=RequestMethod.GET)
	public Collection<String> getUsersTouchedByGift(@PathVariable long id,
																  HttpServletResponse response) throws IOException {
		if (null == gifts.findOne(id)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		Gift g = gifts.findOne(id);
		return g.getTouchesUsernames();
	}
	
	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH + "/{id}/flag", method=RequestMethod.POST)
	public Boolean flagVideo(@PathVariable long id, 
										Principal p, 
										HttpServletResponse response) throws IOException {
		Gift g = gifts.findOne(id);
		
		if (null == g) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} else {
			Set<String> flagged = g.getFlagsUsernames();
			String username = p.getName();
			
			if (flagged.contains(username)) {
				// already liked, return 400
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			} else {
				// perform like, return 200
				Long flaggedNum = g.getFlags();
				flagged.add(username);
				g.setFlagsUsernames(flagged);
				g.setFlags(++flaggedNum);
				gifts.save(g);
				response.setStatus(HttpServletResponse.SC_OK);
				return true;
			}
		}
		return false;
	}
	
	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH + "/{id}/unflag", method=RequestMethod.POST)
	public Boolean unflagVideo(@PathVariable long id, 
										  Principal p, 
										  HttpServletResponse response) throws IOException {
		Gift g = gifts.findOne(id);
		
		if (null == g) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} else {
			Set<String> flagged = g.getFlagsUsernames();
			String username = p.getName();
			
			if (flagged.contains(username)) {
				// perform unlike, return 200
				Long flaggedNum = g.getFlags();
				flagged.remove(username);
				g.setFlagsUsernames(flagged);
				g.setFlags(--flaggedNum);
				gifts.save(g);
				response.setStatus(HttpServletResponse.SC_OK);
				return true;
			} else {
				// user hasn't liked this video, return 400
				
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
		return false;
			
	}
	
	@RequestMapping(value=GiftSvcApi.GIFT_SVC_PATH + "/{id}/flaggedBy", method=RequestMethod.GET)
	public Collection<String> getUsersWhoFlaggedGift(@PathVariable long id,
																  HttpServletResponse response) throws IOException {
		if (null == gifts.findOne(id)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		Gift g = gifts.findOne(id);
		return g.getFlagsUsernames();
	}
	
	// Receives GET requests to /video/find and returns all Videos
	// that have a title (e.g., Video.name) matching the "title" request
	// parameter value that is passed by the client
	@RequestMapping(value=GiftSvcApi.GIFT_TITLE_SEARCH_PATH, method=RequestMethod.GET)
	public Collection<Gift> findByTitle(
			// Tell Spring to use the "title" parameter in the HTTP request's query
			// string as the value for the title method parameter
			@RequestParam(GiftSvcApi.TITLE_PARAMETER) String title
	){
		return gifts.findByTitle(title);
	}
	
	
}
