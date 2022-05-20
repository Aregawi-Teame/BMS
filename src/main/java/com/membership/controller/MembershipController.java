package com.membership.controller;
import java.util.List;

import com.membership.service.MemberService;
import com.membership.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.membership.domain.Location;
import com.membership.domain.Member;
import com.membership.domain.Membership;
import com.membership.domain.Plan;
import com.membership.domain.Transaction;
import com.membership.repository.MemberRepository;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/memberships")
public class MembershipController 
{
	@Autowired
	MembershipService membershipService;

	@Autowired
	MemberService memberService;

	@Autowired
	MemberRepository memberRepository;
	
	@GetMapping
	public List<Membership> findAll()
	{
		return membershipService.findAll();
	}

	@GetMapping("/{id}")
	public Membership findById(@PathVariable(name="id") String id) 
	{
		return membershipService.findById(Long.parseLong(id));
	}
	


	@GetMapping("/{id}/plan")
	public Plan findPlanById(@PathVariable(name="id") String id) 
	{
		return membershipService.findById(Long.parseLong(id)).getPlan();
	}

//	@GetMapping("/{id}/location")
//	public Location findLocationById(@PathVariable(name="id") String id)
//	{
//		return membershipService.findById(Long.parseLong(id)).getLocation();
//
//		//membershipService.findById(Long.parseLong(id)).getLocation().
//	}

	@GetMapping("/{id}/transactions")
	public List<Transaction> findTransactionsById(@PathVariable(name="id") String id) 
	{
		return membershipService.findById(Long.parseLong(id)).getTransactions();
	}

//	@PatchMapping("/{id}")
//	public Membership addLocaitonForMembership(@PathVariable(name = "id") Long id, @RequestBody Location location){
//		return membershipService.addLocation(id, location);
//	}
	@PatchMapping("/{id}/plan")
	public Membership addPlanForMembership(@PathVariable(name = "id") Long id, @RequestBody @Valid Plan plan){
		return membershipService.addPlanForMembership(id, plan);
	}
	

}