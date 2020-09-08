package com.quantumn.ruleengine;

import com.quantumn.ruleengine.bean.TradeAuditBo;
import lombok.extern.slf4j.Slf4j;
import org.drools.core.RuleBaseConfiguration;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
@Slf4j
class RuleEngineApplicationTests {

    @Test
    void contextLoads() {
        log.info("rule engine start...");
        try {
            KieServices kieServices = KieServices.Factory.get();
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            KieModuleModel kieModuleModel = kieServices.newKieModuleModel();
            kieModuleModel.newKieBaseModel("simpleRule").addPackage("simpleRules");
            kieFileSystem.writeKModuleXML(kieModuleModel.toXML());

            String rule1 = "package com.faster.fasterboot\n" +
                    "\n" +
                    "import com.quantumn.ruleengine.bean.TradeAuditBo\n" +
                    "\n" +
                    "rule \"txnaudit\"\n" +
                    "    no-loop true\n" +
                    "    when\n" +
                    "        bo : TradeAuditBo(amount>5)\n" +
                    "    then\n" +
                    "        System.out.println(\"hit rule: amount>5\");\n" +
                    "end\n";

            kieFileSystem.write("src/main/resources/simpleRules/trade0429.drl",rule1);
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);

            kieBuilder.buildAll();
            Results results = kieBuilder.getResults();
            if (results.hasMessages(Message.Level.ERROR)) {
                throw new RuntimeException(results.getMessages().toString());
            }else{
                KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());

                KieBaseConfiguration config = new RuleBaseConfiguration();
                KieBase kieBase = kieContainer.newKieBase("simpleRule", config);
                TradeAuditBo tradeAuditBo = new TradeAuditBo(1, "123", 29L, new Date());
                KieSession kieSession = kieBase.newKieSession();
                kieSession.insert(tradeAuditBo);
                kieSession.fireAllRules();
                kieSession.halt();
                kieSession.dispose();

            }

        } catch (Exception e) {
            log.info("rule engine exception",e);
        }
        log.info("rule engine end...");
    }

}
