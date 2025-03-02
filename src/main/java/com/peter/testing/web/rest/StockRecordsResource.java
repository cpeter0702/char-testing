package com.peter.testing.web.rest;

import com.peter.testing.domain.StockRecords;
import com.peter.testing.repository.StockRecordsRepository;
import com.peter.testing.service.StockRecordsService;
import com.peter.testing.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.peter.testing.domain.StockRecords}.
 */
@RestController
@RequestMapping("/api/stock-records")
public class StockRecordsResource {

    private final Logger log = LoggerFactory.getLogger(StockRecordsResource.class);

    private static final String ENTITY_NAME = "stockRecords";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockRecordsService stockRecordsService;

    private final StockRecordsRepository stockRecordsRepository;

    public StockRecordsResource(StockRecordsService stockRecordsService, StockRecordsRepository stockRecordsRepository) {
        this.stockRecordsService = stockRecordsService;
        this.stockRecordsRepository = stockRecordsRepository;
    }

    /**
     * {@code POST  /stock-records} : Create a new stockRecords.
     *
     * @param stockRecords the stockRecords to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockRecords, or with status {@code 400 (Bad Request)} if the stockRecords has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StockRecords> createStockRecords(@Valid @RequestBody StockRecords stockRecords) throws URISyntaxException {
        log.debug("REST request to save StockRecords : {}", stockRecords);
        if (stockRecords.getId() != null) {
            throw new BadRequestAlertException("A new stockRecords cannot already have an ID", ENTITY_NAME, "idexists");
        }
        stockRecords = stockRecordsService.save(stockRecords);
        return ResponseEntity.created(new URI("/api/stock-records/" + stockRecords.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, stockRecords.getId().toString()))
            .body(stockRecords);
    }

    /**
     * {@code PUT  /stock-records/:id} : Updates an existing stockRecords.
     *
     * @param id the id of the stockRecords to save.
     * @param stockRecords the stockRecords to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockRecords,
     * or with status {@code 400 (Bad Request)} if the stockRecords is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockRecords couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StockRecords> updateStockRecords(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockRecords stockRecords
    ) throws URISyntaxException {
        log.debug("REST request to update StockRecords : {}, {}", id, stockRecords);
        if (stockRecords.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockRecords.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockRecordsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        stockRecords = stockRecordsService.update(stockRecords);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockRecords.getId().toString()))
            .body(stockRecords);
    }

    /**
     * {@code PATCH  /stock-records/:id} : Partial updates given fields of an existing stockRecords, field will ignore if it is null
     *
     * @param id the id of the stockRecords to save.
     * @param stockRecords the stockRecords to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockRecords,
     * or with status {@code 400 (Bad Request)} if the stockRecords is not valid,
     * or with status {@code 404 (Not Found)} if the stockRecords is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockRecords couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockRecords> partialUpdateStockRecords(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockRecords stockRecords
    ) throws URISyntaxException {
        log.debug("REST request to partial update StockRecords partially : {}, {}", id, stockRecords);
        if (stockRecords.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockRecords.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockRecordsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockRecords> result = stockRecordsService.partialUpdate(stockRecords);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockRecords.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-records} : get all the stockRecords.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockRecords in body.
     */
    @GetMapping("")
    public List<StockRecords> getAllStockRecords() {
        log.debug("REST request to get all StockRecords");
        return stockRecordsService.findAll();
    }

    /**
     * {@code GET  /stock-records/:id} : get the "id" stockRecords.
     *
     * @param id the id of the stockRecords to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockRecords, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockRecords> getStockRecords(@PathVariable("id") Long id) {
        log.debug("REST request to get StockRecords : {}", id);
        Optional<StockRecords> stockRecords = stockRecordsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockRecords);
    }

    /**
     * {@code DELETE  /stock-records/:id} : delete the "id" stockRecords.
     *
     * @param id the id of the stockRecords to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockRecords(@PathVariable("id") Long id) {
        log.debug("REST request to delete StockRecords : {}", id);
        stockRecordsService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
