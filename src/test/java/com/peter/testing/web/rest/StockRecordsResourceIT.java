package com.peter.testing.web.rest;

import static com.peter.testing.domain.StockRecordsAsserts.*;
import static com.peter.testing.web.rest.TestUtil.createUpdateProxyForBean;
import static com.peter.testing.web.rest.TestUtil.sameInstant;
import static com.peter.testing.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peter.testing.IntegrationTest;
import com.peter.testing.domain.StockRecords;
import com.peter.testing.repository.StockRecordsRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StockRecordsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StockRecordsResourceIT {

    private static final Long DEFAULT_STOCK_ID = 1L;
    private static final Long UPDATED_STOCK_ID = 2L;

    private static final String DEFAULT_STICKER = "AAAAAAAAAA";
    private static final String UPDATED_STICKER = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_BUSINESS_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_BUSINESS_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final BigDecimal DEFAULT_STOCK_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_STOCK_PRICE = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/stock-records";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StockRecordsRepository stockRecordsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockRecordsMockMvc;

    private StockRecords stockRecords;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockRecords createEntity(EntityManager em) {
        StockRecords stockRecords = new StockRecords()
            .stockId(DEFAULT_STOCK_ID)
            .sticker(DEFAULT_STICKER)
            .businessDate(DEFAULT_BUSINESS_DATE)
            .stockPrice(DEFAULT_STOCK_PRICE);
        return stockRecords;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockRecords createUpdatedEntity(EntityManager em) {
        StockRecords stockRecords = new StockRecords()
            .stockId(UPDATED_STOCK_ID)
            .sticker(UPDATED_STICKER)
            .businessDate(UPDATED_BUSINESS_DATE)
            .stockPrice(UPDATED_STOCK_PRICE);
        return stockRecords;
    }

    @BeforeEach
    public void initTest() {
        stockRecords = createEntity(em);
    }

    @Test
    @Transactional
    void createStockRecords() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StockRecords
        var returnedStockRecords = om.readValue(
            restStockRecordsMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockRecords)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StockRecords.class
        );

        // Validate the StockRecords in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertStockRecordsUpdatableFieldsEquals(returnedStockRecords, getPersistedStockRecords(returnedStockRecords));
    }

    @Test
    @Transactional
    void createStockRecordsWithExistingId() throws Exception {
        // Create the StockRecords with an existing ID
        stockRecords.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockRecordsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockRecords)))
            .andExpect(status().isBadRequest());

        // Validate the StockRecords in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStockIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockRecords.setStockId(null);

        // Create the StockRecords, which fails.

        restStockRecordsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockRecords)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStockPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockRecords.setStockPrice(null);

        // Create the StockRecords, which fails.

        restStockRecordsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockRecords)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStockRecords() throws Exception {
        // Initialize the database
        stockRecordsRepository.saveAndFlush(stockRecords);

        // Get all the stockRecordsList
        restStockRecordsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockRecords.getId().intValue())))
            .andExpect(jsonPath("$.[*].stockId").value(hasItem(DEFAULT_STOCK_ID.intValue())))
            .andExpect(jsonPath("$.[*].sticker").value(hasItem(DEFAULT_STICKER)))
            .andExpect(jsonPath("$.[*].businessDate").value(hasItem(sameInstant(DEFAULT_BUSINESS_DATE))))
            .andExpect(jsonPath("$.[*].stockPrice").value(hasItem(sameNumber(DEFAULT_STOCK_PRICE))));
    }

    @Test
    @Transactional
    void getStockRecords() throws Exception {
        // Initialize the database
        stockRecordsRepository.saveAndFlush(stockRecords);

        // Get the stockRecords
        restStockRecordsMockMvc
            .perform(get(ENTITY_API_URL_ID, stockRecords.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockRecords.getId().intValue()))
            .andExpect(jsonPath("$.stockId").value(DEFAULT_STOCK_ID.intValue()))
            .andExpect(jsonPath("$.sticker").value(DEFAULT_STICKER))
            .andExpect(jsonPath("$.businessDate").value(sameInstant(DEFAULT_BUSINESS_DATE)))
            .andExpect(jsonPath("$.stockPrice").value(sameNumber(DEFAULT_STOCK_PRICE)));
    }

    @Test
    @Transactional
    void getNonExistingStockRecords() throws Exception {
        // Get the stockRecords
        restStockRecordsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockRecords() throws Exception {
        // Initialize the database
        stockRecordsRepository.saveAndFlush(stockRecords);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockRecords
        StockRecords updatedStockRecords = stockRecordsRepository.findById(stockRecords.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStockRecords are not directly saved in db
        em.detach(updatedStockRecords);
        updatedStockRecords
            .stockId(UPDATED_STOCK_ID)
            .sticker(UPDATED_STICKER)
            .businessDate(UPDATED_BUSINESS_DATE)
            .stockPrice(UPDATED_STOCK_PRICE);

        restStockRecordsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStockRecords.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedStockRecords))
            )
            .andExpect(status().isOk());

        // Validate the StockRecords in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStockRecordsToMatchAllProperties(updatedStockRecords);
    }

    @Test
    @Transactional
    void putNonExistingStockRecords() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockRecords.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockRecordsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockRecords.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockRecords))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockRecords in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockRecords() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockRecords.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockRecordsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockRecords))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockRecords in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockRecords() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockRecords.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockRecordsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockRecords)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockRecords in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockRecordsWithPatch() throws Exception {
        // Initialize the database
        stockRecordsRepository.saveAndFlush(stockRecords);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockRecords using partial update
        StockRecords partialUpdatedStockRecords = new StockRecords();
        partialUpdatedStockRecords.setId(stockRecords.getId());

        partialUpdatedStockRecords.sticker(UPDATED_STICKER);

        restStockRecordsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockRecords.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockRecords))
            )
            .andExpect(status().isOk());

        // Validate the StockRecords in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockRecordsUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStockRecords, stockRecords),
            getPersistedStockRecords(stockRecords)
        );
    }

    @Test
    @Transactional
    void fullUpdateStockRecordsWithPatch() throws Exception {
        // Initialize the database
        stockRecordsRepository.saveAndFlush(stockRecords);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockRecords using partial update
        StockRecords partialUpdatedStockRecords = new StockRecords();
        partialUpdatedStockRecords.setId(stockRecords.getId());

        partialUpdatedStockRecords
            .stockId(UPDATED_STOCK_ID)
            .sticker(UPDATED_STICKER)
            .businessDate(UPDATED_BUSINESS_DATE)
            .stockPrice(UPDATED_STOCK_PRICE);

        restStockRecordsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockRecords.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockRecords))
            )
            .andExpect(status().isOk());

        // Validate the StockRecords in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockRecordsUpdatableFieldsEquals(partialUpdatedStockRecords, getPersistedStockRecords(partialUpdatedStockRecords));
    }

    @Test
    @Transactional
    void patchNonExistingStockRecords() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockRecords.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockRecordsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockRecords.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockRecords))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockRecords in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockRecords() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockRecords.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockRecordsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockRecords))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockRecords in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockRecords() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockRecords.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockRecordsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stockRecords)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockRecords in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStockRecords() throws Exception {
        // Initialize the database
        stockRecordsRepository.saveAndFlush(stockRecords);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the stockRecords
        restStockRecordsMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockRecords.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return stockRecordsRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected StockRecords getPersistedStockRecords(StockRecords stockRecords) {
        return stockRecordsRepository.findById(stockRecords.getId()).orElseThrow();
    }

    protected void assertPersistedStockRecordsToMatchAllProperties(StockRecords expectedStockRecords) {
        assertStockRecordsAllPropertiesEquals(expectedStockRecords, getPersistedStockRecords(expectedStockRecords));
    }

    protected void assertPersistedStockRecordsToMatchUpdatableProperties(StockRecords expectedStockRecords) {
        assertStockRecordsAllUpdatablePropertiesEquals(expectedStockRecords, getPersistedStockRecords(expectedStockRecords));
    }
}
