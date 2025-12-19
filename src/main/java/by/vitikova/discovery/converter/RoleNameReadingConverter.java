package by.vitikova.discovery.converter;

import by.vitikova.discovery.constant.RoleName;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class RoleNameReadingConverter implements Converter<String, RoleName> {

    @Override
    public RoleName convert(String source) {
        return RoleName.getRoleName(source);
    }
}