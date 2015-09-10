package com.unisoft.algotrader.provider.ib.api.deserializer;

import com.unisoft.algotrader.model.refdata.Instrument;
import com.unisoft.algotrader.persistence.RefDataStore;
import com.unisoft.algotrader.provider.ib.IBProvider;
import com.unisoft.algotrader.provider.ib.api.event.IBEventHandler;
import com.unisoft.algotrader.provider.ib.api.model.contract.InstrumentSpecification;
import com.unisoft.algotrader.provider.ib.api.model.contract.OptionRight;
import com.unisoft.algotrader.provider.ib.api.model.contract.SecType;
import com.unisoft.algotrader.provider.ib.api.model.system.IncomingMessageId;
import com.unisoft.algotrader.utils.collection.Tuple2;

import java.io.InputStream;

import static com.unisoft.algotrader.provider.ib.InputStreamUtils.*;
/**
 * Created by alex on 8/13/15.
 */
public class ContractSpecificationEventDeserializer extends Deserializer {

    private final RefDataStore refDataStore;

    public ContractSpecificationEventDeserializer(RefDataStore refDataStore){
        super(IncomingMessageId.CONTRACT_SPECIFICATION);
        this.refDataStore = refDataStore;
    }

    @Override
    public void consumeMessageContent(final int version, final InputStream inputStream, final IBEventHandler eventHandler) {
        final int requestId = (version >= 3) ? readInt(inputStream) : -1;
        final InstrumentSpecification instrumentSpecification = consumeInstrumentSpecification(version, inputStream, eventHandler);

        eventHandler.onInstrumentSpecificationEvent(requestId, instrumentSpecification);
    }


    private InstrumentSpecification consumeInstrumentSpecification(final int version, final InputStream inputStream, final IBEventHandler eventHandler) {
        final InstrumentSpecification instrumentSpecification = new InstrumentSpecification();
        final String symbol = readString(inputStream);
        final Instrument.InstType instType = SecType.convert(readString(inputStream));
        final String expString = readString(inputStream);
        final double strike = readDouble(inputStream);
        final Instrument.PutCall putCall = OptionRight.convert(readString(inputStream));
        final String exchange = readString(inputStream);
        final String ccyCode = readString(inputStream);
        final String localSymbol = readString(inputStream);

        instrumentSpecification.setMarketName(readString(inputStream));
        instrumentSpecification.setTradingClass(readString(inputStream));
        int intId = readInt(inputStream);
        instrumentSpecification.setMinimumFluctuation(readDouble(inputStream));
        final String multiplier  = readString(inputStream);


        instrumentSpecification.setValidOrderTypes(readString(inputStream));
        instrumentSpecification.setValidExchanges(readString(inputStream));
        if (version >= 2) {
            instrumentSpecification.setPriceMagnifier(readInt(inputStream));
        }
        if (version >= 4) {
            instrumentSpecification.setUnderlyingContractId(readInt(inputStream));
        }
        String primaryExchange = exchange;
        if (version >= 5) {
            instrumentSpecification.setLongName(readString(inputStream));
            primaryExchange = readString(inputStream);
        }

        Instrument instrument = refDataStore.getInstrumentBySymbolAndExchange(IBProvider.PROVIDER_ID, symbol, exchange);
        instrumentSpecification.setInstrument(instrument);

        if (version >= 6) {
            instrumentSpecification.setContractMonth(readString(inputStream));
            instrumentSpecification.setIndustry(readString(inputStream));
            instrumentSpecification.setCategory(readString(inputStream));
            instrumentSpecification.setSubcategory(readString(inputStream));
            instrumentSpecification.setTimeZoneId(readString(inputStream));
            instrumentSpecification.setTradingHours(readString(inputStream));
            instrumentSpecification.setLiquidHours(readString(inputStream));
        }
        if (version >= 8) {
            instrumentSpecification.setEconomicValueRule(readString(inputStream));
            instrumentSpecification.setEconomicValueMultiplier(readDouble(inputStream));
        }
        if (version >= 7) {
            final int securityIdsCount = readInt(inputStream);
            for (int i = 0; i < securityIdsCount; i++) {
                final Tuple2<String, String> pairTagValue = new Tuple2(readString(inputStream), readString(inputStream));
                instrumentSpecification.getSecurityIds().add(pairTagValue);
            }
        }
        return instrumentSpecification;
    }
}