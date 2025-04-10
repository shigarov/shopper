package shigarov.practicum.shopper.configuration;

import org.springframework.core.convert.converter.Converter;
import shigarov.practicum.shopper.types.ActionType;

public class ActionTypeConverter implements Converter<String, ActionType> {
    @Override
    public ActionType convert(String source) {
        return ActionType.valueOf(source.toUpperCase());
    }
}

