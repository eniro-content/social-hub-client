package com.eniro.content.social.client;

import com.eniro.content.social.client.constants.Action;
import com.eniro.content.social.client.constants.Country;
import com.eniro.content.social.client.constants.Source;
import com.eniro.content.social.client.model.EcoSocialData;
import com.eniro.content.social.client.util.JsonUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;


/**
 * Project: <social-hu>
 * Created by andrew on 05/01/16.
 */
public class SocialhubClientTest {
    private final SocialhubClient client;

    public SocialhubClientTest() {
        this.client = new SocialhubClient("https://socialhubtest.eniro.com", null, "test");
    }

    @Test
    public void testGet() throws Exception {
        EcoSocialData result = client.read(Country.se, 8231330, Source.Facebook, "$.page.id");
        Assert.assertNotNull(result);
        Assert.assertEquals("134871249877000", JsonUtil.getObjectMapper().convertValue(
                result.getSocialData().get(0).getData(), String.class));
        try {
            client.read(Country.se, 11111, Source.Facebook, "$.page.id");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("Resource not found, ecoId:[11111] country code:se source:[Facebook]"));
        }
    }

    @Test
    public void testGetWithAction() throws Exception {
        EcoSocialData result = client.read(Country.se, 8231330, Source.Facebook, "$.page.id", Action.Ping);
        Assert.assertNull(result);
    }

    @Test
    public void testUpdate() throws Exception {


    }
}