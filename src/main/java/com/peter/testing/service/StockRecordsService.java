package com.peter.testing.service;

import com.peter.testing.domain.StockRecords;
import com.peter.testing.repository.StockRecordsRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.peter.testing.domain.StockRecords}.
 */
@Service
@Transactional
public class StockRecordsService {

    private final Logger log = LoggerFactory.getLogger(StockRecordsService.class);

    private final StockRecordsRepository stockRecordsRepository;

    public StockRecordsService(StockRecordsRepository stockRecordsRepository) {
        this.stockRecordsRepository = stockRecordsRepository;
    }

    /**
     * Save a stockRecords.
     *
     * @param stockRecords the entity to save.
     * @return the persisted entity.
     */
    public StockRecords save(StockRecords stockRecords) {
        log.debug("Request to save StockRecords : {}", stockRecords);
        return stockRecordsRepository.save(stockRecords);
    }

    /**
     * Update a stockRecords.
     *
     * @param stockRecords the entity to save.
     * @return the persisted entity.
     */
    public StockRecords update(StockRecords stockRecords) {
        log.debug("Request to update StockRecords : {}", stockRecords);
        return stockRecordsRepository.save(stockRecords);
    }

    /**
     * Partially update a stockRecords.
     *
     * @param stockRecords the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StockRecords> partialUpdate(StockRecords stockRecords) {
        log.debug("Request to partially update StockRecords : {}", stockRecords);

        return stockRecordsRepository
            .findById(stockRecords.getId())
            .map(existingStockRecords -> {
                if (stockRecords.getSticker() != null) {
                    existingStockRecords.setSticker(stockRecords.getSticker());
                }
                if (stockRecords.getBusinessDate() != null) {
                    existingStockRecords.setBusinessDate(stockRecords.getBusinessDate());
                }
                if (stockRecords.getStockPrice() != null) {
                    existingStockRecords.setStockPrice(stockRecords.getStockPrice());
                }

                return existingStockRecords;
            })
            .map(stockRecordsRepository::save);
    }

    /**
     * Get all the stockRecords.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StockRecords> findAll() {
        log.debug("Request to get all StockRecords");
        return stockRecordsRepository.findAll();
    }

    /**
     * Get one stockRecords by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockRecords> findOne(Long id) {
        log.debug("Request to get StockRecords : {}", id);
        return stockRecordsRepository.findById(id);
    }

    /**
     * Delete the stockRecords by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete StockRecords : {}", id);
        stockRecordsRepository.deleteById(id);
    }
}
