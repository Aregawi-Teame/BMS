package com.membership.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.membership.domain.Membership;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long>{
	@Query(value = "select m.memberships from Member m join m.memberships as mem where m.id = :id and mem.membershipType = 'CHECKER'")
	List<Membership> findMembershipsByMembershipType_Checker(@Param("id") Long id);
}