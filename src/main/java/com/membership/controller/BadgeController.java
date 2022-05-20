package com.membership.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.membership.domain.Badge;
import com.membership.service.BadgeService;
import com.membership.service.NotAuthorizedException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {

	@Autowired
	private BadgeService badgeService;
	
	@GetMapping
	public List<Badge> findAll(){
		return badgeService.findAll();
	}
	
	@PutMapping("/{id}")
	public Badge updateBadge(@PathVariable(name="id") String id, @RequestBody @Valid Badge updatedBadge ) {
		Long badgeId = Long.parseLong(id);
		return badgeService.update(badgeId, updatedBadge);
	}
	
	@GetMapping("/{id}")
	public Badge findById(@PathVariable(name="id") String id) {
		Long badgeId = Long.parseLong(id);
		return badgeService.findById(badgeId);
	}
	@DeleteMapping("/{id}")
	public void deleteById(@PathVariable(name = "id") String id){
		Long badgeId = Long.parseLong(id);
		badgeService.deleteById(badgeId);
	}
	
	@GetMapping("/swipe")
	public String hasAccess(@RequestParam(name="badgeId") Long badgeId,@RequestParam(name="locationId") long locationId, @RequestParam(name="planId") long planId) throws NotAuthorizedException {
		return badgeService.hasAccess(badgeId, locationId, planId);
	}
}
