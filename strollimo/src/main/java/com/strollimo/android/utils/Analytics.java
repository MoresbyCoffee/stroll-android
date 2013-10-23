package com.strollimo.android.utils;

import com.flurry.android.FlurryAgent;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.strollimo.android.StrollimoApplication;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcoc on 25/09/2013.
 */
public class Analytics {

    public static final String FLURRY_DEBUG_KEY = "23F35RQ9T33RMY5X3FH6";
    public static final String FLURRY_PRODUCTION_KEY = "X9YHVP5TX29M8GSQWSJ3";
    private static final String MIXPANEL_DEBUG_KEY = "dc79ca458e2252c03f31e8a86907e427";
    private static final String MIXPANEL_PRODUCTION_KEY = "eeb6606cdbeb71f2774b1abad3e540d0";

    private static MixpanelAPI sMixpanel;

    public enum Event {
        SELECT_MYSTERY_ON_MAP("Select mystery on map"),
        OPEN_MYSTERY_MAIN("Open mystery main"),
        OPEN_MYSTERY_SECRETS("Open mystery secrets"),
        SWIPE_SECRET("Swipe secret"),
        OPEN_CAPTURE("Open capture"),
        QUEST_COMPLETE("Quest complete"),
        FEEDBACK_PAGE1("Feedback page 1"),
        FEEDBACK_PAGE2("Feedback page 2"),
        FEEDBACK_LIKE("Like app"),
        FEEDBACK_USE("Use it again"),
        FEEDBACK_RECOMMEND("Recommend to friends"),
        FEEDBACK_COLLECT("Collect points and achievements"),
        FEEDBACK_FIND_PHOTOS("Find photos"),
        FEEDBACK_DISCOVER_PLACES("Discover places"),
        FEEDBACK_LEARNING_FACTS("Learning new facts"),
        FEEDBACK_FUN("Having fun with friends"),
        FEEDBACK_GET_OUT("Getting out");


        private String mEvent;

        Event(String event) {
            mEvent = event;
        }

        @Override
        public String toString() {
            return mEvent;
        }
    }


    public static void track(Event event) {
        getMixpanel().track(event.toString(), null);
        FlurryAgent.logEvent(event.toString());
    }

    public static void track(Event event, Map<String, String> params) {
        getMixpanel().track(event.toString(), new JSONObject(params));
        FlurryAgent.logEvent(event.toString(), params);
    }

    public static void track(Event event, String value) {
        Map<String,String> param = new HashMap<String, String>();
        param.put("value", value);
        getMixpanel().track(event.toString(), new JSONObject(param));
        FlurryAgent.logEvent(event.toString(), param);
    }

    private static synchronized MixpanelAPI getMixpanel() {
        if (sMixpanel == null) {
            final String token;
            if (Utils.isDebugBuild()) {
                token = Analytics.MIXPANEL_DEBUG_KEY;
            } else {
                token = Analytics.MIXPANEL_PRODUCTION_KEY;
            }
            sMixpanel = MixpanelAPI.getInstance(StrollimoApplication.getContext(), token);
        }
        return sMixpanel;
    }

    public static void flush() {
        if (sMixpanel != null) {
            sMixpanel.flush();
        }
    }
}
