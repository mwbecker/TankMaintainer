package com.michaelbecker.tankmaintainer.service;

import com.michaelbecker.tankmaintainer.model.Tank;
import com.michaelbecker.tankmaintainer.model.WaterChange;
import com.michaelbecker.tankmaintainer.repository.WaterChangeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WaterChangeServiceTest {

    @Mock
    private WaterChangeRepository waterChangeRepository;

    private WaterChangeService service;

    @BeforeEach
    void setUp() {
        service = new WaterChangeService(waterChangeRepository);
    }

    @Test
    void typicalFifteenToTwentyPercentDoesNotChangeInterval() {
        assertEquals(7, WaterChangeService.adjustIntervalForLastChangeVolume(
                7, gallons(15), gallons(100)));
        assertEquals(7, WaterChangeService.adjustIntervalForLastChangeVolume(
                7, gallons(20), gallons(100)));
        assertEquals(1.0, WaterChangeService.volumeMultiplier(0.15), 1e-9);
        assertEquals(1.0, WaterChangeService.volumeMultiplier(0.175), 1e-9);
        assertEquals(1.0, WaterChangeService.volumeMultiplier(0.20), 1e-9);
    }

    @Test
    void largerChangeStretchesIntervalWithDiminishingReturnsAndCap() {
        double atThirty = WaterChangeService.volumeMultiplier(0.30);
        double atForty = WaterChangeService.volumeMultiplier(0.40);
        double atFifty = WaterChangeService.volumeMultiplier(0.50);
        double atEighty = WaterChangeService.volumeMultiplier(0.80);

        assertEquals(1.50, atFifty, 1e-9);
        assertEquals(atFifty, atEighty, 1e-9);
        // First extra gallons buy more time than the last extra gallons.
        assertTrue(atThirty - 1.0 > atFifty - atForty);
        assertEquals(11, WaterChangeService.adjustIntervalForLastChangeVolume(
                7, gallons(50), gallons(100)));
    }

    @Test
    void smallerChangePullsIntervalIn() {
        assertTrue(WaterChangeService.volumeMultiplier(0.05) < 1.0);
        assertEquals(6, WaterChangeService.adjustIntervalForLastChangeVolume(
                7, gallons(5), gallons(100)));
    }

    @Test
    void missingVolumeLeavesAverageIntervalUnchanged() {
        assertEquals(7, WaterChangeService.adjustIntervalForLastChangeVolume(
                7, null, gallons(100)));
        assertEquals(7, WaterChangeService.adjustIntervalForLastChangeVolume(
                7, gallons(20), null));
        assertEquals(7, WaterChangeService.adjustIntervalForLastChangeVolume(
                7, gallons(0), gallons(100)));
    }

    @Test
    void predictNextMaintenanceUsesLastChangeVolume() {
        UUID tankId = UUID.randomUUID();
        Tank tank = new Tank();
        tank.setId(tankId);
        tank.setVolumeGallons(gallons(40));

        WaterChange first = waterChange(tank, LocalDate.now().minusDays(14), gallons(8));
        WaterChange last = waterChange(tank, LocalDate.now().minusDays(7), gallons(20));
        when(waterChangeRepository.findByTankId(tankId)).thenReturn(List.of(first, last));

        LocalDate predicted = service.predictNextMaintenance(tankId);

        // Average interval is 7 days; 50% last change stretches to 11 days.
        assertEquals(LocalDate.now().plusDays(4), predicted);
    }

    private static BigDecimal gallons(double value) {
        return BigDecimal.valueOf(value);
    }

    private static WaterChange waterChange(Tank tank, LocalDate date, BigDecimal gallons) {
        WaterChange change = new WaterChange();
        change.setTank(tank);
        change.setDate(OffsetDateTime.of(date.atStartOfDay(), ZoneOffset.UTC));
        change.setVolumeGallons(gallons);
        return change;
    }
}
