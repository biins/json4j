# json4j

## Serialization

```
@SuppressWarnings("unused")
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
json.toString(); // {"name":"json4j","metadata":{"year":2017,"version":"1.0.0"}}
```

## Deserialization
```
String jsonString; // {"name":"json4j","metadata":{"year":2017,"version":"1.0.0"}}
Json json = new Json() {
    String name;
    Json metadata;
}.load(jsonString);

assertThat(json.get("name"), is("json4j"));
assertThat(json.get("metadata.version"), is("1.0.0"));
assertThat(json.get("metadata.year"), is(2017));
```