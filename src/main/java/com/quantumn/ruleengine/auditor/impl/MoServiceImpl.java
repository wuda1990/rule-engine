package com.quantumn.ruleengine.auditor.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.quantumn.ruleengine.auditor.MoService;
import com.quantumn.ruleengine.bean.Mo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

@Service
@Slf4j
public class MoServiceImpl implements MoService {

    Cache<String,Mo> cache = CacheBuilder.newBuilder().build();
    @Override
    public List<Mo> getMoList() {
        List<Mo> moList = new ArrayList<Mo>(){
            {
                Mo mo1 = new Mo("100","MerchantRisk", Arrays.asList(
                        "package com.faster.fasterboot\n" +
                                "\n" +
                                "import com.quantumn.ruleengine.bean.TradeAuditBo\n" +
                                "import java.util.concurrent.ConcurrentHashMap\n" +
                                "global java.util.concurrent.ConcurrentHashMap<String,Long> scoreMap;\n" +
                                "\n" +
                                "rule \"txnaudit1\"\n" +
                                "    no-loop true\n" +
//                                "    agenda-group \"ruleGroup1\"\n" +
                                "    when\n" +
                                "        bo : TradeAuditBo(card==\"001\" &&amount>5 && amount<30)\n" +
                                "    then\n" +
                                "        System.out.println(\"hit rule: txnaudit1\");\n" +
                                "        Long score = (Long) scoreMap.getOrDefault(\"100\",0L);\n" +
                                "        scoreMap.put(\"100\",score+5L);\n" +
                                "        System.out.println(\"score:\"+scoreMap.get(\"100\"));\n" +
                                "\n" +
                                "end\n" +
                                "\n" +
                                "\n" +
                                "rule \"txnaudit2\"\n" +
                                "    no-loop true\n" +
//                                "    agenda-group \"ruleGroup2\"\n" +
                                "    when\n" +
                                "        bo : TradeAuditBo(tradeTime>\"01-Mar-2020\" && tradeTime<\"01-Jun-2020\")\n" +
                                "    then\n" +
                                "        System.out.println(\"hit rule: txnaudit2\");\n" +
                                "        Long score = (Long) scoreMap.getOrDefault(\"100\",0L);\n" +
                                "        scoreMap.put(\"100\",score+50L);\n" +
                                "        System.out.println(\"score:\"+scoreMap.get(\"100\"));\n" +
                                "\n" +
                                "\n" +
                                "\n" +
                                "end"

                ));

                Mo mo2 = new Mo("101","TradeRisk", Arrays.asList(
                        "package com.faster.fasterboot\n" +
                                "\n" +
                                "import com.quantumn.ruleengine.bean.TradeAuditBo\n" +
                                "global java.util.concurrent.ConcurrentHashMap<String,Long> scoreMap;\n" +
                                "\n" +
                                "rule \"ruleGroup1-txnaudit1\"\n" +
                                "    no-loop true\n" +
//                                "    agenda-group \"ruleGroup1\"\n" +
                                "    when\n" +
                                "        bo : TradeAuditBo(card==\"001\")\n" +
                                "    then\n" +
                                "        System.out.println(\"hit rule: group1-txnaudit1\");\n" +
                                "        Long score = (Long) scoreMap.getOrDefault(\"101\",0L);\n" +
                                "        scoreMap.put(\"101\",score+100L);\n" +
                                "        System.out.println(\"score:\"+scoreMap.get(\"101\"));\n" +
                                "\n" +
                                "end\n" +
                                "\n"
                ));

                add(mo1);
                add(mo2);
            }
        };
        return moList;
    }

    public void loadDrls() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture f = CompletableFuture.supplyAsync(()->{
            //load drls from db
            log.info("load drls Thread:{}",Thread.currentThread().getName());
            List<Mo> moList = getMoList();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return moList;
        }).thenAcceptAsync(moList -> {
            log.info("thenAccept Thread:{}", Thread.currentThread().getName());
            moList.forEach(
                    mo -> {
                        log.info("put mo:{} to cache", mo.toString());
                        cache.put(mo.getMoId(), mo);
                        latch.countDown();
                    });

        });
//        f.join();
        latch.await();
    }
}
