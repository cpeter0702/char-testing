package com.peter.testing.domain;

import static com.peter.testing.domain.StockRecordsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.peter.testing.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockRecordsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockRecords.class);
        StockRecords stockRecords1 = getStockRecordsSample1();
        StockRecords stockRecords2 = new StockRecords();
        assertThat(stockRecords1).isNotEqualTo(stockRecords2);

        stockRecords2.setId(stockRecords1.getId());
        assertThat(stockRecords1).isEqualTo(stockRecords2);

        stockRecords2 = getStockRecordsSample2();
        assertThat(stockRecords1).isNotEqualTo(stockRecords2);
    }
}
