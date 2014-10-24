package com.newtonwilliamsdesign.potlatch.gift.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.newtonwilliamsdesign.potlatch.gift.auth.User;
import com.newtonwilliamsdesign.potlatch.gift.client.GiftSvcApi;

/**
 * An interface for a repository that can store Gift
 * objects and allow them to be searched by title.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long>{
	
	// Find User with a matching username (e.g., User.username)
	public User findByUsername_(String username);
	
	// Get list of users with highest TouchedCounts
	public List<User> findTop10ByTouchedcntOrderByTouchedcntDesc();
}
