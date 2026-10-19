package com.campus.sensormonitor.model;

public enum RuleCondition {
    GT, GTE, LT, LTE, EQ;

    public boolean matches(double actual, double threshold) {
        return switch (this) {
            case GT -> actual > threshold;
            case GTE -> actual >= threshold;
            case LT -> actual < threshold;
            case LTE -> actual <= threshold;
            case EQ -> Double.compare(actual, threshold) == 0;
        };
    }
}
