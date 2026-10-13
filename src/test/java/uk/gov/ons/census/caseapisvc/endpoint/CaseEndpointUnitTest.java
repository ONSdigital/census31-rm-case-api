package uk.gov.ons.census.caseapisvc.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ons.census.caseapisvc.model.dto.*;
import uk.gov.ons.census.caseapisvc.service.CaseService;
import uk.gov.ons.census.common.model.entity.Case;
import uk.gov.ons.census.common.model.entity.Event;
import uk.gov.ons.census.common.model.entity.EventType;
import uk.gov.ons.census.common.model.entity.RefusalType;
import uk.gov.ons.census.common.model.entity.UacQidLink;

@ExtendWith(MockitoExtension.class)
class CaseEndpointUnitTest {

  @Mock private CaseService caseService;

  @InjectMocks private uk.gov.ons.census.caseapisvc.endpoint.CaseEndpoint caseEndpoint;

  private Case caze;
  private UUID caseId;

  @BeforeEach
  void setup() {
    caseId = UUID.randomUUID();
    caze = mock(Case.class);

    when(caze.getId()).thenReturn(caseId);
  }

  // -------------------------------------------------------------------------
  // findCaseById
  // -------------------------------------------------------------------------
  @Test
  void testFindCaseById_NoEvents() {
    when(caseService.findById(caseId)).thenReturn(caze);
    when(caze.getCaseRef()).thenReturn(12345L);
    when(caze.isReceiptReceived()).thenReturn(true);
    when(caze.getRefusalReceived()).thenReturn(RefusalType.HARD_REFUSAL);
    when(caze.isSurveyLaunched()).thenReturn(true);

    CaseContainerDTO dto = caseEndpoint.findCaseById(caseId, false);

    assertEquals(caseId, dto.getCaseId());
    assertEquals("12345", dto.getCaseRef());
    assertTrue(dto.isReceiptReceived());
    assertTrue(dto.isRefusalReceived());
    assertTrue(dto.isSurveyLaunched());
    assertTrue(dto.getCaseEvents().isEmpty());
    verify(caseService).findById(caseId);
  }

  // -------------------------------------------------------------------------
  // findCaseByReference
  // -------------------------------------------------------------------------
  @Test
  void testFindCaseByReference() {

    when(caseService.findByReference(12345L)).thenReturn(caze);
    when(caze.getCaseRef()).thenReturn(12345L);

    CaseContainerDTO dto = caseEndpoint.findCaseByReference(12345L, false);

    assertEquals(caseId, dto.getCaseId());
    assertEquals("12345", dto.getCaseRef());
    verify(caseService).findByReference(12345L);
  }

  // -------------------------------------------------------------------------
  // findCasesByUPRN
  // -------------------------------------------------------------------------
  @Test
  void testFindCasesByUPRN_WithEvents() {
    // Mock event
    Event event = mock(Event.class);
    when(event.getId()).thenReturn(UUID.randomUUID());
    when(event.getDescription()).thenReturn("TEST_EVENT");
    when(event.getDateTime()).thenReturn(OffsetDateTime.now());
    when(event.getType()).thenReturn(EventType.NEW_CASE);

    // Mock UAC/QID link
    UacQidLink link = mock(UacQidLink.class);
    when(link.getEvents()).thenReturn(List.of(event));

    when(caze.getUacQidLinks()).thenReturn(List.of(link));
    when(caze.getEvents()).thenReturn(List.of(event));

    when(caseService.findByUPRN("123456789", true)).thenReturn(List.of(caze));

    List<CaseContainerDTO> result = caseEndpoint.findCasesByUPRN("123456789", true, true);

    assertEquals(1, result.size());
    assertEquals(2, result.get(0).getCaseEvents().size()); // 1 from UAC, 1 from Case
  }

  // -------------------------------------------------------------------------
  // getCasesByPostcode
  // -------------------------------------------------------------------------
  @Test
  void testGetCasesByPostcode() {
    when(caseService.findByPostcode("AB12CD")).thenReturn(List.of(caze));

    List<CaseContainerDTO> result = caseEndpoint.getCasesByPostcode("AB12CD");

    assertEquals(1, result.size());
    assertEquals(caseId, result.get(0).getCaseId());
    verify(caseService).findByPostcode("AB12CD");
  }

  // -------------------------------------------------------------------------
  // findCaseByQid
  // -------------------------------------------------------------------------
  @Test
  void testFindCaseByQid() {
    when(caze.getAddressType()).thenReturn("HH");
    when(caseService.findCaseByQid("Q123")).thenReturn(caze);

    CaseContainerDTO dto = caseEndpoint.findCaseByQid("Q123");

    assertEquals(caseId, dto.getCaseId());
    assertEquals("HH", dto.getAddressType());
    verify(caseService).findCaseByQid("Q123");
  }

