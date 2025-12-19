package by.vitikova.discovery.converter;

import by.vitikova.discovery.constant.RoleName;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class RoleNameWritingConverter implements Converter<RoleName, String> {

    @Override
    public String convert(RoleName source) {
        return source.getRole();
    }
}