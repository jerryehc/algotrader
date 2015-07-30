package com.unisoft.algotrader.provider.data;

import com.google.common.collect.Lists;
import com.unisoft.algotrader.model.event.EventBus;
import com.unisoft.algotrader.model.event.data.MarketDataContainer;
import com.unisoft.algotrader.provider.ProviderManager;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by alex on 5/19/15.
 */
public class DummyDataProvider extends AbstractHistoricalDataProvider {

    private final static long DAY_TO_MS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
    private final static int DAILY_SIZE = 60 * 60 * 24;

    public static SimpleDateFormat FORMAT2 = new SimpleDateFormat("yyyyMMdd");

    public static final String PROVIDER_ID = "Dummy";
    private final EventBus.MarketDataEventBus marketDataEventBus;

    public DummyDataProvider(ProviderManager providerManager, EventBus.MarketDataEventBus marketDataEventBus){
        super(providerManager);
        this.marketDataEventBus = marketDataEventBus;
    }

    @Override
    public boolean subscribeHistoricalData(HistoricalSubscriptionKey subscriptionKey) {
//        CsvParserSettings settings = new CsvParserSettings();
//
//        settings.getFormat().setLineSeparator("\n");
//        settings.getFormat().setDelimiter(',');
//        settings.setHeaderExtractionEnabled(true);

        long dateTime = subscriptionKey.fromDate;
        long toDateTime = subscriptionKey.toDate;

        int count = 0;
        while (dateTime < toDateTime) {

            marketDataEventBus.publishBar(subscriptionKey.instId, subscriptionKey.barSize, dateTime,
                    900 + count,
                    1000 + count,
                    800 + count,
                    950 + count,
                    0, 0);

            dateTime += DAY_TO_MS;
            count++;
        }

        return true;
    }

    @Override
    public List<MarketDataContainer> loadHistoricalData(HistoricalSubscriptionKey subscriptionKey) {
        List<MarketDataContainer> list = Lists.newArrayList();
        long dateTime = subscriptionKey.fromDate;
        long toDateTime = subscriptionKey.toDate;

        int count = 0;
        while (dateTime < toDateTime) {
            MarketDataContainer container = new MarketDataContainer();

            container.setBar(subscriptionKey.instId, subscriptionKey.barSize, dateTime,
                    900 + count,
                    1000 + count,
                    800 + count,
                    950 + count,
                    0, 0);
            list.add(container);

            dateTime += DAY_TO_MS;
            count++;
        }
        return list;
    }

    @Override
    public String providerId() {
        return PROVIDER_ID;
    }

    @Override
    public boolean connected(){
        return true;
    }

}