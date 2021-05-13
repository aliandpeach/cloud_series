package com.yk.comp.es.model;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "matching_result_661d2b9237bd4a4392061d9306f33bba")
public class ProcessResult
{
    private String id;

    private String incExternalId;

    private String matchContent;

    private String policyCategories;

    private String srcFilepath;

    private String filepath;

    private String ruleName;

    private String breachContent;

    private String ruleId;

    private String jobId;

    private String jobName;

    private String md5Value;
}
