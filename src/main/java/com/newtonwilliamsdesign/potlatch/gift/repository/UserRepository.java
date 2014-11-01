package com.newtonwilliamsdesign.potlatch.gift.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.newtonwilliamsdesign.potlatch.gift.auth.User;

/**
 * An interface for a repository that can store User
 * objects and allow them to be searched by username.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long>{
	
	// Find User with a matching username (e.g., User.username)
	public User findByUsername(String username);
	
	// Get list of users with highest TouchedCounts
	//public List<User> findTop10ByTouchedcount();
}
