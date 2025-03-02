package com.peter.testing.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StockRecordsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StockRecords getStockRecordsSample1() {
        return new StockRecords().id(1L).stockId(1L).sticker("sticker1");
    }

    public static StockRecords getStockRecordsSample2() {
        return new StockRecords().id(2L).stockId(2L).sticker("sticker2");
    }

    public static StockRecords getStockRecordsRandomSampleGenerator() {
        return new StockRecords()
            .id(longCount.incrementAndGet())
            .stockId(longCount.incrementAndGet())
            .sticker(UUID.randomUUID().toString());
    }
}
