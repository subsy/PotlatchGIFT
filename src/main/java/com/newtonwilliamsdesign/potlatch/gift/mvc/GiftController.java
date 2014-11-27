package com.newtonwilliamsdesign.potlatch.gift.mvc;

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

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.collect.Lists;
import com.newtonwilliamsdesign.potlatch.gift.domain.Gift;
import com.newtonwilliamsdesign.potlatch.gift.domain.GiftServiceUser;
import com.newtonwilliamsdesign.potlatch.gift.domain.GiftUserPreferences;
import com.newtonwilliamsdesign.potlatch.gift.repository.GiftRepository;
import com.newtonwilliamsdesign.potlatch.gift.repository.GiftServiceUserRepository;

@RestController
public class GiftController {
	
	@Autowired
	private GiftRepository gifts;
	
	@Autowired
	private GiftServiceUserRepository usrs;
	
	@Autowired
	ServletContext ctx;

	@RequestMapping(value = ControllerPaths.GIFT_SVC_PATH, method = RequestMethod.POST)
	public Gift addGift(@RequestBody Gift g, Principal p) {
		Gift savedGift = gifts.save(g);
		if (g.getParentid() != 0)
		{
			Gift parentGift = gifts.findOne(g.getParentid());
			parentGift.setModifiedon(g.getModifiedon());
			gifts.save(parentGift);
		}
		GiftServiceUser gUser = usrs.findByUsername(p.getName());
		savedGift.setCreatedby(gUser);
		return gifts.save(savedGift);
	}
	
	@RequestMapping(value=ControllerPaths.GIFT_SVC_PATH, method=RequestMethod.GET)
	public Collection<Gift> getGiftList(){
		return Lists.newArrayList(gifts.findAll());
	}
	
	// Gift Chain List is a list of Gifts where parentId = 0
	@RequestMapping(value=ControllerPaths.GIFT_SVC_PATH + "/chain", method=RequestMethod.GET)
	public Collection<Gift> getGiftChainList(Principal p,
											HttpServletResponse response) throws IOException {
		if (null == usrs.findByUsername(p.getName())) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		GiftUserPreferences prefs = usrs.findByUsername(p.getName()).getUserprefs();
		if(prefs.isDisplayFlagged()) {
			return Lists.newArrayList(gifts.findByParentidOrderByModifiedonDesc(0));
		} else return Lists.newArrayList(gifts.findByParentidAndFlagsOrderByModifiedonDesc(0, 0));
	}
	
	@RequestMapping(value=ControllerPaths.GIFT_SVC_PATH + "/chain" + "/{id}", method=RequestMethod.GET)
	public Collection<Gift> getGiftChainChildren(@PathVariable long id, Principal p,
										HttpServletResponse response) throws IOException {
		if (null == gifts.findOne(id)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		if (null == usrs.findByUsername(p.getName())) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		GiftUserPreferences prefs = usrs.findByUsername(p.getName()).getUserprefs();
		if(prefs.isDisplayFlagged()) {
			return Lists.newArrayList(gifts.findByParentidOrderByCreatedonAsc(id));
		}
		else return Lists.newArrayList(gifts.findByParentidAndFlags(id, 0));
	}
	
	// Get List of Top Ten Gift Givers
	@RequestMapping(value=ControllerPaths.GIFT_SVC_PATH + "/top", method=RequestMethod.GET)
	public ArrayList<GiftServiceUser> getTopTenGiftGiversList(){
		return Lists.newArrayList(usrs.findTop10ByTouchedcountGreaterThanOrderByTouchedcountDesc(0));
		
	}
	
	@RequestMapping(value=ControllerPaths.GIFT_SVC_PATH + "/{id}", method=RequestMethod.GET)
	public Gift getGiftById(@PathVariable long id,
											HttpServletResponse response) throws IOException {
		if (null == gifts.findOne(id)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		return gifts.findOne(id);
	}
	
	@RequestMapping(value=ControllerPaths.GIFT_SVC_PATH + "/{id}", method=RequestMethod.DELETE)
	public Boolean deleteGiftById(@PathVariable long id,
											Principal p,
											HttpServletResponse response) throws IOException {
		if (null == gifts.findOne(id)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		if (null == usrs.findByUsername(p.getName())) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}

		Gift g = gifts.findOne(id);
		GiftServiceUser u = usrs.findByUsername(p.getName());
		if (g.getCreatedby().equals(u)) {
			if (gifts.findByParentid(id).isEmpty()) { // only allow gift to be deleted if it has no children
				long touches = g.getTouches();
				u.setTouchedcount((int) (u.getTouchedcount() - touches));
				gifts.delete(g);
				response.setStatus(HttpServletResponse.SC_OK);
				return true;
			}
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return false;
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return false;
		}
	}
	
	@RequestMapping(value=ControllerPaths.GIFT_SVC_PATH + "/{id}/touch", method=RequestMethod.POST)
	public long touchGift(@PathVariable long id, 
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
				// already touched, return 400
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			} else {
				// perform touch, return 200
				Long touchedNum = g.getTouches();
				touched.add(username);
				g.setTouchesUsernames(touched);
				g.setTouches(++touchedNum);
				gifts.save(g);
				giftownerTouchedCount = giftowner.getTouchedcount();
				giftowner.setTouchedcount(++giftownerTouchedCount);
				usrs.save(giftowner);
				response.setStatus(HttpServletResponse.SC_OK);
			}
		}
		return g.getTouches();
	}
	
