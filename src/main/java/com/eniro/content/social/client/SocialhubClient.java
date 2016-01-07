package com.eniro.content.social.client;

import com.eniro.content.social.client.constants.Action;
import com.eniro.content.social.client.constants.Country;
import com.eniro.content.social.client.constants.Source;
import com.eniro.content.social.client.model.EcoSocialData;
import com.eniro.content.social.client.model.SocialData;
import com.eniro.content.social.client.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Project: <social-hu>
 * Created by andrew on 05/01/16.
 */
public class SocialhubClient {
    private static final Logger LOGGER = Logger.getLogger(SocialhubClient.class.getCanonicalName());
    public static final String SEPARATOR = ",";
    public static final String ENIRO_CALLER = "EniroCaller";
    public static final String DEFAULT_USER_AGENT = "socialhub-client-java";
    public static final String PARAM_QUERY = "query";
    public static final String PARAM_ACTION = "action";
    private final String auth;
    private final String host;
    private final String caller;
    private final HttpClient client;
    private static final String VERSION = "v1";
    private static final String URI_FORMAT = "%s/socialhub/" + VERSION + "/country/%s/eco/%s/socialdata/%s";


    public SocialhubClient(String host) {
        this(host, null);
    }

    public SocialhubClient(String host, String auth) {
        this(host, auth, null);
    }

    public SocialhubClient(String host, String auth, String caller) {
        this.host = host;
        this.auth = auth;
        this.caller = caller;

        this.client = HttpClientBuilder.create()
                .setDefaultHeaders(getDefaultHeaders())
                .build();
    }


    private Collection<Header> getDefaultHeaders() {
        Collection<Header> toReturn = new HashSet<Header>();
        toReturn.add(new BasicHeader(HttpHeaders.AUTHORIZATION, auth));
        toReturn.add(new BasicHeader(ENIRO_CALLER, caller));
        toReturn.add(new BasicHeader(HttpHeaders.USER_AGENT, DEFAULT_USER_AGENT));
        return toReturn;
    }

    public EcoSocialData read(Country country, long ecoId, Source source, String query) throws IOException {
        return read(country, ecoId, source, query, null);
    }

    public EcoSocialData read(Country country, long ecoId, Source source, String query, Action action) throws IOException {
        try {
            URIBuilder uri = new URIBuilder().setPath(buildUri(country, ecoId, source));
            if (!StringUtils.isEmpty(query)) {
                uri.addParameter(PARAM_QUERY, query);
            }
            if (action != null) {
                uri.addParameter(PARAM_ACTION, action.name());
            }
            HttpResponse result = client.execute(new HttpGet(uri.build()));
            if (result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return JsonUtil.getObjectMapper().readValue(result.getEntity().getContent(), EcoSocialData[].class)[0];
            } else if (result.getStatusLine().getStatusCode() > 200 && result.getStatusLine().getStatusCode() < 300) {
                LOGGER.info(responseToString(result));
                return null;
            } else {
                throw new IOException(responseToString(result));
            }
        } catch (IndexOutOfBoundsException e) {
            LOGGER.warning("Can not find item number 0:" + e.getMessage());
            return null;
        } catch (URISyntaxException e) {
            LOGGER.warning("This should never happen:" + e.getMessage());
            return null;
        }
    }

    public EcoSocialData[] read(Country country, Collection<Long> ecoId, Collection<Source> source, String query) throws IOException {
        return read(country, ecoId, source, query, null);
    }

    public EcoSocialData[] read(Country country, Collection<Long> ecoId, Collection<Source> source, String query, Action action) throws IOException {
        try {
            URIBuilder uri = new URIBuilder().setPath(buildUri(country, ecoId, source));
            if (!StringUtils.isEmpty(query)) {
                uri.addParameter(PARAM_QUERY, query);
            }
            if (action != null) {
                uri.addParameter(PARAM_ACTION, action.name());
            }
            HttpResponse result = client.execute(new HttpGet(uri.build()));
            if (result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return JsonUtil.getObjectMapper().readValue(result.getEntity().getContent(), EcoSocialData[].class);
            } else if (result.getStatusLine().getStatusCode() > 200 && result.getStatusLine().getStatusCode() < 300) {
                LOGGER.info(responseToString(result));
                return null;
            } else {
                throw new IOException(responseToString(result));
            }
        } catch (URISyntaxException e) {
            LOGGER.warning("This should never happen:" + e.getMessage());
            return null;
        }
    }

    public void update(SocialData... socialData) throws IOException {
        Map<Long, EcoSocialData> dataMap = new HashMap<Long, EcoSocialData>();
        for (SocialData aSocialData : socialData) {
            if (!dataMap.containsKey(aSocialData.getEcoId())) {
                dataMap.put(aSocialData.getEcoId(), new EcoSocialData(aSocialData.getEcoId(), aSocialData.getCountry()));
            }
            EcoSocialData data = dataMap.get(aSocialData.getEcoId());
            data.getSocialData().add(aSocialData);
        }
        EcoSocialData[] toUpdate = dataMap.values().toArray(new EcoSocialData[dataMap.size()]);
        update(toUpdate);
    }

    public void update(EcoSocialData... toUpdate) throws IOException {
        Country country = null;
        Set<Long> ecoIds = new HashSet<Long>();
        Set<Source> sources = new HashSet<Source>();
        for (EcoSocialData aSocialData : toUpdate) {
            if (country == null) {
                country = aSocialData.getCountry();
            }
            if (country != aSocialData.getCountry()) {
                throw new IllegalArgumentException("All social data should be for the same country.");
            }
        }
        try {
            HttpPost request = new HttpPost(buildUri(country, ecoIds, sources));
            request.setEntity(new StringEntity(JsonUtil.getObjectMapper().writeValueAsString(toUpdate), ContentType.APPLICATION_JSON));
            HttpResponse result = client.execute(request);
            if (result.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new IOException(responseToString(result));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }
    }

    public void delete(Country country, long ecoId, Source source) throws IOException {
        delete(country, ecoId, source, true);
    }


    public void delete(Country country, long ecoId, Source source, boolean tombStone) throws IOException {
        if (tombStone) {
            update(createTombStone(country, ecoId, source));
        } else {
            client.execute(new HttpDelete(buildUri(country, ecoId, source)));
        }
    }

    private String buildUri(Country country, Collection<Long> ecoIds, Collection<Source> sources) {
        return String.format(URI_FORMAT, host, country, StringUtils.join(ecoIds, SEPARATOR), StringUtils.join(sources, SEPARATOR));
    }

    private String buildUri(Country country, Long ecoId, Source source) {
        return String.format(URI_FORMAT, host, country, ecoId, source);
    }

    private String responseToString(HttpResponse response) throws IOException {
        return String.format("Response code: %d\nResponse body: %s",
                response.getStatusLine().getStatusCode(),
                EntityUtils.toString(response.getEntity()));
    }

    private SocialData createTombStone(Country country, long ecoId, Source source) {
        SocialData toReturn = new SocialData(ecoId, country, source, new ArrayList<String>(0), null);
        toReturn.setAutoMapping(false);
        return toReturn;
    }

}
