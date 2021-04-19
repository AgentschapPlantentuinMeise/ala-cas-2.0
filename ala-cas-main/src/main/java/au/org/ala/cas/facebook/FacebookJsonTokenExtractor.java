package au.org.ala.cas.facebook;

import org.scribe.exceptions.OAuthException;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.Token;
import org.scribe.utils.Preconditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FacebookJsonTokenExtractor implements AccessTokenExtractor {


    private static final Pattern ACCESS_TOKEN_REGEX_PATTERN = Pattern.compile("\"access_token\"\\s*:\\s*\"(\\S*?)\"");
    private static final Pattern TOKEN_TYPE_REGEX_PATTERN = Pattern.compile("\"token_type\"\\s*:\\s*\"(\\S*?)\"");
    private static final Pattern EXPIRES_IN_REGEX_PATTERN = Pattern.compile("\"expires_in\"\\s*:\\s*\"?(\\d*?)\"?\\D");
    private static final Pattern REFRESH_TOKEN_REGEX_PATTERN = Pattern.compile("\"refresh_token\"\\s*:\\s*\"(\\S*?)\"");
    private static final Pattern SCOPE_REGEX_PATTERN = Pattern.compile("\"scope\"\\s*:\\s*\"(\\S*?)\"");
    private static final Pattern ERROR_REGEX_PATTERN = Pattern.compile("\"error\"\\s*:\\s*\"(\\S*?)\"");
    private static final Pattern ERROR_DESCRIPTION_REGEX_PATTERN
            = Pattern.compile("\"error_description\"\\s*:\\s*\"([^\"]*?)\"");
    private static final Pattern ERROR_URI_REGEX_PATTERN = Pattern.compile("\"error_uri\"\\s*:\\s*\"(\\S*?)\"");

    private static final String EMPTY_SECRET = "";

    private Token createToken(String response) {
        final String accessToken = extractParameter(response, ACCESS_TOKEN_REGEX_PATTERN, true);
        final String tokenType = extractParameter(response, TOKEN_TYPE_REGEX_PATTERN, false);
        final String expiresInString = extractParameter(response, EXPIRES_IN_REGEX_PATTERN, false);
        Integer expiresIn;
        try {
            expiresIn = expiresInString == null ? null : Integer.valueOf(expiresInString);
        } catch (NumberFormatException nfe) {
            expiresIn = null;
        }
        final String refreshToken = extractParameter(response, REFRESH_TOKEN_REGEX_PATTERN, false);
        final String scope = extractParameter(response, SCOPE_REGEX_PATTERN, false);

        return createToken(accessToken, tokenType, expiresIn, refreshToken, scope, response);
    }

    protected Token createToken(String accessToken, String tokenType, Integer expiresIn,
                                            String refreshToken, String scope, String response) {
        return new Token(accessToken, EMPTY_SECRET, response);
    }

    protected static String extractParameter(String response, Pattern regexPattern, boolean required)
            throws OAuthException {
        final Matcher matcher = regexPattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1);
        }

        if (required) {
            throw new OAuthException("Response body is incorrect. Can't extract a '" + regexPattern.pattern()
                    + "' from this: '" + response + "'", null);
        }

        return null;
    }

    @Override
    public Token extract(String response) {
        Preconditions.checkEmptyString(response,
                "Response body is incorrect. Can't extract a token from an empty string");

        return createToken(response);
    }
}
