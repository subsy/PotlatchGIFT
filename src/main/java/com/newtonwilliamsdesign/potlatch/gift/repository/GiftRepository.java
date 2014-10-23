package com.newtonwilliamsdesign.potlatch.gift.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.newtonwilliamsdesign.potlatch.gift.client.GiftSvcApi;

/**
 * An interface for a repository that can store Gift
 * objects and allow them to be searched by title.
 */
@Repository
public interface GiftRepository extends CrudRepository<Gift, Long>{
	
	// Find all Gifts with a matching title (e.g., Gift.title)
	public Collection<Gift> findByTitle(
			// The @Param annotation tells Spring Data Rest which HTTP request
			// parameter it should use to fill in the "title" variable used to
			// search for Gifts
			@Param(GiftSvcApi.TITLE_PARAMETER) String title);
		
}
