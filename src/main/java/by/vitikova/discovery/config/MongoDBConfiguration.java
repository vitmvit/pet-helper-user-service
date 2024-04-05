package by.vitikova.discovery.config;

import by.vitikova.discovery.converter.RoleNameReadingConverter;
import by.vitikova.discovery.converter.RoleNameWritingConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;

/**
 * Конфигурация для настройки конвертеров при сохранении и чтении данных из MongoDB.
 *
 * @return объект MongoCustomConversions, содержащий список конвертеров
 */
@Configuration
public class MongoDBConfiguration {

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<>();
        converterList.add(new RoleNameReadingConverter());
        converterList.add(new RoleNameWritingConverter());
        return new MongoCustomConversions(converterList);
    }
}