	@RequestMapping(value=ControllerPaths.GIFT_SVC_PATH + "/{id}/untouch", method=RequestMethod.POST)
	public long untouchGift(@PathVariable long id, 
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
				// perform untouch, return 200
				Long touchedNum = g.getTouches();
				touched.remove(username);
				g.setTouchesUsernames(touched);
				g.setTouches(--touchedNum);
				gifts.save(g);
				giftownerTouchedCount = giftowner.getTouchedcount();
				giftowner.setTouchedcount(--giftownerTouchedCount);
				usrs.save(giftowner);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				// user hasn't touched this Gift, return 400
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
		return g.getTouches();
			
	}
	
	@RequestMapping(value=ControllerPaths.GIFT_SVC_PATH + "/{id}/touched", method=RequestMethod.GET)
	public Collection<String> getUsersTouchedByGift(@PathVariable long id,
																  HttpServletResponse response) throws IOException {
		if (null == gifts.findOne(id)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		Gift g = gifts.findOne(id);
		return g.getTouchesUsernames();
	}
	
	@RequestMapping(value=ControllerPaths.GIFT_SVC_PATH + "/{id}/flag", method=RequestMethod.POST)
	public long flagGift(@PathVariable long id, 
										Principal p, 
										HttpServletResponse response) throws IOException {
		Gift g = gifts.findOne(id);
		
		if (null == g) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} else {
			Set<String> flagged = g.getFlagsUsernames();
			String username = p.getName();
			
			if (flagged.contains(username)) {
				// already flagged, return 400
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			} else {
				// perform flag, return 200
				Long flaggedNum = g.getFlags();
				flagged.add(username);
				g.setFlagsUsernames(flagged);
				g.setFlags(++flaggedNum);
				gifts.save(g);
				response.setStatus(HttpServletResponse.SC_OK);
			}
		}
		return g.getFlags();
	}
	
	@RequestMapping(value=ControllerPaths.GIFT_SVC_PATH + "/{id}/unflag", method=RequestMethod.POST)
	public long unflagGift(@PathVariable long id, 
										  Principal p, 
										  HttpServletResponse response) throws IOException {
		Gift g = gifts.findOne(id);
		
		if (null == g) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} else {
			Set<String> flagged = g.getFlagsUsernames();
			String username = p.getName();
			
			if (flagged.contains(username)) {
				// perform unflag, return 200
				Long flaggedNum = g.getFlags();
				flagged.remove(username);
				g.setFlagsUsernames(flagged);
				g.setFlags(--flaggedNum);
				gifts.save(g);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				// user hasn't flagged this gift, return 400
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
		return g.getFlags();	
	}
	
	@RequestMapping(value=ControllerPaths.GIFT_SVC_PATH + "/{id}/flaggedBy", method=RequestMethod.GET)
	public Collection<String> getUsersWhoFlaggedGift(@PathVariable long id,
																  HttpServletResponse response) throws IOException {
		if (null == gifts.findOne(id)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		Gift g = gifts.findOne(id);
		return g.getFlagsUsernames();
	}
	
	
	@RequestMapping(value=ControllerPaths.GIFT_TITLE_SEARCH_PATH, method=RequestMethod.GET)
	public Collection<Gift> findByTitle(@RequestParam(ControllerPaths.TITLE_PARAMETER) String title,
										Principal p,
										HttpServletResponse response) throws IOException {
		if (null == usrs.findByUsername(p.getName())) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		GiftUserPreferences prefs = usrs.findByUsername(p.getName()).getUserprefs();
		if(prefs.isDisplayFlagged()) {
			return Lists.newArrayList(gifts.findByTitleContaining(title));
		} else return Lists.newArrayList(gifts.findByTitleContainingAndFlags(title, 0));
	}
	
	
}
