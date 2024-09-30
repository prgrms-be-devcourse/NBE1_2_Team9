package com.grepp.nbe1_2_team09.domain.repository.finance;

import com.grepp.nbe1_2_team09.domain.entity.ExchangeRate;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query("SELECT conversionRate FROM ExchangeRate WHERE fromCountry =:fromCounty AND toCountry =:toCountry")
    BigDecimal findConversionRate (String toCountry, String fromCountry);
}
