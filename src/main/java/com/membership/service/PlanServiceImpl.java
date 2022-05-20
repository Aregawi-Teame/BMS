package com.membership.service;

import com.membership.domain.ActivityType;
import com.membership.domain.Location;
import com.membership.domain.Plan;
import com.membership.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlanServiceImpl implements PlanService{
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private LocationService locationService;
    @Autowired
    private ActivityTypeService activityTypeService;
    
    @Override
    public List<Plan> findAll() {
        return planRepository.findAll();
    }

    @Override
    public Plan findById(Long id) {
        return planRepository.findById(id).get();
    }

    @Override
    public Plan save(Plan plan) throws NotAuthorizedException {
    	Set<Long> locationIds = plan.getLocations().stream().map(l->l.getId()).collect(Collectors.toSet());
    	Set<Location> locations = locationIds.stream().map(id -> locationService.findById(id)).collect(Collectors.toSet());
    	ActivityType planActivity = activityTypeService.findById(plan.getActivityType().getId());
    	boolean matches = locations.stream().allMatch(l -> l.getTimeSlots().stream()
    			.anyMatch(t -> t.getActivityType().getActivityName().equals(planActivity.getActivityName())));
    	
    	if(!matches) throw new NotAuthorizedException("Location has no matching activity for this plan.");
    	
        return planRepository.save(plan);
    }

    @Override
    public Plan update(Plan plan, Long id) {
        Plan existingPlan = findById(id);
        Plan updateResponse = null;
        if(existingPlan != null){
            updateResponse = planRepository.save(plan);
        }
        return updateResponse;
    }

    @Override
    public void deleteById(Long id) {
        planRepository.deleteById(id);
    }

    @Override
    public Set<Location> getAllPlanLocations(Long id) {
        return planRepository.getById(id).getLocations();
    }
}
