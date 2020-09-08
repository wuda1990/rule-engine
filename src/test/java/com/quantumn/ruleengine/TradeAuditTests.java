package com.quantumn.ruleengine;

import com.quantumn.ruleengine.auditor.MoService;
import com.quantumn.ruleengine.auditor.RuleEngine;
import com.quantumn.ruleengine.auditor.RuleEngineFactory;
import com.quantumn.ruleengine.auditor.impl.MoServiceImpl;
import com.quantumn.ruleengine.bean.TradeAuditBo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
@Slf4j
public class TradeAuditTests {

    @Autowired
    RuleEngineFactory ruleEngineFactory;

    @Autowired
    MoService moService;

    @Test
    public void testAudit() {
        TradeAuditBo tradeAuditBo = new TradeAuditBo(1, "001", 29L, new Date());
        RuleEngine ruleEngine = ruleEngineFactory.getRuleEngine();
        ruleEngine.audit(tradeAuditBo,moService.getMoList());

    }

    @Test
    public void testLoadDrls() throws InterruptedException {
        ((MoServiceImpl)moService).loadDrls();

    }
}
