package com.yk.es;

import com.yk.comp.es.model.ProcessResult;
import com.yk.comp.es.service.ProcessResultService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class ESTest
{
    @Autowired
    private ProcessResultService processResultService;

    @Test
    public void query()
    {
        processResultService.getBy(new ProcessResult());
    }

    @Test
    public void queryById()
    {
        Optional<ProcessResult> resultOptional = processResultService.getById("cc496960d1eb226c0b8f37550321cf2c00");
        resultOptional.ifPresent(System.out::println);
    }
    @Test
    public void queryAll()
    {
        Iterable<ProcessResult> results = processResultService.getAll();
        System.out.println(results);
    }
}
