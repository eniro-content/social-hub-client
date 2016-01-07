package com.eniro.content.social.client.model;

import com.eniro.content.social.client.constants.Country;

import java.util.Arrays;
import java.util.List;

/**
 * Project: <social-hub>
 * Created by andrew on 19/11/15.
 */
public class EcoSocialData {
    private long ecoId;
    private Country country;
    private List<SocialData> socialData;

    public EcoSocialData() {
        // no init needed, user has to set it properly
    }

    public EcoSocialData(long ecoId, Country country, SocialData... socialData) {
        this.ecoId = ecoId;
        this.country = country;
        this.socialData = Arrays.asList(socialData);
    }

    public EcoSocialData(long ecoId, Country country, List<SocialData> socialData) {
        this.ecoId = ecoId;
        this.country = country;
        this.socialData = socialData;
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

    public List<SocialData> getSocialData() {
        return socialData;
    }

    public void setSocialData(List<SocialData> socialData) {
        this.socialData = socialData;
    }
}

