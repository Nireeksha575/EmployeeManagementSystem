package com.example.EmployeeManagementSystem.Controller;

import com.example.EmployeeManagementSystem.DTO.RestaurantDTO;
import com.example.EmployeeManagementSystem.DTO.RestaurantRequest;
import com.example.EmployeeManagementSystem.Enum.MealSlot;
import com.example.EmployeeManagementSystem.Service.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Restaurant management endpoints.
 *
 * ── VENDOR-ONLY (write operations) ───────────────────────────────────────────
 * POST   /restaurants                        — Create a restaurant (vendor supplies vendorId in body)
 * PUT    /restaurants/{id}                   — Update restaurant details (vendor ownership verified)
 * PUT    /restaurants/{id}/deactivate        — Soft-delete a restaurant  (vendor ownership verified)
 * GET    /restaurants/vendor/{vendorId}      — List MY restaurants (vendor views their own)
 *
 * ── EMPLOYEE / MANAGER (read-only browse) ────────────────────────────────────
 * GET    /restaurants                        — Browse all active restaurants
 * GET    /restaurants/{id}                   — Get a specific restaurant
 * GET    /restaurants/by-slot?slot=LUNCH     — Find restaurants that serve a given meal slot
 *
 * Employees use the browse endpoints to pick a restaurant when creating a subscription.
 * Employees and managers cannot create, update, or deactivate restaurants.
 */
@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // ── VENDOR-ONLY write operations ─────────────────────────────────────────

    /**
     * Create a new restaurant.
     * Request body must include vendorId — the service validates the vendor exists.
     */
    @PostMapping
    public ResponseEntity<RestaurantDTO> createRestaurant(@RequestBody RestaurantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restaurantService.createRestaurant(request));
    }

    /**
     * Update a restaurant's name, address, description, or supported meal slots.
     * vendorId in the request body is used to verify ownership before any change is made.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(@PathVariable Long id,
                                                          @RequestBody RestaurantRequest request) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(id, request));
    }

    /**
     * Soft-deactivate a restaurant. Active subscriptions remain in the DB but
     * new deliveries will stop since the restaurant is inactive.
     * Pass vendorId as a query param so ownership can be verified.
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateRestaurant(@PathVariable Long id,
                                                       @RequestParam Long vendorId) {
        restaurantService.deactivateRestaurant(id, vendorId);
        return ResponseEntity.ok("Restaurant " + id + " has been deactivated.");
    }

    /**
     * List all active restaurants owned by a specific vendor.
     * Only the vendor themselves should call this to see their portfolio.
     */
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<RestaurantDTO>> getRestaurantsByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByVendor(vendorId));
    }

    // ── EMPLOYEE / MANAGER read-only browse ──────────────────────────────────

    /**
     * Browse all active restaurants.
     * Employees use this to discover available restaurants before subscribing.
     */
    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllActiveRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllActiveRestaurants());
    }

    /**
     * Get a specific restaurant by id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurant(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurant(id));
    }

    /**
     * Find all active restaurants that serve a specific meal slot.
     * Example: GET /restaurants/by-slot?slot=LUNCH
     * Employees call this when building their subscription to see valid options for each slot.
     */
    @GetMapping("/by-slot")
    public ResponseEntity<List<RestaurantDTO>> getRestaurantsByMealSlot(@RequestParam MealSlot slot) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByMealSlot(slot));
    }
}