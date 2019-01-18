package io.turbine.core.utils;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static io.turbine.core.utils.Utils.Strings.format;
import static io.turbine.core.utils.Utils.Web.parseQueryString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

class UtilsTest {

    private final static Logger logger = LoggerFactory.getLogger(UtilsTest.class);

    @Test
    void testFormat() {
        assertThat(format("I'm {}, and I'm {} years old.", "Fabien", 16), is("I'm Fabien, and I'm 16 years old."));
        assertThat(format("Hello world"), is("Hello world"));
        assertThat(format("Hello {}, how are you ?", "David", 3.14159f), is("Hello David, how are you ?"));
        assertThat(format("Goodbye {}"), is("Goodbye {}"));
        assertThat(format("{}"), is ("{}"));
        assertThat(format("{}", 3.14159f), is("3.14159"));
    }


    @Test
    void testWebParseQueryString() {
        Map<String, Object> params;

        params = parseQueryString("name=Steven&age=35");
        logger.info("Testing : Standard QS with two params");
        assertThat("there must be 2 params", params.size(), is(2));
        assertThat("must contains 'name' and 'age' keys",
                params.keySet(), containsInAnyOrder("name", "age"));
        assertThat("param 'name' value must be 'Steven'", params.get("name"), is("Steven"));
        assertThat("param 'age' value must be 35", params.get("age"), is(35));
    }
}