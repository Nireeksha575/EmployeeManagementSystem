package com.example.EmployeeManagementSystem.Service;

import com.example.EmployeeManagementSystem.DTO.SubscriptionDTO;
import com.example.EmployeeManagementSystem.DTO.SubscriptionRequest;
import com.example.EmployeeManagementSystem.Entity.Employee;
import com.example.EmployeeManagementSystem.Entity.Subscription;
import com.example.EmployeeManagementSystem.Enum.MealSlot;
import com.example.EmployeeManagementSystem.Enum.ScheduleType;
import com.example.EmployeeManagementSystem.Enum.SubscriptionStatus;
import com.example.EmployeeManagementSystem.Exception.EmployeeNotFound;
import com.example.EmployeeManagementSystem.Exception.SubscriptionAlreadyExists;
import com.example.EmployeeManagementSystem.Repository.EmployeeRepo;
import com.example.EmployeeManagementSystem.Repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final EmployeeRepo employeeRepo;
    private final DeliveryTimeService deliveryTimeService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, EmployeeRepo employeeRepo, DeliveryTimeService deliveryTimeService) {
        this.subscriptionRepository = subscriptionRepository;
        this.employeeRepo = employeeRepo;
        this.deliveryTimeService = deliveryTimeService;
    }

    public void addSubscription(SubscriptionRequest request) {
        Employee employee=employeeRepo.findById(request.getEmployeeId())
                .orElseThrow(
                        ()->new EmployeeNotFound("employee with Id "+request.getEmployeeId()+" not found")
                        );
        if (request.getMealSlots() == null || request.getMealSlots().isEmpty()) {
            throw new IllegalArgumentException("Meal slots cannot be null or empty");
        }


        if (request.getScheduleType() == ScheduleType.DAILY) {
            request.setDayOfWeek(null); // must be null
        }
        else if (request.getScheduleType() == ScheduleType.WEEKLY) {

            if (request.getDayOfWeek() == null) {
                throw new IllegalArgumentException("DayOfWeek required for WEEKLY schedule");
            }
        }
        for (MealSlot slot : request.getMealSlots()) {


            if (subscriptionRepository.checkSubscriptionExists(
                    request.getEmployeeId(),
                    slot.name(),
                    SubscriptionStatus.ACTIVE.name()) > 0) {

                throw new SubscriptionAlreadyExists(
                        "Subscription already exists for user: "
                                + request.getEmployeeId() + " with slot " + slot.name());
            }

            Subscription subscription = new Subscription();
            subscription.setEmployee(employee);
            subscription.setSlot(slot);
            subscription.setScheduleType(request.getScheduleType());
            subscription.setDayOfWeek(request.getDayOfWeek());
            subscription.setStatus(SubscriptionStatus.ACTIVE);


            subscription.setNextDeliveryTime(
                    deliveryTimeService.calculateNextDelivery(request, slot,employee)
            );

            subscriptionRepository.save(subscription);
        }
    }


    public List<SubscriptionDTO> getAllSubscriptions() {
        List<Subscription> subscriptions=subscriptionRepository.findAll();
        List<SubscriptionDTO> subscriptionDTOList=new ArrayList<>();
        for (Subscription subscription:subscriptions){
            SubscriptionDTO dto=convertToDTO(subscription);
            subscriptionDTOList.add(dto);
        }
        return subscriptionDTOList;
    }

    private SubscriptionDTO convertToDTO(Subscription subscription){
        SubscriptionDTO subscriptionDTO=new SubscriptionDTO();
        subscriptionDTO.setSubscriptionId(subscription.getId());
        subscriptionDTO.setUserId(subscription.getEmployee().getEmployeeId());
        subscriptionDTO.setMealSlot(subscription.getSlot());
        subscriptionDTO.setScheduleType(subscription.getScheduleType());
        if(subscription.getScheduleType()==ScheduleType.WEEKLY){
            subscriptionDTO.setDayOfWeek(subscription.getDayOfWeek());
        }
        subscriptionDTO.setStatus(subscription.getStatus());
        if(subscription.getNextDeliveryTime()!=null){
            ZoneId userZone=ZoneId.of(subscription.getEmployee().getTimezone());
            ZonedDateTime zonedDateTime=subscription.getNextDeliveryTime().atZone(userZone);
            subscriptionDTO.setNextDeliveryTime(zonedDateTime);
        }
        return subscriptionDTO;
    }

    public Subscription updateSubscription(Long id, SubscriptionRequest request) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
        Employee employee=subscription.getEmployee();


        if (request.getScheduleType() == ScheduleType.DAILY) {
            subscription.setDayOfWeek(null);
        }
        else if (request.getScheduleType() == ScheduleType.WEEKLY) {

            if (request.getDayOfWeek() == null) {
                throw new IllegalArgumentException("DayOfWeek required");
            }

            subscription.setDayOfWeek(request.getDayOfWeek());
        }

        subscription.setScheduleType(request.getScheduleType());

        if (request.getMealSlots() != null && !request.getMealSlots().isEmpty()) {
            if (request.getMealSlots().size() > 1) {
                throw new IllegalArgumentException(
                        "Update supports only one meal slot at a time. Use separate subscriptions for multiple slots.");
            }
            subscription.setSlot(request.getMealSlots().get(0));
        }

        subscription.setNextDeliveryTime(
                deliveryTimeService.calculateNextDelivery(request, subscription.getSlot(),employee)
        );

        return subscriptionRepository.save(subscription);
    }

    public Subscription pauseSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setStatus(SubscriptionStatus.PAUSED);
        subscription.setNextDeliveryTime(null);

        return subscriptionRepository.save(subscription);
    }

    public Subscription resumeSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setStatus(SubscriptionStatus.ACTIVE);

        subscription.setNextDeliveryTime(
                deliveryTimeService.getNextDeliveryTime(subscription)
        );

        return subscriptionRepository.save(subscription);
    }

    public Subscription expireSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setStatus(SubscriptionStatus.EXPIRED);
        subscription.setNextDeliveryTime(null);

        return subscriptionRepository.save(subscription);
    }

    public List<SubscriptionDTO> getSubscriptionOfuser(long id) {
        List<Subscription> subscriptions= subscriptionRepository.findByEmployee_employeeId(id);
        List<SubscriptionDTO> subscriptionDTOList=new ArrayList<>();
        for (Subscription subscription:subscriptions){
            SubscriptionDTO dto=convertToDTO(subscription);
            subscriptionDTOList.add(dto);
        }
        return subscriptionDTOList;
    }
}
