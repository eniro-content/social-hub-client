package com.eniro.content.social.client.model;

import com.eniro.content.social.client.constants.Country;
import com.eniro.content.social.client.constants.Source;


import java.util.List;

/**
 * Created by andrew on 26/08/15.
 */
public class SocialData {
    private long ecoId;
    private Country country;
    private Source source;
    private List<String> externalId;
    private Object data;
    private long updateTime;
    private boolean autoMapping;
    private long version;

    public SocialData() {
        this.updateTime = System.currentTimeMillis();
        this.autoMapping = true;
    }

    public SocialData(long ecoId, Country country, Source source, List<String> externalId, Object data) {
        this.ecoId = ecoId;
        this.country = country;
        this.source = source;
        this.externalId = externalId;
        this.data = data;
        this.updateTime = System.currentTimeMillis();
        this.autoMapping = true;
    }

    public SocialData(SocialData orig,Object data) {
        this.ecoId = orig.ecoId;
        this.country = orig.country;
        this.source = orig.source;
        this.externalId = orig.externalId;
        this.updateTime = orig.updateTime;
        this.autoMapping = orig.autoMapping;
        this.data = data;
    }

    public Source getSource() {
        return source;
    }

    public void expire() {
        updateTime = 1;
    }

    public long getEcoId() {
        return ecoId;
    }

    public void setEcoId(long ecoId) {
        this.ecoId = ecoId;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public List<String> getExternalId() {
        return externalId;
    }

    public void setExternalId(List<String> externalId) {
        this.externalId = externalId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isAutoMapping() {
        return autoMapping;
    }

    public void setAutoMapping(boolean autoMapping) {
        this.autoMapping = autoMapping;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
