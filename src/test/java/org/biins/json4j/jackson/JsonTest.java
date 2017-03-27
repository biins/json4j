package org.biins.json4j.jackson;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * @author Martin Janys
 */
public class JsonTest {

    @SuppressWarnings("unused")
    private static final Json INT_FIELD = new Json() {
        int a = 1;
    };

    @SuppressWarnings("unused")
    private static final Json STRING_FIELD = new Json() {
        String a = "1";
    };

    @SuppressWarnings("unused")
    private static final Json NESTED_STRING_FIELD = new Json() {
        Json a = new Json() {
            Json b = new Json() {
                String c = "1";
            };
        };
    };

    @Test
    public void testNonExistingField() {
        assertThat(INT_FIELD.get("x"), is(nullValue()));
    }

    @Test
    public void testGetIntField() {
        assertThat(INT_FIELD.get("a"), is(1));
    }

    @Test(expected = ClassCastException.class)
    public void testGetIntFieldAsString() {
        @SuppressWarnings("unused")
        String a = INT_FIELD.get("a");
    }

    @Test
    public void testGetStringField() {
        assertThat(STRING_FIELD.get("a"), is("1"));
    }

    @Test(expected = ClassCastException.class)
    public void testGetStringFieldAsInt() {
        @SuppressWarnings("unused")
        int i = STRING_FIELD.get("a");
    }

    @Test
    public void testGetByPathNestedString() {
        assertThat(NESTED_STRING_FIELD.get("a.b.c"), is("1"));
    }

    @Test
    public void testGetByPathString() {
        assertThat(STRING_FIELD.get("a"), is("1"));
    }

    @Test
    public void testGetByPathNestedStringWithChaining() {
        assertThat(NESTED_STRING_FIELD.getJson("a").getJson("b").get("c"), is("1"));
    }

    @Test
    public void testGetByPathNestedStringWithVariables() {
        Json a = NESTED_STRING_FIELD.get("a");
        Json b = a.get("b");
        assertThat(b.get("c"), is("1"));
    }

    @Test
    public void testGetNestedStringWithVariables() {
        assertThat(STRING_FIELD.get("a"), is("1"));
    }

    @Test
    public void testToString() {
        String jsonString = NESTED_STRING_FIELD.asString();

        assertThat(jsonString, is("{\"a\":{\"b\":{\"c\":\"1\"}}}"));
    }

    @Test
    public void testDeserialize() {
        String jsonString = "{" +
                "\"a\": 1," +
                "\"b\": \"2\"," +
                "\"c\": { \"d\": 1 }" +
                "}";
        Json json = new Json() {
            int a;
            String b;
            Json c;
        }.load(jsonString);

        assertThat(json.get("a"), is(1));
        assertThat(json.get("b"), is("2"));
        assertThat(json.get("c"), is(notNullValue()));
        assertThat(json.get("c.d"), is(1));
    }

    @Test
    public void testJson() {
        Json json = new Json() {
            String name = "json4j";
            Json metadata = new Json() {
                String version = "1.0.0";
                int year = 2017;
            };
        };

        assertThat(json.get("name"), is("json4j"));
        assertThat(json.get("metadata.version"), is("1.0.0"));
        assertThat(json.get("metadata.year"), is(2017));

        String jsonString = json.toString();
        json.clear();
        json = json.load(jsonString);
        assertThat(json.get("name"), is("json4j"));
        assertThat(json.get("metadata.version"), is("1.0.0"));
        assertThat(json.get("metadata.year"), is(2017));
    }
}