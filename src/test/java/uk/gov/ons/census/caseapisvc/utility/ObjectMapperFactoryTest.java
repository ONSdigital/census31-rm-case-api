package uk.gov.ons.census.caseapisvc.utility;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class ObjectMapperFactoryTest {
  private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.objectMapper();
  private static final String CASE_ID = "10000000001";
  private static final OffsetDateTime EVENT_TIME = OffsetDateTime.parse("2024-06-01T10:15:30Z");
  private static final String ACTION = "CREATE";

  @Test
  void shouldPreserveFieldOrderAndWriteDatesAsIsoStrings() {
    CaseEvent caseEvent = new CaseEvent(CASE_ID, EVENT_TIME, ACTION);

    assertThat(OBJECT_MAPPER.writeValueAsString(caseEvent))
        .isEqualTo(
            "{\"caseId\":\"10000000001\",\"eventTime\":\"2024-06-01T10:15:30Z\",\"action\":\"CREATE\"}");
  }

  @Test
  void shouldIgnoreUnknownPropertiesWhenReadingJson() {
    CaseEvent caseEvent =
        OBJECT_MAPPER.readValue(
            "{\"caseId\":\"10000000001\",\"eventTime\":\"2024-06-01T10:15:30Z\","
                + "\"action\":\"CREATE\",\"extraField\":\"ignored\"}",
            CaseEvent.class);

    assertThat(caseEvent).isEqualTo(new CaseEvent(CASE_ID, EVENT_TIME, ACTION));
  }

  @Test
  void shouldIgnoreTrailingTokensWhenReadingJson() {
    CaseEvent caseEvent =
        OBJECT_MAPPER.readValue(
            "{\"caseId\":\"10000000001\",\"eventTime\":\"2024-06-01T10:15:30Z\","
                + "\"action\":\"CREATE\"} true",
            CaseEvent.class);

    assertThat(caseEvent).isEqualTo(new CaseEvent(CASE_ID, EVENT_TIME, ACTION));
  }

  private record CaseEvent(String caseId, OffsetDateTime eventTime, String action) {}
}
