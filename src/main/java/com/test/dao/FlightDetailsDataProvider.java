package com.test.dao;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.test.dao.entity.FlightDetails;

public interface FlightDetailsDataProvider extends JpaRepository<FlightDetails, Long> {

	@Cacheable(value = "CacheName")
	@Query(value = "select t.* from tb_olci_exlr_fee t where " + "t.EXLR_AIRLINE_CODE =:airlineCode AND " + "t.EXLR_ORIGIN = :origin AND " + "t.EXLR_DEPARTURE = :departure " + "ORDER BY EXLR_LAST_UPDATE_TIMESTAMP", nativeQuery = true)
	@Modifying
	public List<FlightDetails> findFlightDetails(@Param("airlineCode") String airlineCode, @Param("origin") String origin, @Param("departure") String departure);
}
