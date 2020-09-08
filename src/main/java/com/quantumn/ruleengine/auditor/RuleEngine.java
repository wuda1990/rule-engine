package com.quantumn.ruleengine.auditor;

import com.quantumn.ruleengine.bean.Mo;
import com.quantumn.ruleengine.bean.TradeAuditBo;

import java.util.List;
import java.util.Map;

public interface RuleEngine {

    Map audit(TradeAuditBo tradeAuditBo,List<Mo> moList);

}
