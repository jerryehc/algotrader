package com.unisoft.algotrader.persistence.cassandra.accessor;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;
import com.google.common.util.concurrent.ListenableFuture;
import com.unisoft.algotrader.model.refdata.Instrument;

/**
 * Created by alex on 7/7/15.
 */

@Accessor
public interface InstrumentAccessor {
    @Query("SELECT * FROM refdata.instruments WHERE inst_id = :inst_id")
    Instrument get(@Param("inst_id") long inst_id);

    @Query("SELECT * FROM refdata.instruments")
    Result<Instrument> getAll();

    @Query("SELECT * FROM refdata.instruments")
    ListenableFuture<Result<Instrument>> getAllAsync();


    @Query("SELECT * FROM refdata.instruments WHERE symbol = :symbol AND exch_id = :exch_id ALLOW FILTERING")
    Result<Instrument> getBySymbolAndExchange(@Param("symbol") String symbol, @Param("exch_id") String exchId );
}