package com.newtonwilliamsdesign.potlatch.gift.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.newtonwilliamsdesign.potlatch.gift.domain.GiftServiceUser;

/**
 * An interface for a repository that can store User
 * objects and allow them to be searched by username.
 */
@Repository
public interface UserRepository extends CrudRepository<GiftServiceUser, Long>{
	
	// Find User with a matching username (e.g., User.username)
	public GiftServiceUser findByUsername(String username);
	
	// Get list of users with highest TouchedCounts
	//public List<User> findTop10ByTouchedcount();
}
