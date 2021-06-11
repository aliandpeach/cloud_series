package com.yk.comp.es.model;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * 1. application.yml : index.prefix
 *    indexName = "#{@environment.getProperty('index.prefix')}"
 *
 * 2. indexName = "log-#{T(java.time.LocalDate).now().toString()}"
 *
 * 3. @Component
 *    public class indexNameProvider {
 *       public String timeSuffix() {
 *           return LocalTime.now().truncatedTo(ChronoUnit.MINUTES).toString().replace(':', '-');
 *       }
 *    }
 *    indexName = "#{@indexNameProvider.timeSuffix()}"
 *
 *
 *    Important notice:
 *    The evaluation of SpEL for index names is only done for the index names defined in the @Document annotation.
 *    It is not done for index names that are passed as a IndexCoordinates parameter in the different methods of the ElasticsearchOperations or IndexOperations interfaces.
 *    If it were allowed on these, it would be easy to set up a scenario, where an expression is read from some outside source.
 *    And then someone might send something like "log-#{T(java.lang.Runtime).getRuntime().exec(new String[]{'/bin/rm', '/tmp/somefile'})}" which will not provide an index name,
 *    but delete files on your computer.
 */
@Data
//@Document(indexName = "#{@processResultService.getIndexNamePart()}", type = "_doc")
@Document(indexName = "matching_result_ff946d3af4ed4212bd103b8495ec7fd6", type = "_doc")
public class ProcessResult
{
    private float score;

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
