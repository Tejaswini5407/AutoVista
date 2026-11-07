package com.autovista.dto.response;

public class AdminStatsResponse {

    private long totalNewModels;
    private long totalNewVariants;
    private long totalUsedCars;
    private long usedAvailable;
    private long usedBooked;
    private long usedSold;
    private long totalPricingRules;
    private long totalBookings;
    private long confirmedBookings;

    public AdminStatsResponse() {}

    public AdminStatsResponse(long totalNewModels, long totalNewVariants, long totalUsedCars,
                              long usedAvailable, long usedBooked, long usedSold, long totalPricingRules) {
        this(totalNewModels, totalNewVariants, totalUsedCars, usedAvailable, usedBooked, usedSold, totalPricingRules, 0, 0);
    }

    public AdminStatsResponse(long totalNewModels, long totalNewVariants, long totalUsedCars,
                              long usedAvailable, long usedBooked, long usedSold, long totalPricingRules,
                              long totalBookings, long confirmedBookings) {
        this.totalNewModels = totalNewModels;
        this.totalNewVariants = totalNewVariants;
        this.totalUsedCars = totalUsedCars;
        this.usedAvailable = usedAvailable;
        this.usedBooked = usedBooked;
        this.usedSold = usedSold;
        this.totalPricingRules = totalPricingRules;
        this.totalBookings = totalBookings;
        this.confirmedBookings = confirmedBookings;
    }

    public long getTotalNewModels() { return totalNewModels; }
    public void setTotalNewModels(long totalNewModels) { this.totalNewModels = totalNewModels; }

    public long getTotalNewVariants() { return totalNewVariants; }
    public void setTotalNewVariants(long totalNewVariants) { this.totalNewVariants = totalNewVariants; }

    public long getTotalUsedCars() { return totalUsedCars; }
    public void setTotalUsedCars(long totalUsedCars) { this.totalUsedCars = totalUsedCars; }

    public long getUsedAvailable() { return usedAvailable; }
    public void setUsedAvailable(long usedAvailable) { this.usedAvailable = usedAvailable; }

    public long getUsedBooked() { return usedBooked; }
    public void setUsedBooked(long usedBooked) { this.usedBooked = usedBooked; }

    public long getUsedSold() { return usedSold; }
    public void setUsedSold(long usedSold) { this.usedSold = usedSold; }

    public long getTotalPricingRules() { return totalPricingRules; }
    public void setTotalPricingRules(long totalPricingRules) { this.totalPricingRules = totalPricingRules; }

    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }

    public long getConfirmedBookings() { return confirmedBookings; }
    public void setConfirmedBookings(long confirmedBookings) { this.confirmedBookings = confirmedBookings; }
}
