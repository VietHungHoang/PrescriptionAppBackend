package com.vhh.PrescriptionAppBackend.repository;

import com.vhh.PrescriptionAppBackend.model.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s " +
            "JOIN s.drugInPrescription dip " +
            "JOIN dip.prescription p " +
            "WHERE p.user.id = :userId " +
            "AND FUNCTION('DATE', s.date) = :date " +
            "ORDER BY s.date ASC")
    List<Schedule> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") java.sql.Date date);


    @Query("SELECT s FROM Schedule s " +
            "JOIN s.drugInPrescription dip " +
            "JOIN dip.prescription p " +
            "WHERE p.user.id = :userId " +
            "AND s.editted = true " +
            "ORDER BY s.date DESC")
    List<Schedule> findAllByUserIdBeforeOrEqualTodayAndEdittedTrueOrderByDateDesc(@Param("userId") Long userId);



}

