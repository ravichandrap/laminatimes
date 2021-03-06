package com.laminatimes.timesheet;

import com.laminatimes.timesheet.entity.*;
import com.laminatimes.timesheet.service.TimeSheetProjectService;
import com.laminatimes.timesheet.service.TimeSheetService;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/timesheet")
@RefreshScope
public class TimeSheetController {

    final String PROJECT_URL = "http://PROJECTS/projects/";

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.env.prod: default value }")
    private String defaultValue;

    @Value("This is static value")
    private String staticValue;

    @Value("#{${sping.map.dbvalues}}")
    private Map<String, String> dbValues;

    @Autowired
    private DBDetails dbDetails;

    @Autowired
    private TimeSheetService service;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private TimeSheetProjectService timeSheetProjectService;

    @GetMapping
    public List<TimeSheetEntity> getAll() {
        System.out.println("applicationName: "+applicationName);
        System.out.println("defaultValue: "+defaultValue);
        System.out.println("staticValue: "+staticValue);
        System.out.println("dbValues:"+dbValues);
        System.out.println(dbDetails.toString());

        List<TimeSheetEntity> entities = service.getAll();
         return entities.stream().peek(e-> e.setTimeSheetProjects(e.getTimeSheetProjects().stream().peek(p->{
             Project ptemp = getProject(p.getProjectId());
             p.setDescription(ptemp.getDescription());
             p.setName(ptemp.getName());
         }).collect(Collectors.toList()))).collect(Collectors.toList());
    }

    @PostMapping
    @PutMapping
    public ResponseEntity create(@RequestBody TimeSheetEntity timeSheetEntity) {
        System.out.println("==========================================");
        System.out.println(timeSheetEntity.toString());
        System.out.println("==========================================");
        validateTimeSheetEntity(timeSheetEntity);
        service.create(timeSheetEntity);
        return new ResponseEntity(HttpStatus.OK);
    }

    private void validateTimeSheetEntity(TimeSheetEntity timeSheetEntity) {
        List<TimeSheetProjectEntity> projects = timeSheetEntity.getTimeSheetProjects();
        projects.forEach(entity -> getProject(entity.getProjectId()));

    }

    @HystrixCommand(fallbackMethod = "getFallbackProject")
    private Project getProject(Long id) {
        System.out.println("PROJECT_URL+id: "+PROJECT_URL+id);
            return restTemplate.getForObject(PROJECT_URL+id, Project.class);
    }

    private Project getFallbackProject(Long id) {
        System.out.println("id0000000..........................."+id);
        return new Project();
    }
}
