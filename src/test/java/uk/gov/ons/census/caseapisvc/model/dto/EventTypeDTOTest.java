package uk.gov.ons.census.caseapisvc.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import uk.gov.ons.census.common.model.entity.EventType;

class EventTypeDTOTest {

  @Test
  void shouldMapAllCurrentCommonModelEventTypesByName() {
    for (EventType eventType : EventType.values()) {
      assertEquals(EventTypeDTO.valueOf(eventType.name()), EventTypeDTO.fromEventType(eventType));
    }
  }

  @Test
  void shouldFallbackToUnknownForNullEventTypes() {
    assertEquals(EventTypeDTO.UNKNOWN, EventTypeDTO.fromEventType(null));
  }
}
