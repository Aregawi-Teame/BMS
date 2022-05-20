package com.membership.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.membership.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.membership.repository.BadgeRepository;
import com.membership.repository.TimeSlotRepository;

@Service
@Transactional
public class BadgeServiceImpl implements BadgeService {

	@Autowired
	private BadgeRepository badgeRepository;
	@Autowired
	LocationService locationService;
	@Autowired
	private TransactionService transactionService;

	@Override
	public List<Badge> findAll() {
		return badgeRepository.findAll();
	}

	@Override
	public Badge findById(Long id) {
		return badgeRepository.findById(id).get();
	}

	@Override
	public Badge save(Badge badge) {
		return badgeRepository.save(badge);
	}

	@Override
	public Badge update(Long badgeId, Badge updatedBadge) {
		Badge oldBadge = findById(badgeId);
		if (updatedBadge.getIssueDate() != null)
			oldBadge.setIssueDate(updatedBadge.getIssueDate());
		if (updatedBadge.getExpirationDate() != null)
			oldBadge.setExpirationDate(updatedBadge.getExpirationDate());
		oldBadge.setActive(updatedBadge.isActive());

		save(oldBadge);

		return oldBadge;
	}

	@Override
	public void deleteById(Long id) {
		badgeRepository.deleteById(id);
	}

	@Override
	public Member findBadgeMember(Long badgeId) {
		return findById(badgeId).getMember();
	}

	@Override
	public boolean hasAccess(Long badgeId, Long locationId, Long planId) {
		// initial reasons to decline access (no badge, location or plan)
		if (badgeId == null || locationId == null || planId == null)
			return false;
		// fetch location from db
		Location accessLocation = locationService.findById(locationId);
		// Get the time of access attempt
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalTime currentTime = LocalTime.now();
		// invalid badge
		Badge badge = findById(badgeId);
		if (badge == null) {
			createTransaction(currentDateTime, accessLocation, false, TransactionDescription.INVALID_BADGE_ID, null,
					null, null);
			return false;
		}

		// fetch member from given badge
		Member member = badge.getMember();
		// check if member is available
		if (member == null) {
			createTransaction(currentDateTime, accessLocation, false, TransactionDescription.BADGE_HAS_NO_MEMBER, null,
					null, null);
			return false; // Not authorized
		}
		// check if badge is active and not expired
		if (!badge.isActive()) {
			createTransaction(currentDateTime, accessLocation, false, TransactionDescription.INACTIVE_BADGE, null, null,
					member);
			return false; // Not authorized
		}
		if (badge.getExpirationDate().isBefore(LocalDate.now())) {
			createTransaction(currentDateTime, accessLocation, false, TransactionDescription.EXPIRED_BADGE, null, null,
					member);
			return false; // Not authorized
		}
		Membership membership = member.getMemberships().stream()
				.filter(membship -> membship.getPlan().getId() == planId).findFirst().orElse(null);
		// check if membership is available
		if (membership == null) {
			createTransaction(currentDateTime, accessLocation, false, TransactionDescription.BADGE_HAS_NO_MEMBERSHIP,
					null, null, member);
			return false; // Not authorized
		}

		// check if membership is not expired
		if (membership.getEndDate().isBefore(LocalDate.now())) {
			createTransaction(currentDateTime, accessLocation, false, TransactionDescription.EXPIRED_MEMBERSHIP,
					membership, null, member);
			return false; // Membership is expired
		}

		// get the timeslots of the current day for this location
		Integer dayOfTheWeek = LocalDate.now().getDayOfWeek().getValue();
		List<TimeSlot> currentDayTimeSlots = accessLocation.getTimeSlots().stream()
				.filter(s -> s.getDayOfWeek().valueOfTheDay() == dayOfTheWeek).collect(Collectors.toList());
		// check if timeslot is present (duration is allowed for access)
		Optional<TimeSlot> timeSlot = currentDayTimeSlots.stream()
				.filter(s -> s.getStartTime().isBefore(currentTime) && s.getEndTime().isAfter(currentTime)).findFirst();
		if (!timeSlot.isPresent()) {
			createTransaction(currentDateTime, accessLocation, false, TransactionDescription.NOT_ALLOWED_DURATION,
					membership, null, member);
			return false; // this means out of time or not opened yet;
		}

		// check if membership limit has been reached
		if (membership.getMembershipType() == MembershipType.LIMITED) {
			long successfulTransactionsCount = membership.getTransactions().stream()
					.filter(tx -> tx.getDateTime().getMonthValue() == LocalDate.now().getMonthValue())
					.filter(tx -> tx.getDateTime().getYear() == LocalDate.now().getYear())
					.filter(tx -> tx.isSuccessful()).filter(tx -> tx.getMembership().getPlan().getActivityType()
							.getActivityName() == timeSlot.get().getActivityType().getActivityName())
					.count();
			if (membership.getQuota() <= successfulTransactionsCount) {
				createTransaction(currentDateTime, accessLocation, false, TransactionDescription.LIMIT_QUOTA_REACHED,
						membership, timeSlot.get().getActivityType(), member);
				return false;// quota reached
			}
		}

		// successful access
		createTransaction(currentDateTime, accessLocation, true, TransactionDescription.SUCCESSFUL_ACCESS, membership,
				timeSlot.get().getActivityType(), member);

		return true;
	}

	private void createTransaction(LocalDateTime currentDateTime, Location accessLocation, boolean isSuccessful,
			TransactionDescription transactionDescription, Membership membership, ActivityType activityType,
			Member member) {

		Transaction transaction = new Transaction();
		transaction.setDateTime(currentDateTime);
		transaction.setLocation(accessLocation);

		if (transactionDescription == TransactionDescription.BADGE_HAS_NO_MEMBER
				|| transactionDescription == TransactionDescription.INVALID_BADGE_ID) {
			transaction.setSuccessful(isSuccessful);
			transaction.setTransactionDescription(transactionDescription);
		} else if (transactionDescription == TransactionDescription.BADGE_HAS_NO_MEMBERSHIP
				|| transactionDescription == TransactionDescription.INACTIVE_BADGE
				|| transactionDescription == TransactionDescription.EXPIRED_BADGE) {
			transaction.setSuccessful(isSuccessful);
			transaction.setTransactionDescription(transactionDescription);
			member.addTransaction(transaction);
		} else if (transactionDescription == TransactionDescription.EXPIRED_MEMBERSHIP
				|| transactionDescription == TransactionDescription.NOT_ALLOWED_DURATION
				|| transactionDescription == TransactionDescription.LIMIT_QUOTA_REACHED
				|| transactionDescription == TransactionDescription.SUCCESSFUL_ACCESS) {
			transaction.setSuccessful(isSuccessful);
			transaction.setTransactionDescription(transactionDescription);
			transaction.setMembership(membership);
			membership.addTransaction(transaction);
			member.addTransaction(transaction);
		}

		transactionService.save(transaction);
	}
}
