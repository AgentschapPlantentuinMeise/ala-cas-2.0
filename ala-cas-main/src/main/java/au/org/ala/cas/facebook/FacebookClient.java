package au.org.ala.cas.facebook;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.util.CommonHelper;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.oauth.StateOAuth20ServiceImpl;

/**
 * Extend existing Facebook client to parse new Facebook OAuth JSON tokens.  Could
 * be removed on a pac4j / scribe update
 */
public class FacebookClient extends org.pac4j.oauth.client.FacebookClient {

    @Override
    protected void internalInit() {
        super.internalInit();
        CommonHelper.assertNotBlank("fields", this.fields);
        this.api20 = new ExtendedExtendedFacebookApi(); // This is the only changed line from the existing FacebookClient
        if (StringUtils.isNotBlank(this.scope)) {
            this.service = new StateOAuth20ServiceImpl(this.api20, new OAuthConfig(this.key, this.secret,
                    this.callbackUrl,
                    SignatureType.Header, this.scope,
                    null), this.connectTimeout,
                    this.readTimeout, this.proxyHost, this.proxyPort);
        } else {
            this.service = new StateOAuth20ServiceImpl(this.api20, new OAuthConfig(this.key, this.secret,
                    this.callbackUrl,
                    SignatureType.Header, null, null),
                    this.connectTimeout, this.readTimeout, this.proxyHost,
                    this.proxyPort);
        }
    }

}
