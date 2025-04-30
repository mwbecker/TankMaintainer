package com.michaelbecker.tankmaintainer.config;

import com.michaelbecker.tankmaintainer.model.*;
import com.michaelbecker.tankmaintainer.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner loadData(
            TankRepository tankRepo,
            TankParamRepository paramRepo,
            WaterChangeRepository waterRepo,
            FeedingRepository feedingRepo
    ) {
        return args -> {
            // Prevent duplicate tank
            String tankName = "Neo Tank";
            if (tankRepo.findByName(tankName).isPresent()) {
                System.out.println("🚫 Sample data already exists. Skipping load.");
                return;
            }

            // Create a sample tank
            Tank tank = new Tank();
            tank.setName(tankName);
            tank.setSpecies("Neocaridina davidi");
            tank.setVolumeGallons(new BigDecimal("10"));
            tank.setNotes("Planted tank with Java moss");
            tank = tankRepo.save(tank);

            // Add a pH reading
            TankParam ph = new TankParam();
            ph.setTank(tank);
            ph.setTimestamp(LocalDateTime.now());
            ph.setParamType(ParamType.PH);
            ph.setValue(new BigDecimal("6.8"));
            ph.setUnit("pH");
            ph.setNotes("Stable pH");
            paramRepo.save(ph);

            // Add a KH reading
            TankParam kh = new TankParam();
            kh.setTank(tank);
            kh.setTimestamp(LocalDateTime.now());
            kh.setParamType(ParamType.KH);
            kh.setValue(new BigDecimal("3"));
            kh.setUnit("°dKH");
            kh.setNotes("Low KH, expected");
            paramRepo.save(kh);

            // Add a water change
            WaterChange wc = new WaterChange();
            wc.setTank(tank);
            wc.setDate(LocalDateTime.now().minusDays(2));
            wc.setVolumeGallons(new BigDecimal("2"));
            wc.setNotes("Added Prime");
            waterRepo.save(wc);

            // Add a feeding
            Feeding feed = new Feeding();
            feed.setTank(tank);
            feed.setTimestamp(LocalDateTime.now().minusHours(3));
            feed.setFoodType("BacterAE");
            feed.setAmount("1/16 tsp");
            feed.setNotes("Light feeding");
            feedingRepo.save(feed);

            System.out.println("✅ Sample data loaded.");
        };
    }
}