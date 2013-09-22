package com.strollimo.android.model;

/**
 * Created by marcoc on 22/09/2013.
 */
public enum MixpanelEvent {
    SELECT_MYSTERY_ON_MAP("Select mystery on map"),
    OPEN_MYSTERY_MAIN("Open mystery main"),
    OPEN_MYSTERY_SECRETS("Open mystery secrets"),
    SWIPE_SECRET("Swipe secret"),
    OPEN_CAPTURE("Open capture"),
    QUEST_COMPLETE("Quest complete");

    private String mEvent;

    MixpanelEvent(String event) {
        mEvent = event;
    }

    @Override
    public String toString() {
        return mEvent;
    }
}
