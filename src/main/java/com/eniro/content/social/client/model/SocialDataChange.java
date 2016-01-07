package com.eniro.content.social.client.model;


import com.eniro.content.social.client.constants.Source;

/**
 * Created by henrik on 20/11/15.
 */
public class SocialDataChange {
    private long ecoId;
    private Source source;
    private long updateTime;
    private long version;

    public long getEcoId() {
        return ecoId;
    }

    public Source getSource() {
        return source;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public long getVersion() {
        return version;
    }
}
