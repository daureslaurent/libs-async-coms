package lda.services.libs.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassResolverTest {

    @Test
    void fromName_OK() {
        final var nameExpected = "lda.services.libs.utils.ClassResolverTest";
        assertEquals(ClassResolver.fromName(nameExpected).getName(), nameExpected);
    }

    @Test
    void fromName_OK_Rewrite() {
        final var nameExpected = "lda.services.libs.utils.ClassResolverTest";
        final var nameRewrite = "lda.services.libs.utils.$ClassResolverTest$toto$tutu";
        assertEquals(ClassResolver.fromName(nameRewrite).getName(), nameExpected);
    }
}