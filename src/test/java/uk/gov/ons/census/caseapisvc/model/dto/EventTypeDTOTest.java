package uk.gov.ons.census.caseapisvc.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import uk.gov.ons.census.common.model.entity.EventType;

class EventTypeDTOTest {

  @Test
  void shouldMapLegacyAliasesFromCommonEventType() {
    assertEquals(EventTypeDTO.RECEIPT, EventTypeDTO.fromEventType(EventType.RESPONSE_RECEIVED));
    assertEquals(EventTypeDTO.REFUSAL, EventTypeDTO.fromEventType(EventType.REFUSAL_RECEIVED));
    assertEquals(
        EventTypeDTO.INVALID_CASE, EventTypeDTO.fromEventType(EventType.ADDRESS_NOT_VALID));
    assertEquals(EventTypeDTO.EQ_LAUNCH, EventTypeDTO.fromEventType(EventType.SURVEY_LAUNCHED));
    assertEquals(
        EventTypeDTO.UAC_AUTHENTICATION,
        EventTypeDTO.fromEventType(EventType.RESPONDENT_AUTHENTICATED));
  }

  @Test
  void shouldMapAllCurrentCommonModelEventTypes() {
    for (EventType eventType : EventType.values()) {
      assertEquals(expectedMapping(eventType), EventTypeDTO.fromEventType(eventType));
    }
  }

  @Test
  void shouldFallbackToUnknownForNullEventTypes() {
    assertEquals(EventTypeDTO.UNKNOWN, EventTypeDTO.fromEventType(null));
  }

  private EventTypeDTO expectedMapping(EventType eventType) {
    return switch (eventType) {
      case RESPONSE_RECEIVED -> EventTypeDTO.RECEIPT;
      case REFUSAL_RECEIVED -> EventTypeDTO.REFUSAL;
      case ADDRESS_NOT_VALID -> EventTypeDTO.INVALID_CASE;
      case SURVEY_LAUNCHED -> EventTypeDTO.EQ_LAUNCH;
      case RESPONDENT_AUTHENTICATED -> EventTypeDTO.UAC_AUTHENTICATION;
      default -> EventTypeDTO.valueOf(eventType.name());
    };
  }
}
