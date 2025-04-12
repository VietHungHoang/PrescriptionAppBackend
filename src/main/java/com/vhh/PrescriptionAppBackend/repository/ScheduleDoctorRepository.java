package com.vhh.PrescriptionAppBackend.repository;

import com.vhh.PrescriptionAppBackend.model.entity.ScheduleDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleDoctorRepository extends JpaRepository<ScheduleDoctor, Long> {
    @Query("SELECT s FROM ScheduleDoctor s WHERE s.startDate >= :startDate AND s.endDate <= :endDate")
    List<ScheduleDoctor> findByDateRange(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
}
