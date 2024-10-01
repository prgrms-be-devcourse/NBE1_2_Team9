package com.grepp.nbe1_2_team09.domain.repository.finance;

import com.grepp.nbe1_2_team09.domain.entity.ExchangeRate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query("SELECT conversionRate FROM ExchangeRate WHERE fromCountry =:fromCountry AND toCountry =:toCountry")
    BigDecimal findConversionRate (String toCountry, String fromCountry);

    @Query("SELECT exchangeRateId FROM ExchangeRate WHERE fromCountry =:fromCountry AND toCountry =:toCountry")
    Long findIdByFromCountryAndToCountry(String fromCountry, String toCountry);
}
