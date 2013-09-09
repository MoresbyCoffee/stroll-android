package com.strollimo.android.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Secret extends BaseAccomplishable {
    public static final String TYPE = "secret";


    public enum SecretStatus {
        PENDING,
        REJECTED,
        ACCOMPLISHED,
        UNPICKED;
    }

    @Expose
    private List<PickupMode> pickupModes = new ArrayList<PickupMode>();

    public Secret(String id, String name) {
        super(id, name, TYPE, false);
        this.id = id;
        this.name = name;
        this.topLevel = false;
    }

    public List<PickupMode> getPickupModes() {
        return pickupModes;
    }

    public void addPickupMode(PickupMode pickupMode) {
        pickupModes.add(pickupMode);
    }

}
