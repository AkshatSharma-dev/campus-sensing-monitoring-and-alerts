package com.campus.sensormonitor.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RuleConditionTest {
    @Test
    void supportsAllComparisonOperators() {
        assertTrue(RuleCondition.GT.matches(36.2, 35.0));
        assertTrue(RuleCondition.GTE.matches(35.0, 35.0));
        assertTrue(RuleCondition.LT.matches(20.0, 25.0));
        assertTrue(RuleCondition.LTE.matches(25.0, 25.0));
        assertTrue(RuleCondition.EQ.matches(25.0, 25.0));
        assertFalse(RuleCondition.GT.matches(35.0, 35.0));
    }
}
