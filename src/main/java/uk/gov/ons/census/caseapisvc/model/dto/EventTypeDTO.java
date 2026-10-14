package uk.gov.ons.census.caseapisvc.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.ons.census.common.model.entity.EventType;

@Schema(
    description = "Enumeration of all possible event types that can occur in the census RM system",
    example = "NEW_CASE",
    enumAsRef = true)
public enum EventTypeDTO {
  @Schema(description = "New case created")
  NEW_CASE,
  @Schema(description = "Response received from respondent")
  RESPONSE_RECEIVED,
  @Schema(description = "Refusal received from respondent")
  REFUSAL_RECEIVED,
  @Schema(description = "Data erase requested")
  ERASE_DATA,
  @Schema(description = "EQ (eCollect) questionnaire launched")
  SURVEY_LAUNCHED,
  @Schema(description = "UAC authenticated by respondent")
  RESPONDENT_AUTHENTICATED,
  @Schema(description = "Telephone capture event processed")
  TELEPHONE_CAPTURE,
  @Schema(description = "Print fulfillment request processed")
  PRINT_FULFILMENT,
  @Schema(description = "Data export file generated")
  EXPORT_FILE,
  @Schema(description = "UAC deactivated")
  DEACTIVATE_UAC,
  @Schema(description = "Sample data updated")
  UPDATE_SAMPLE,
  @Schema(description = "Sample sensitive data updated")
  UPDATE_SAMPLE_SENSITIVE,
  @Schema(description = "SMS fulfillment request processed")
  SMS_FULFILMENT,
  @Schema(description = "SMS fulfillment action rule triggered")
  ACTION_RULE_SMS_REQUEST,
  @Schema(description = "SMS confirmation action rule triggered")
  ACTION_RULE_SMS_CONFIRMATION,
  @Schema(description = "SMS fulfillment confirmation processed")
  FULFILMENT_SMS_CONFIRMATION,
  @Schema(description = "Case update event processed")
  CASE_UPDATE,
  @Schema(description = "UAC update event processed")
  UAC_UPDATE,
  @Schema(description = "Fulfilment request event processed")
  FULFILMENT_REQUEST,
  @Schema(description = "Field case updated event processed")
  FIELD_CASE_UPDATED,
  @Schema(description = "Questionnaire linked event processed")
  QUESTIONNAIRE_LINKED,
  @Schema(description = "Fulfilment confirmation event processed")
  FULFILMENT_CONFIRMATION,
  @Schema(description = "Fulfilment undelivered event processed")
  FULFILMENT_UNDELIVERED,
  @Schema(description = "Case marked as invalid")
  ADDRESS_NOT_VALID,
  @Schema(description = "Event type not yet mapped by this API version")
  UNKNOWN;

  /** Maps persisted common-model event types to API enum values. */
  public static EventTypeDTO fromEventType(EventType eventType) {
    if (eventType == null) {
      return UNKNOWN;
    }

    try {
      return EventTypeDTO.valueOf(eventType.name());
    } catch (IllegalArgumentException ignored) {
      return UNKNOWN;
    }
  }
}
