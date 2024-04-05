package by.vitikova.discovery.converter;

import by.vitikova.discovery.constant.RoleName;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * Конвертер для преобразования строки в RoleName.
 */
@ReadingConverter
public class RoleNameReadingConverter implements Converter<String, RoleName> {

    @Override
    public RoleName convert(String source) {
        return RoleName.getRoleName(source);
    }
}