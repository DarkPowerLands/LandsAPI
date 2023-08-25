
package ru.landsproject.api.configuration.abstractconfigure.objectmapping;

import com.google.common.reflect.TypeToken;

public class InvalidTypeException extends ObjectMappingException {
    public InvalidTypeException(TypeToken<?> received) {
        super("Invalid type presented to serializer: " + received);
    }
}
