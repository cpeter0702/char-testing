package com.peter.testing.repository;

import com.peter.testing.domain.StockRecords;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockRecords entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockRecordsRepository extends JpaRepository<StockRecords, Long> {}
