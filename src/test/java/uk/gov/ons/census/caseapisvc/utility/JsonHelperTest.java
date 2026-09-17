package uk.gov.ons.census.caseapisvc.utility;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.ons.census.caseapisvc.utility.Constants.ALLOWED_INBOUND_EVENT_SCHEMA_VERSIONS;
import static uk.gov.ons.census.caseapisvc.utility.Constants.EVENT_SCHEMA_VERSION;

import org.junit.jupiter.api.Test;
import uk.gov.ons.census.caseapisvc.model.dto.EventDTO;
import uk.gov.ons.census.caseapisvc.model.dto.EventHeaderDTO;
import uk.gov.ons.census.caseapisvc.model.dto.PayloadDTO;

class JsonHelperTest {

  @Test
  void convertObjectToJson_serializesEvent() {
    EventDTO event = createEvent(EVENT_SCHEMA_VERSION);

    String json = JsonHelper.convertObjectToJson(event);

    assertThat(json).contains("\"version\":\"" + EVENT_SCHEMA_VERSION + "\"");
    assertThat(json).contains("\"topic\":\"test-topic\"");
  }

  @Test
  void convertJsonBytesToEvent_deserializesSupportedVersion() {
    EventDTO event = createEvent(EVENT_SCHEMA_VERSION);

    EventDTO converted =
        JsonHelper.convertJsonBytesToEvent(JsonHelper.convertObjectToJson(event).getBytes(UTF_8));

    assertThat(converted.getHeader().getVersion()).isEqualTo(EVENT_SCHEMA_VERSION);
    assertThat(converted.getHeader().getTopic()).isEqualTo("test-topic");
  }

  @Test
  void convertJsonBytesToEvent_throwsForUnsupportedVersion() {
    EventDTO event = createEvent("0.1.0");

    RuntimeException ex =
        assertThrows(
            RuntimeException.class,
            () ->
                JsonHelper.convertJsonBytesToEvent(
                    JsonHelper.convertObjectToJson(event).getBytes(UTF_8)));

    assertThat(ex.getMessage()).contains("Unsupported message version");
    assertThat(ex.getMessage()).contains("0.1.0");
    assertThat(ex.getMessage()).contains(String.join(", ", ALLOWED_INBOUND_EVENT_SCHEMA_VERSIONS));
  }

  @Test
  void convertJsonBytesToEvent_throwsForInvalidJson() {
    RuntimeException ex =
        assertThrows(
            RuntimeException.class, () -> JsonHelper.convertJsonBytesToEvent("{".getBytes(UTF_8)));

    assertThat(ex.getCause()).isNotNull();
  }

  private EventDTO createEvent(String version) {
    EventHeaderDTO header = EventHelper.createEventDTO("test-topic", "RM", "CASE_API");
    header.setVersion(version);

    EventDTO event = new EventDTO();
    event.setHeader(header);
    event.setPayload(new PayloadDTO());
    return event;
  }
}
