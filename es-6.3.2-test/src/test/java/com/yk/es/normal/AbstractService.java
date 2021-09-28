package com.yk.es.normal;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;

public abstract class AbstractService
{
    protected String index;
    
    protected String type;
    
    protected RestHighLevelClient rhlClient = ESClientFactory.getHighLevelClient();
    
    
    @Before
    public void prepare()
    {
        parameters();
    }
    
    public abstract void parameters();
}
