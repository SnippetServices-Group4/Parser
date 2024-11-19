package com.services.group4.parser.common;

public enum TestState {
    NOT_STARTED("not_started"),
    RUNNING("running"),
    PASSED("passed"),
    FAILED("failed");

    public final String name;

    TestState(String name) {
        this.name = name;
    }
}
