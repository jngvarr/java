package dao.converters;

import dao.entities.Unit;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter(autoApply = true)
public class UnitsConverter implements AttributeConverter<Unit, String> {

    @Override
    public String convertToDatabaseColumn(Unit attribute) {
        return attribute.getName();
    }

    @Override
    public Unit convertToEntityAttribute(String dbData) {
        return Arrays.stream(Unit.values())
                .filter(unit -> unit.getName().equals(dbData))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("illegal DB value: " + dbData));
    }
}
