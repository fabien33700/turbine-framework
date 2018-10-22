package io.turbine.core.utils;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static io.turbine.core.utils.GeneralUtils.format;

class GeneralUtilsTest {

    @Test
    void testFormat() {
        assertThat(format("I'm {}, and I'm {} years old.", "Fabien", 16), is("I'm Fabien, and I'm 16 years old."));
        assertThat(format("Hello world"), is("Hello world"));
        assertThat(format("Hello {}, how are you ?", "David", 3.14159f), is("Hello David, how are you ?"));
        assertThat(format("Goodbye {}"), is("Goodbye {}"));
        assertThat(format("{}"), is ("{}"));
        assertThat(format("{}", 3.14159f), is("3.14159"));
    }
}