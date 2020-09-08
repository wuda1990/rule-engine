package com.quantumn.ruleengine.auditor.impl;

import com.quantumn.ruleengine.auditor.MoService;
import com.quantumn.ruleengine.auditor.RuleEngine;
import com.quantumn.ruleengine.auditor.RuleEngineFactory;
import com.quantumn.ruleengine.bean.Mo;
import com.quantumn.ruleengine.exception.EngineExceptionEnum;
import com.quantumn.ruleengine.exception.RuleEngineException;
import lombok.extern.slf4j.Slf4j;
import org.drools.core.RuleBaseConfiguration;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.conf.MultithreadEvaluationOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class SimpleRuleEngineFactory implements RuleEngineFactory {

    @Autowired
    MoService moService;

    @Override
    public RuleEngine getRuleEngine() {
        log.info("init rule engine...");
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        KieModuleModel kieModuleModel = kieServices.newKieModuleModel();

        //build kiebase and add drls
        AtomicInteger count = new AtomicInteger();
        List<Mo> moList = moService.getMoList();
        moList.forEach(mo -> {
            kieModuleModel.newKieBaseModel(mo.getMoId()).addPackage(mo.getMoId());
            for (int i = 0; i < mo.getMoDrls().size();i++) {
                String fileName = "src/main/resources/" + mo.getMoId() + "/file" + i + ".drl";
                log.info(fileName);
                kieFileSystem.write(fileName, mo.getMoDrls().get(i));
            }
            return;
        }
        );
        kieFileSystem.writeKModuleXML(kieModuleModel.toXML());
        //build the kieModule
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            throw new RuleEngineException(EngineExceptionEnum.RULE_COMPILE_ERROR, results.getMessages().toString());
        }

        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        if (kieContainer == null) {
            throw new RuleEngineException(EngineExceptionEnum.CONTAINER_ERROR);
        }
        KieBaseConfiguration config = new RuleBaseConfiguration();
        config.setOption(MultithreadEvaluationOption.YES); //开启drools内部多线程
        //create kieBase for each mo
        ConcurrentHashMap kieBaseMap = new ConcurrentHashMap();
        moList.forEach(mo -> {
            kieBaseMap.put(mo.getMoId(), kieContainer.newKieBase(mo.getMoId(), config));
        });

        RuleEngine ruleEngine = new SimpleRuleEngine(kieContainer);
        ((SimpleRuleEngine) ruleEngine).setKieBaseMap(kieBaseMap);

        log.info("init rule engine end");
        return ruleEngine;
    }

}
