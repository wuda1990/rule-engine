package com.quantumn.ruleengine.auditor.impl;

import com.quantumn.ruleengine.auditor.RuleEngine;
import com.quantumn.ruleengine.bean.Mo;
import com.quantumn.ruleengine.bean.TradeAuditBo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.drools.core.impl.StatefulSessionPool;
import org.kie.api.KieBase;
import org.kie.api.runtime.*;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.command.CommandFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Slf4j
public class SimpleRuleEngine implements RuleEngine {

    private KieContainer kieContainer;
    ConcurrentHashMap<String,KieBase> kieBaseMap;

    public SimpleRuleEngine(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    @Override
    public Map audit(TradeAuditBo tradeAuditBo, List<Mo> moList) {
        ConcurrentHashMap<String,Long> scoreMap = new ConcurrentHashMap(8);
//        moList.stream().forEach(mo -> {
//            log.info(mo.toString());
//            KieBase kieBase = kieContainer.getKieBase(mo.getMoId());
//            StatelessKieSession kieSession = kieBase.newStatelessKieSession();
//            List cmds = new ArrayList<>(8);
//            cmds.add(CommandFactory.newSetGlobal("scoreMap", scoreMap, true));
//            cmds.add(CommandFactory.newInsert(tradeAuditBo));
//
//            ExecutionResults results = kieSession.execute(CommandFactory.newBatchExecution(cmds));
//            ConcurrentHashMap<String,Long> resMap = (ConcurrentHashMap) results.getValue("scoreMap");
////            log.info("moId:{},score:{}",mo.getMoId(),resMap.get(mo.getMoId()).toString());
//        });

        moList.forEach(mo -> {
            log.info(mo.toString());
            KieBase kieBase = kieContainer.getKieBase(mo.getMoId());
//            KieSessionsPool kieSessionsPool = kieBase.newKieSessionsPool(8);
//            KieSession kieSession = kieSessionsPool.newKieSession();
            KieSession kieSession = kieBase.newKieSession();
            kieSession.setGlobal("scoreMap",scoreMap);
            kieSession.insert(tradeAuditBo);
//            kieSession.getAgenda().getAgendaGroup("ruleGroup1").setFocus();
//            kieSession.getAgenda().getAgendaGroup("ruleGroup2").setFocus();
            kieSession.fireAllRules();
            kieSession.halt();
            kieSession.dispose();
            log.info("moId:{},score:{}",mo.getMoId(),scoreMap.get(mo.getMoId()).toString());
        });

        //execute rules async by mo
        //以mo为粒度去做多线程不行，在new kiesession的时候虽然kieBase不一样，但workMemory是一个，所以其实drools这样是不支持多线程的。
        //drools多线程需要设置kieBaseConf.setOption(MultithreadEvaluationOption.YES);
//        moList.forEach(mo -> {
//
//            CompletableFuture future = CompletableFuture.runAsync(()->{
//                log.info(mo.toString());
//                KieBase kieBase = kieBaseMap.get(mo.getMoId());
//                KieSession kieSession = kieBase.newKieSession();
//                log.info(mo.toString());
//                kieSession.setGlobal("scoreMap",scoreMap);
//                kieSession.insert(tradeAuditBo);
//                kieSession.getAgenda().getAgendaGroup("ruleGroup1").setFocus();
//                kieSession.getAgenda().getAgendaGroup("ruleGroup2").setFocus();
//                kieSession.fireAllRules();
//                kieSession.halt();
//                kieSession.dispose();
//                log.info("moId:{},score:{}",mo.getMoId(),scoreMap.get(mo.getMoId()).toString());
//            });
//
//        });
        return scoreMap;
    }

}
