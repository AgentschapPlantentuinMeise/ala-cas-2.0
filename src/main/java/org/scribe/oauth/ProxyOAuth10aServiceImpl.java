package org.scribe.oauth;

import java.util.Map;
import java.util.Map.Entry;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.BaseStringExtractor;
import org.scribe.extractors.HeaderExtractor;
import org.scribe.extractors.RequestTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.ProxyOAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.services.SignatureService;
import org.scribe.services.TimestampService;
import org.scribe.utils.MapUtils;

public class ProxyOAuth10aServiceImpl
        extends OAuth10aServiceImpl
{
    protected final DefaultApi10a api;
    protected final OAuthConfig config;
    protected final int connectTimeout;
    protected final int readTimeout;
    protected final String proxyHost;
    protected final int proxyPort;

    public ProxyOAuth10aServiceImpl(DefaultApi10a api, OAuthConfig config, int connectTimeout, int readTimeout, String proxyHost, int proxyPort)
    {
        super(api, config);
        this.api = api;
        this.config = config;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    public Token getRequestToken()
    {
        this.config.log("obtaining request token from " + this.api.getRequestTokenEndpoint());

        OAuthRequest request = new ProxyOAuthRequest(this.api.getRequestTokenVerb(), this.api.getRequestTokenEndpoint(), this.connectTimeout, this.readTimeout, this.proxyHost, this.proxyPort);

        this.config.log("setting oauth_callback to " + this.config.getCallback());
        request.addOAuthParameter("oauth_callback", this.config.getCallback());
        addOAuthParams(request, OAuthConstants.EMPTY_TOKEN);
        appendSignature(request);

        this.config.log("sending request...");
        Response response = request.send();
        String body = response.getBody();

        this.config.log("response status code: " + response.getCode());
        this.config.log("response body: " + body);
        return this.api.getRequestTokenExtractor().extract(body);
    }

    private void addOAuthParams(OAuthRequest request, Token token)
    {
        request.addOAuthParameter("oauth_timestamp", this.api.getTimestampService().getTimestampInSeconds());
        request.addOAuthParameter("oauth_nonce", this.api.getTimestampService().getNonce());
        request.addOAuthParameter("oauth_consumer_key", this.config.getApiKey());
        request.addOAuthParameter("oauth_signature_method", this.api.getSignatureService().getSignatureMethod());
        request.addOAuthParameter("oauth_version", getVersion());
        if (this.config.hasScope()) {
            request.addOAuthParameter("scope", this.config.getScope());
        }
        request.addOAuthParameter("oauth_signature", getSignature(request, token));

        this.config.log("appended additional OAuth parameters: " + MapUtils.toString(request.getOauthParameters()));
    }

    public Token getAccessToken(Token requestToken, Verifier verifier)
    {
        this.config.log("obtaining access token from " + this.api.getAccessTokenEndpoint());

        ProxyOAuthRequest request = new ProxyOAuthRequest(this.api.getAccessTokenVerb(), this.api.getAccessTokenEndpoint(), this.connectTimeout, this.readTimeout, this.proxyHost, this.proxyPort);

        request.addOAuthParameter("oauth_token", requestToken.getToken());
        request.addOAuthParameter("oauth_verifier", verifier.getValue());

        this.config.log("setting token to: " + requestToken + " and verifier to: " + verifier);
        addOAuthParams(request, requestToken);
        appendSignature(request);
        Response response = request.send();
        return this.api.getAccessTokenExtractor().extract(response.getBody());
    }

    public void signRequest(Token token, OAuthRequest request)
    {
        this.config.log("signing request: " + request.getCompleteUrl());
        if (!token.isEmpty()) {
            request.addOAuthParameter("oauth_token", token.getToken());
        }
        this.config.log("setting token to: " + token);
        addOAuthParams(request, token);
        appendSignature(request);
    }

    private String getSignature(OAuthRequest request, Token token)
    {
        this.config.log("generating signature...");
        String baseString = this.api.getBaseStringExtractor().extract(request);
        String signature = this.api.getSignatureService().getSignature(baseString, this.config.getApiSecret(), token
                .getSecret());

        this.config.log("base string is: " + baseString);
        this.config.log("signature is: " + signature);
        return signature;
    }

    private void appendSignature(OAuthRequest request)
    {
        switch (this.config.getSignatureType())
        {
            case Header:
                this.config.log("using Http Header signature");

                String oauthHeader = this.api.getHeaderExtractor().extract(request);
                request.addHeader("Authorization", oauthHeader);
                break;
            case QueryString:
                this.config.log("using Querystring signature");
                for (Map.Entry<String, String> entry : request.getOauthParameters().entrySet()) {
                    request.addQuerystringParameter((String)entry.getKey(), (String)entry.getValue());
                }
        }
    }
}