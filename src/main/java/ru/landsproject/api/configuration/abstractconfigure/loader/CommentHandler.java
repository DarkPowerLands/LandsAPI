
package ru.landsproject.api.configuration.abstractconfigure.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public interface CommentHandler {
    Optional<String> extractHeader(BufferedReader reader) throws IOException;
    Collection<String> toComment(Collection<String> lines);
}
