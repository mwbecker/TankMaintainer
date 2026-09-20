package com.michaelbecker.tankmaintainer.service;

import com.michaelbecker.tankmaintainer.model.Tank;
import com.michaelbecker.tankmaintainer.model.WaterChange;
import com.michaelbecker.tankmaintainer.model.AppUser;
import com.michaelbecker.tankmaintainer.repository.WaterChangeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WaterChangeService {

    /**
     * Typical hobbyist water changes sit in this band. The historical average
     * interval already reflects that cadence, so these fractions do not move
     * the predicted date.
     */
    static final double BASELINE_LOW = 0.15;
    static final double BASELINE_HIGH = 0.20;

    /**
     * Beyond this fraction of tank volume, extra water changed no longer
     * stretches the schedule. A 50%+ change is already a large reset.
     */
    static final double VOLUME_FRACTION_CAP = 0.50;

    /** Maximum extra wait after a capped large change: +50% of the average interval. */
    static final double MAX_STRETCH = 0.50;

    /** Maximum pull-in after a very small change: -25% of the average interval. */
    static final double MAX_COMPRESS = 0.25;

    private final WaterChangeRepository waterChangeRepository;

    public WaterChangeService(WaterChangeRepository waterChangeRepository) {
        this.waterChangeRepository = waterChangeRepository;
    }

    public List<WaterChange> getAll(AppUser user) {
        return waterChangeRepository.findByUser(user);
    }

    public Optional<WaterChange> getById(UUID id) {
        return waterChangeRepository.findById(id);
    }

    public List<WaterChange> getByTankId(UUID tankId) {
        return waterChangeRepository.findByTankId(tankId);
    }

    public WaterChange save(WaterChange change) {
        return waterChangeRepository.save(change);
    }

    public void delete(UUID id) {
        waterChangeRepository.deleteById(id);
    }

    public Optional<LocalDate> getLastChangeDate(UUID tankId) {
        return waterChangeRepository.findByTankId(tankId).stream()
                .max(Comparator.comparing(WaterChange::getDate))
                .map(change -> change.getDate().toLocalDate());
    }

    public LocalDate predictNextMaintenance(UUID tankId) {
        List<WaterChange> changes = new ArrayList<>(waterChangeRepository.findByTankId(tankId));

        if (changes.size() < 2) {
            // Fallback: not enough history, default to weekly
            return LocalDate.now().plusDays(7);
        }

        // Sort by date ascending
        changes.sort(Comparator.comparing(WaterChange::getDate));

        // Calculate average interval
        long totalDays = 0;
        for (int i = 1; i < changes.size(); i++) {
            long diff = ChronoUnit.DAYS.between(
                changes.get(i - 1).getDate().toLocalDate(),
                changes.get(i).getDate().toLocalDate()
            );
            totalDays += diff;
        }

        long avgInterval = Math.round((double) totalDays / (changes.size() - 1));

        WaterChange lastChange = changes.get(changes.size() - 1);
        LocalDate lastChangeDate = lastChange.getDate().toLocalDate();
        long adjustedInterval = adjustIntervalForLastChangeVolume(
                avgInterval,
                lastChange.getVolumeGallons(),
                tankVolumeGallons(lastChange)
        );

        return lastChangeDate.plusDays(adjustedInterval);
    }

    /**
     * Scales the historical average interval using the most recent change's
     * volume as a fraction of tank size.
     *
     * <p>Waste rebuilds roughly in proportion to how much was removed, but a
     * typical 15–20% change is already baked into the average cadence. Above
     * 20%, extra volume buys more time with diminishing returns, saturating at
     * a 50% water change ({@code interval * 1.5}). Below 15%, the clock is
     * pulled in slightly because less of the tank was reset.
     */
    static long adjustIntervalForLastChangeVolume(
            long averageIntervalDays,
            BigDecimal lastChangeGallons,
            BigDecimal tankGallons
    ) {
        long baseline = Math.max(averageIntervalDays, 1);
        if (lastChangeGallons == null || tankGallons == null) {
            return baseline;
        }

        double tank = tankGallons.doubleValue();
        double changed = lastChangeGallons.doubleValue();
        if (tank <= 0 || changed <= 0) {
            return baseline;
        }

        double fraction = Math.min(changed / tank, VOLUME_FRACTION_CAP);
        double multiplier = volumeMultiplier(fraction);
        return Math.max(1, Math.round(baseline * multiplier));
    }

    /**
     * Piecewise multiplier for last-change volume fraction {@code f}:
     * <pre>
     *   f ≤ 0.15 : 1 - 0.25 * (0.15 - f) / 0.15
     *   0.15–0.20: 1
     *   f ≥ 0.20 : 1 + 0.50 * (1 - (1 - x)^2)
     *              where x = min((f - 0.20) / 0.30, 1)
     * </pre>
     */
    static double volumeMultiplier(double fraction) {
        if (fraction < BASELINE_LOW) {
            double t = (BASELINE_LOW - fraction) / BASELINE_LOW;
            return 1.0 - MAX_COMPRESS * clamp01(t);
        }
        if (fraction <= BASELINE_HIGH) {
            return 1.0;
        }

        double x = clamp01((fraction - BASELINE_HIGH) / (VOLUME_FRACTION_CAP - BASELINE_HIGH));
        // Quadratic ease-out: the first extra gallons matter more than the last.
        double eased = 1.0 - (1.0 - x) * (1.0 - x);
        return 1.0 + MAX_STRETCH * eased;
    }

    private static BigDecimal tankVolumeGallons(WaterChange lastChange) {
        Tank tank = lastChange.getTank();
        return tank != null ? tank.getVolumeGallons() : null;
    }

    private static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}