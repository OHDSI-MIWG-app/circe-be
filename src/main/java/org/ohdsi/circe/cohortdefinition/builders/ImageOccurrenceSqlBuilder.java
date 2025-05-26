package org.ohdsi.circe.cohortdefinition.builders;

import org.apache.commons.lang3.StringUtils;
import org.ohdsi.circe.cohortdefinition.ImageOccurrence;
import org.ohdsi.circe.helper.ResourceHelper;

import java.util.*;

import static org.ohdsi.circe.cohortdefinition.builders.BuilderUtils.getCodesetInExpression;

public class ImageOccurrenceSqlBuilder<T extends ImageOccurrence> extends CriteriaSqlBuilder<T> {

    // Custom SQL Template
    private final static String IMAGE_OCCURRENCE_TEMPLATE = ResourceHelper
            .GetResourceAsString("/resources/cohortdefinition/sql/imageOccurrence.sql");

    // default columns are those that are specified in the template, and dont' need
    // to be added if specifeid in 'additionalColumns'
    private final Set<CriteriaColumn> DEFAULT_COLUMNS = new HashSet<>(
            Arrays.asList(CriteriaColumn.START_DATE, CriteriaColumn.END_DATE, CriteriaColumn.VISIT_ID));
    // SELECT Base Columns
    private final List<String> DEFAULT_SELECT_COLUMNS = new ArrayList<>(
            Arrays.asList("io.person_id", "io.image_occurrence_id"));

    @Override
    protected Set<CriteriaColumn> getDefaultColumns() {
        return DEFAULT_COLUMNS;
    }

    @Override
    protected String getQueryTemplate() {
        return IMAGE_OCCURRENCE_TEMPLATE;
    }

    @Override
    protected String getTableColumnForCriteriaColumn(CriteriaColumn column) {
        switch (column) {
            case DOMAIN_CONCEPT:
                return "C.modality_concept_id";
            default:
                throw new IllegalArgumentException("Invalid CriteriaColumn for ImageOccurrence: " + column);
        }
    }

    @Override
    protected String embedCodesetClause(String query, T criteria) {
        return StringUtils.replace(query, "@codesetClause",
                BuilderUtils.getCodesetJoinExpression(
                        criteria.modalityCS != null ? criteria.modalityCS.codesetId : null,
                        "io.modality_concept_id",
                        null,
                        "io.modality_source_concept_id"));
    }

    @Override
    protected String embedOrdinalExpression(String query, T criteria, List<String> whereClauses) {

        // first
        if (criteria.first != null && criteria.first) {
            whereClauses.add("C.ordinal = 1");
            query = StringUtils.replace(query, "@ordinalExpression",
                    ", row_number() over (PARTITION BY io.person_id ORDER BY io.drug_exposure_start_date, io.drug_exposure_id) as ordinal");
        } else {
            query = StringUtils.replace(query, "@ordinalExpression", "");
        }

        return query;
    }

    @Override
    protected List<String> resolveSelectClauses(T criteria) {
        List<String> selectCols = new ArrayList<>(DEFAULT_SELECT_COLUMNS);

        if (criteria.modalityCS != null) {
            selectCols.add("io.modality_concept_id");
        }

        if (criteria.anatomicSiteCS != null) {
            selectCols.add("io.anatomic_site_concept_id");
        }

        // start_date / end_date
        selectCols.add("io.image_date as start_date, io.image_date as end_date");

        return selectCols;
    }

    @Override
    protected List<String> resolveWhereClauses(T criteria) {
        List<String> whereClauses = new ArrayList<>();

        if (criteria.modalityCS != null) {
            whereClauses.add(getCodesetInExpression("io.modality_concept_id", criteria.modalityCS));
        }

        if (criteria.anatomicSiteCS != null) {
            whereClauses.add(getCodesetInExpression("io.anatomic_site_concept_id", criteria.anatomicSiteCS));
        }

        return whereClauses;
    }

    @Override
    protected List<String> resolveJoinClauses(T criteria) {
        return new ArrayList<>(); // If no join needed
    }
}
