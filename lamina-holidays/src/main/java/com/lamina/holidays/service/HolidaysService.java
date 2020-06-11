package com.lamina.holidays.service;

import com.lamina.holidays.entity.Holiday;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HolidaysService {
    Set<Holiday> holidays = new HashSet<>();

    HolidaysService() {
        holidays.add(new Holiday(1, "New Year", "New year holiday", LocalDate.of(2019, Month.APRIL, 20), LocalDate.of(2019, Month.APRIL, 30)));
        holidays.add(new Holiday(2, "Three Kings", "New year holiday", LocalDate.of(2019, Month.APRIL, 20), LocalDate.of(2019, Month.JULY, 30)));
        holidays.add(new Holiday(3, "Fasching", "New year holiday", LocalDate.of(2019, Month.APRIL, 20), LocalDate.of(2019, Month.JANUARY, 30)));
        holidays.add(new Holiday(4, "Good Friday", "New year holiday", LocalDate.of(2019, Month.APRIL, 20), LocalDate.of(2019, Month.MARCH, 30)));
        holidays.add(new Holiday(5, "Easter Monday", "New year holiday", LocalDate.of(2019, Month.APRIL, 20), LocalDate.of(2019, Month.APRIL, 30)));
        holidays.add(new Holiday(6, "Labour Day", "New year holiday", LocalDate.of(2019, Month.APRIL, 20), LocalDate.of(2019, Month.DECEMBER, 30)));
        holidays.add(new Holiday(7, "Labour Day", "New year holiday", LocalDate.of(2017, Month.APRIL, 20), LocalDate.of(2017, Month.DECEMBER, 30)));
        holidays.add(new Holiday(8, "Labour Day", "New year holiday", LocalDate.of(2018, Month.APRIL, 20), LocalDate.of(2018, Month.DECEMBER, 30)));
        holidays.add(new Holiday(9, "Labour Day", "New year holiday", LocalDate.of(2020, Month.APRIL, 20), LocalDate.of(2020, Month.DECEMBER, 30)));
    }

    public Set<Holiday> getHolidays() {
        return holidays;
    }

    public Holiday get(int id) {
        return holidays.stream().filter(h -> h.getId() == id).findFirst().orElse(null);
    }

    public Set<Holiday> get(String name) {
        String n = name.toLowerCase();
        return holidays.stream().filter(h -> h.getName().toLowerCase()
                .contains(n)).collect(Collectors.toSet());
    }

    public Set<Holiday> getByYear(int year) {
        return holidays.stream().filter(h -> h.getStartDate().getYear() == year)
                .collect(Collectors.toSet());
    }

    public Holiday create(Holiday holiday) {
        Comparator<Holiday> comparator = Comparator.comparing(Holiday::getId);
        Optional<Holiday> maxHoliday = holidays.stream().max(comparator);
        holiday.setId(maxHoliday.map(value -> value.getId() + 1).orElse(1));
        holidays.add(holiday);
        return holiday;
    }

    public Holiday update(Holiday holiday) {
        Optional<Holiday> optional = holidays.stream().filter(h -> holiday.getId() == h.getId()).findFirst().map(h -> {
            h.setDescription(holiday.getDescription());
            h.setName(holiday.getName());
            h.setEndDate(holiday.getEndDate());
            h.setStartDate(holiday.getStartDate());
            return h;
        });
        return optional.orElse(null);
    }

    public Holiday delete(int id) throws Exception {
        Optional<Holiday> optional = holidays.stream().filter(h->h.getId() == id).findFirst();
        if(optional.isPresent()) {
            System.out.println(holidays.size());
            holidays.remove(optional.get());
            System.out.println(holidays.size());
        } else {
            throw new Exception("No Holiday found with id: "+id);
        }
        return optional.orElse(null);
    }
}
