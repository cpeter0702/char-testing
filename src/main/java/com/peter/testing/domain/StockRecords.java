package com.peter.testing.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A StockRecords.
 */
@Entity
@Table(name = "stock_records")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockRecords implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "sticker")
    private String sticker;

    @Column(name = "business_date")
    private ZonedDateTime businessDate;

    @NotNull
    @Column(name = "stock_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal stockPrice;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockRecords id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSticker() {
        return this.sticker;
    }

    public StockRecords sticker(String sticker) {
        this.setSticker(sticker);
        return this;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }

    public ZonedDateTime getBusinessDate() {
        return this.businessDate;
    }

    public StockRecords businessDate(ZonedDateTime businessDate) {
        this.setBusinessDate(businessDate);
        return this;
    }

    public void setBusinessDate(ZonedDateTime businessDate) {
        this.businessDate = businessDate;
    }

    public BigDecimal getStockPrice() {
        return this.stockPrice;
    }

    public StockRecords stockPrice(BigDecimal stockPrice) {
        this.setStockPrice(stockPrice);
        return this;
    }

    public void setStockPrice(BigDecimal stockPrice) {
        this.stockPrice = stockPrice;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockRecords)) {
            return false;
        }
        return getId() != null && getId().equals(((StockRecords) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockRecords{" +
            "id=" + getId() +
            ", sticker='" + getSticker() + "'" +
            ", businessDate='" + getBusinessDate() + "'" +
            ", stockPrice=" + getStockPrice() +
            "}";
    }
}