  // -------------------------------------------------------------------------
  // getAllCaseDetailsByCaseId
  // -------------------------------------------------------------------------
  @Test
  void testGetAllCaseDetailsByCaseId() {
    when(caze.getCollectionExercise())
        .thenReturn(mock(uk.gov.ons.census.common.model.entity.CollectionExercise.class));
    when(caze.getCollectionExercise().getId()).thenReturn(UUID.randomUUID());
    when(caseService.findById(caseId)).thenReturn(caze);

    CaseDetailsDTO dto = caseEndpoint.getAllCaseDetailsByCaseId(caseId);

    assertEquals(caseId, dto.getCaseId());
    verify(caseService).findById(caseId);
  }

  @Test
  void testGetAllCaseDetailsByCaseId_IncludesCaseAndUacEvents() {
    when(caze.getCollectionExercise())
        .thenReturn(mock(uk.gov.ons.census.common.model.entity.CollectionExercise.class));
    when(caze.getCollectionExercise().getId()).thenReturn(UUID.randomUUID());
    when(caseService.findById(caseId)).thenReturn(caze);

    OffsetDateTime now = OffsetDateTime.now();

    Event uacEvent = mock(Event.class);
    when(uacEvent.getId()).thenReturn(UUID.randomUUID());
    when(uacEvent.getDescription()).thenReturn("Survey launched");
    when(uacEvent.getDateTime()).thenReturn(now);
    when(uacEvent.getType()).thenReturn(EventType.SURVEY_LAUNCHED);
    when(uacEvent.getChannel()).thenReturn("RH");
    when(uacEvent.getSource()).thenReturn("RM");
    when(uacEvent.getCorrelationId()).thenReturn(UUID.randomUUID());
    when(uacEvent.getProcessedAt()).thenReturn(now);
    when(uacEvent.getPayload()).thenReturn("{}");
    when(uacEvent.getMessageTimestamp()).thenReturn(now);

    Event caseLevelEvent = mock(Event.class);
    when(caseLevelEvent.getId()).thenReturn(UUID.randomUUID());
    when(caseLevelEvent.getDescription()).thenReturn("Refusal received");
    when(caseLevelEvent.getDateTime()).thenReturn(now);
    when(caseLevelEvent.getType()).thenReturn(EventType.REFUSAL_RECEIVED);
    when(caseLevelEvent.getChannel()).thenReturn("RM");
    when(caseLevelEvent.getSource()).thenReturn("RM");
    when(caseLevelEvent.getCorrelationId()).thenReturn(UUID.randomUUID());
    when(caseLevelEvent.getProcessedAt()).thenReturn(now);
    when(caseLevelEvent.getPayload()).thenReturn("{}");
    when(caseLevelEvent.getMessageTimestamp()).thenReturn(now);

    UacQidLink link = mock(UacQidLink.class);
    when(link.getEvents()).thenReturn(List.of(uacEvent));

    when(caze.getUacQidLinks()).thenReturn(List.of(link));
    when(caze.getEvents()).thenReturn(List.of(caseLevelEvent));

    CaseDetailsDTO dto = caseEndpoint.getAllCaseDetailsByCaseId(caseId);

    assertEquals(2, dto.getEvents().size());
    assertTrue(
        dto.getEvents().stream().anyMatch(event -> "SURVEY_LAUNCHED".equals(event.getEventType())));
    assertTrue(
        dto.getEvents().stream()
            .anyMatch(event -> "REFUSAL_RECEIVED".equals(event.getEventType())));
  }

  @Test
  void testGetAllCaseDetailsByCaseId_NullSafeEventAndCollectionExerciseHandling() {
    when(caseService.findById(caseId)).thenReturn(caze);
    when(caze.getCollectionExercise()).thenReturn(null);
    when(caze.getUacQidLinks()).thenReturn(null);
    when(caze.getEvents()).thenReturn(null);

    CaseDetailsDTO dto = caseEndpoint.getAllCaseDetailsByCaseId(caseId);

    assertNull(dto.getCollectionExerciseId());
    assertNotNull(dto.getEvents());
    assertTrue(dto.getEvents().isEmpty());
  }

  // -------------------------------------------------------------------------
  // buildCaseContainerDTO: event mapping
  // -------------------------------------------------------------------------
  @Test
  void testBuildCaseContainerDTO_MapsEventsCorrectly() {
    Event event = mock(Event.class);
    when(event.getId()).thenReturn(UUID.randomUUID());
    when(event.getDescription()).thenReturn("DESC");
    when(event.getDateTime()).thenReturn(OffsetDateTime.now());
    when(event.getType()).thenReturn(EventType.NEW_CASE);

    UacQidLink link = mock(UacQidLink.class);
    when(link.getEvents()).thenReturn(List.of(event));

    when(caze.getUacQidLinks()).thenReturn(List.of(link));
    when(caze.getEvents()).thenReturn(List.of(event));

    CaseContainerDTO dto = invokeBuildCaseContainerDTO(caze, true);

    assertEquals(2, dto.getCaseEvents().size());
    assertEquals("DESC", dto.getCaseEvents().get(0).getDescription());
  }

