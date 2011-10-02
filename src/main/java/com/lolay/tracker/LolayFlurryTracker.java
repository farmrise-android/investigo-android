/*
 * Created by Lolay, Inc.
 * Copyright 2011 Lolay, Inc. All rights reserved.
 */
package com.lolay.tracker;

import android.os.Build;
import com.flurry.android.Constants;
import com.flurry.android.FlurryAgent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LolayFlurryTracker extends LolayBaseTracker {
    private static Logger logger = Logger.getLogger("com.lolay.tracker");
    private String platform;
    private Map<Object, Object> globalParametersValue = Collections.emptyMap();

    public LolayFlurryTracker(String version) {
        platform = clientPlatform();
        FlurryAgent.setVersionName(version);
    }

    @Override
    public void setIdentifier(String identifier) {
        FlurryAgent.setUserId(identifier);
    }

    @Override
    public void setVersion(String version) {
        FlurryAgent.setVersionName(version);
    }

    @Override
    public void setAge(int age) {
        FlurryAgent.setAge(age);
    }

    @Override
    public void setGender(LolayTrackerGender gender) {
        switch (gender) {
            case MALE:
                FlurryAgent.setGender(Constants.MALE);
                break;
            case FEMALE:
                FlurryAgent.setGender(Constants.FEMALE);
                break;
            case UNKNOWN:
                break;
        }
    }

    @Override
    public void logEvent(String name) {
        logEventWithParams(name, null);
    }

    @Override
    public void logEventWithParams(String name, Map<Object, Object> parameters) {
        FlurryAgent.logEvent(name, buildParameters(parameters));
    }

    @Override
    public void logPage(String name) {
        logPageWithParams(name, null);
    }

    @Override
    public void logPageWithParams(String name, Map<Object, Object> parameters) {
        FlurryAgent.logEvent(name, buildParameters(parameters));
        FlurryAgent.onPageView();
    }

    @Override
    public void logException(Exception exception) {
        FlurryAgent.onError(exception.getMessage(), exception.getMessage(), exception.getClass().getSimpleName());
    }

    @Override
    public void logException(String errorId, String message, String errorClass) {
        FlurryAgent.onError(errorId,message, errorClass);
    }

    public Map<Object, Object> getGlobalParametersValue() {
        return globalParametersValue;
    }

    public void setGlobalParametersValue(Map<Object, Object> globalParametersValue) {
        this.globalParametersValue = globalParametersValue;
    }

    Map<Object, Object> buildParameters(Map<Object, Object> parameters) {
        Map<Object, Object> flurryParameters;

        if (parameters == null) {
            flurryParameters = new HashMap<Object, Object>(1 + globalParametersValue.size());
        } else {
            flurryParameters = parameters;
        }

        if (!getGlobalParametersValue().isEmpty()) {
            flurryParameters.putAll(globalParametersValue);
        }

        flurryParameters.put("platform", platform);

        // flurry for android tracks locale automatically.
        //[flurryParameters setObject:[[NSLocale currentLocale] localeIdentifier] forKey:@"locale"];

        return flurryParameters;
    }

    private String clientPlatform() {
        String manufacturer = Build.MANUFACTURER;
        String product = Build.PRODUCT;
        String model = Build.MODEL;
        String systemVersion = Build.VERSION.RELEASE;
        int sdk = Build.VERSION.SDK_INT;
        return String.format("%s %s (%s): %s %d", manufacturer, product, model, systemVersion, sdk);
    }

}
