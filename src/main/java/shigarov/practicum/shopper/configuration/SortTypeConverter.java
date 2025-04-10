package shigarov.practicum.shopper.configuration;

import org.springframework.core.convert.converter.Converter;
import shigarov.practicum.shopper.types.SortType;

public class SortTypeConverter implements Converter<String, SortType> {
    @Override
    public SortType convert(String source) {
        return SortType.valueOf(source.toUpperCase());
    }
}

