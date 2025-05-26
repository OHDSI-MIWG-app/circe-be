-- Begin Image Occurrence Criteria
SELECT C.person_id, C.image_occurrence_id AS event_id, C.start_date, C.end_date,
       C.image_occurrence_id, C.start_date AS sort_date@additionalColumns
FROM 
(
  SELECT @selectClause @ordinalExpression
  FROM @cdm_database_schema.IMAGE_OCCURRENCE io
  @codesetClause
) C
@joinClause
@whereClause
-- End Image Occurrence Criteria
