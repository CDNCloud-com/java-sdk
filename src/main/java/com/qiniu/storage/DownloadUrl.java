package com.qiniu.storage;

import com.qiniu.common.QiniuException;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DownloadUrl {

    private String domain;
    private boolean useHttps = false;
    private String key;
    private Auth auth;
    private Long deadline;
    private String style;
    private String styleSeparator;
    private String fop;
    private String attname;
    private List<Api.Request.Pair<String, String>> customQuerys = new ArrayList<>();

    protected void setDomain(String domain) {
        this.domain = domain;
    }

    protected String getDomain() {
        return domain;
    }

    public DownloadUrl setAttname(String attname) {
        this.attname = attname;
        return this;
    }

    public DownloadUrl setStyle(String style, String styleSeparator) {
        this.style = style;
        this.styleSeparator = styleSeparator;
        return this;
    }

    public DownloadUrl setFop(String fop) {
        this.fop = fop;
        return this;
    }

    public DownloadUrl addCustomQuery(String queryName, String queryValue) {
        customQuerys.add(new Api.Request.Pair<String, String>(queryName, queryValue));
        return this;
    }

    public DownloadUrl(String domain, boolean useHttps, String key) {
        this.domain = domain;
        this.useHttps = useHttps;
        this.key = key;
    }

    public String buildURL(Auth auth, long deadline) throws QiniuException {
        this.auth = auth;
        this.deadline = deadline;
        return buildURL();
    }

    public String buildURL() throws QiniuException {
        willBuildUrl();

        Api.Request request = new Api.Request(getUrlPrefix());

        willSetKeyForUrl(request);
        String keyAndStyle = key;
        if (!StringUtils.isNullOrEmpty(style) && !StringUtils.isNullOrEmpty(styleSeparator)) {
            keyAndStyle += styleSeparator + style;
        }
        request.addPathSegment(keyAndStyle);
        didSetKeyForUrl(request);

        if (!StringUtils.isNullOrEmpty(fop)) {
            request.addQueryPair(fop, null);
        }

        for (Api.Request.Pair<String, String> pair : customQuerys) {
            request.addQueryPair(pair.getKey(), pair.getValue());
        }

        if (!StringUtils.isNullOrEmpty(attname)) {
            request.addQueryPair("attname", attname);
        }

        didBuildUrl();

        String url = request.getUrl().toString();
        if (auth != null && deadline != null) {
            url = auth.privateDownloadUrlWithDeadline(url, deadline);
        }
        return url;
    }

    protected void willBuildUrl() throws QiniuException {
        if (StringUtils.isNullOrEmpty(key)) {
            ApiUtils.throwInvalidRequestParamException("key");
        }
    }

    protected void willSetKeyForUrl(Api.Request request) throws QiniuException {
        if (StringUtils.isNullOrEmpty(domain)) {
            ApiUtils.throwInvalidRequestParamException("domain");
        }
    }

    protected void didSetKeyForUrl(Api.Request request) throws QiniuException {
    }

    protected void didBuildUrl() throws QiniuException {
    }

    private String getUrlPrefix() throws QiniuException {
        if (useHttps) {
            return "https://" + domain;
        } else {
            return "http://" + domain;
        }
    }
}
