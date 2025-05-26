package org.ohdsi.circe.cohortdefinition;

import org.ohdsi.circe.cohortdefinition.builders.BuilderOptions;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageOccurrence extends Criteria {
    @JsonProperty("ModalityCS")
    public ConceptSetSelection modalityCS;

    @JsonProperty("AnatomicSiteCS")
    public ConceptSetSelection anatomicSiteCS;

    @JsonProperty("First")
    public Boolean first;

    @Override
    public String accept(IGetCriteriaSqlDispatcher dispatcher, BuilderOptions options) {
        return dispatcher.getCriteriaSql(this, options);
    }
}