  @Test
  void testBuildCaseContainerDTO_MapsCanonicalEventTypesToApiTypes() {
    Event refusalEvent = mock(Event.class);
    when(refusalEvent.getId()).thenReturn(UUID.randomUUID());
    when(refusalEvent.getDescription()).thenReturn("Refusal Received");
    when(refusalEvent.getDateTime()).thenReturn(OffsetDateTime.now());
    when(refusalEvent.getType()).thenReturn(EventType.REFUSAL_RECEIVED);

    Event invalidEvent = mock(Event.class);
    when(invalidEvent.getId()).thenReturn(UUID.randomUUID());
    when(invalidEvent.getDescription()).thenReturn("Invalid address");
    when(invalidEvent.getDateTime()).thenReturn(OffsetDateTime.now());
    when(invalidEvent.getType()).thenReturn(EventType.ADDRESS_NOT_VALID);

    Event surveyLaunchedEvent = mock(Event.class);
    when(surveyLaunchedEvent.getId()).thenReturn(UUID.randomUUID());
    when(surveyLaunchedEvent.getDescription()).thenReturn("Survey launched");
    when(surveyLaunchedEvent.getDateTime()).thenReturn(OffsetDateTime.now());
    when(surveyLaunchedEvent.getType()).thenReturn(EventType.SURVEY_LAUNCHED);

    Event receiptEvent = mock(Event.class);
    when(receiptEvent.getId()).thenReturn(UUID.randomUUID());
    when(receiptEvent.getDescription()).thenReturn("Receipt received");
    when(receiptEvent.getDateTime()).thenReturn(OffsetDateTime.now());
    when(receiptEvent.getType()).thenReturn(EventType.RESPONSE_RECEIVED);

    Event respondentAuthenticatedEvent = mock(Event.class);
    when(respondentAuthenticatedEvent.getId()).thenReturn(UUID.randomUUID());
    when(respondentAuthenticatedEvent.getDescription()).thenReturn("Respondent authenticated");
    when(respondentAuthenticatedEvent.getDateTime()).thenReturn(OffsetDateTime.now());
    when(respondentAuthenticatedEvent.getType()).thenReturn(EventType.RESPONDENT_AUTHENTICATED);

    Event caseUpdateEvent = mock(Event.class);
    when(caseUpdateEvent.getId()).thenReturn(UUID.randomUUID());
    when(caseUpdateEvent.getDescription()).thenReturn("Case update event");
    when(caseUpdateEvent.getDateTime()).thenReturn(OffsetDateTime.now());
    when(caseUpdateEvent.getType()).thenReturn(EventType.CASE_UPDATE);

    Event fieldCaseUpdatedEvent = mock(Event.class);
    when(fieldCaseUpdatedEvent.getId()).thenReturn(UUID.randomUUID());
    when(fieldCaseUpdatedEvent.getDescription()).thenReturn("Field case updated event");
    when(fieldCaseUpdatedEvent.getDateTime()).thenReturn(OffsetDateTime.now());
    when(fieldCaseUpdatedEvent.getType()).thenReturn(EventType.FIELD_CASE_UPDATED);

    UacQidLink link = mock(UacQidLink.class);
    when(link.getEvents())
        .thenReturn(
            List.of(
                surveyLaunchedEvent,
                receiptEvent,
                respondentAuthenticatedEvent,
                caseUpdateEvent,
                fieldCaseUpdatedEvent));

    when(caze.getUacQidLinks()).thenReturn(List.of(link));
    when(caze.getEvents()).thenReturn(List.of(refusalEvent, invalidEvent));

    CaseContainerDTO dto = invokeBuildCaseContainerDTO(caze, true);

    List<EventTypeDTO> mappedTypes =
        dto.getCaseEvents().stream().map(CaseEventDTO::getType).toList();

    assertThat(mappedTypes)
        .contains(
            EventTypeDTO.REFUSAL,
            EventTypeDTO.INVALID_CASE,
            EventTypeDTO.EQ_LAUNCH,
            EventTypeDTO.RECEIPT,
            EventTypeDTO.UAC_AUTHENTICATION,
            EventTypeDTO.CASE_UPDATE,
            EventTypeDTO.FIELD_CASE_UPDATED);

    assertThat(mappedTypes).doesNotContain(EventTypeDTO.UNKNOWN);
  }

  @Test
  void testBuildCaseContainerDTO_WithNullEventCollections() {
    when(caze.getUacQidLinks()).thenReturn(null);
    when(caze.getEvents()).thenReturn(null);

    CaseContainerDTO dto = invokeBuildCaseContainerDTO(caze, true);

    assertNotNull(dto.getCaseEvents());
    assertTrue(dto.getCaseEvents().isEmpty());
  }

  // Helper to call private method via reflection
  private CaseContainerDTO invokeBuildCaseContainerDTO(Case caze, boolean includeEvents) {
    try {
      var method =
          uk.gov.ons.census.caseapisvc.endpoint.CaseEndpoint.class.getDeclaredMethod(
              "buildCaseContainerDTO", Case.class, boolean.class);
      method.setAccessible(true);
      return (CaseContainerDTO) method.invoke(caseEndpoint, caze, includeEvents);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
