package com.sdanzig.logalerter.server.controller;

import java.util.Base64;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdanzig.logalerter.server.entities.UserEntity;
import com.sdanzig.logalerter.server.exception.ResourceNotFoundException;
import com.sdanzig.logalerter.server.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class UserController {
	Logger logger = LoggerFactory.getLogger(UserController.class);

	private final UserRepository userRepository;

	@Autowired
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/users")
	public List<UserEntity> getUsers() {
		return userRepository.findAll();
	}

	@GetMapping("/users/{email}")
	public UserEntity getUserByEmail(@PathVariable String email) {
		byte[] decodedBytes = Base64.getDecoder().decode(email);
		String decodedEmail = new String(decodedBytes);
		logger.debug("getUserByEmail called with email address {}", decodedEmail);
		return userRepository.findByEmail(decodedEmail).orElseThrow(() ->
				new ResourceNotFoundException("UserEntity not found with email address specified."));
	}

	@PutMapping("/users")
	public UserEntity createUser(@Valid @RequestBody UserEntity user) { return userRepository.save(user); }

	@DeleteMapping("/users/{email}")
	public ResponseEntity<?> deleteUser(@PathVariable String email) {
		String decryptedEmail = Base64.getDecoder().decode(email).toString();
		logger.debug("deleteUser called with email address {}", decryptedEmail);
		return userRepository.findByEmail(email)
				.map(user -> {
					userRepository.delete(user);
					return ResponseEntity.ok().build();
				}).orElseThrow(() -> new ResourceNotFoundException("UserEntity not found with email " + decryptedEmail));
	}
}