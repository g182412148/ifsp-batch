package com.scrcu.ebank.ebap.batch.common.converter;

import com.scrcu.ebank.ebap.dubbo.msgconvert.IMsgFieldConverter;
import org.springframework.stereotype.Component;

@Component
public class OriginalRespCode implements IMsgFieldConverter {
    @Override
    public String getName() {
        return "originalRespCode";
    }

    @Override
    public Object handle(Object o) throws IllegalArgumentException {
        return o;
    }
}
