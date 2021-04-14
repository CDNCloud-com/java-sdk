package test.com.qiniu.storage;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.DownloadPrivateCloudUrl;
import com.qiniu.storage.DownloadUrl;
import com.qiniu.util.Auth;
import org.junit.Assert;
import org.junit.Test;
import test.com.qiniu.TestConfig;

import java.net.URLEncoder;

public class DownloadUrlTest {

    @Test
    public void testUrl() {
        TestConfig.TestFile[] files = TestConfig.getTestFileArray();
        for (TestConfig.TestFile file : files) {
            String key = file.getKey();
            String domain = file.getTestDomain();

            String attname = "test_file.jpg";
            String fop = "imageView2/2/w/320/h/480";
            String style = "iphone";
            String styleSeparator = "-";
            String customQueryKey = "customQueryKey";
            String customQueryValue = "customQueryValue";
            try {
                String url = new DownloadUrl(domain, false, key)
                        .setAttname(attname).setFop(fop).setStyle(style, styleSeparator)
                        .addCustomQuery(customQueryKey, customQueryValue)
                        .buildURL();
                String urlExpire = "http://" + domain + "/" + key + styleSeparator + style + "?" + URLEncoder.encode(fop) + "&" + customQueryKey + "=" + customQueryValue + "&attname=" + URLEncoder.encode(attname);
                System.out.println("create url:" + url + " expire url:" + urlExpire);
                Assert.assertEquals("create url:" + url + " expire url:" + urlExpire, urlExpire, url);

                url = new DownloadUrl(domain, true, key)
                        .setAttname(attname).setFop(fop).setStyle(style, styleSeparator)
                        .addCustomQuery(customQueryKey, customQueryValue)
                        .buildURL();
                urlExpire = "https://" + domain + "/" + key + styleSeparator + style + "?" + URLEncoder.encode(fop) + "&" + customQueryKey + "=" + customQueryValue + "&attname=" + URLEncoder.encode(attname);
                System.out.println("create url:" + url + " expire url:" + urlExpire);
                Assert.assertEquals("create url:" + url + " expire url:" + urlExpire, urlExpire, url);
            } catch (QiniuException e) {
                Assert.assertTrue(e.error(), false);
            }
        }
    }

    @Test
    public void testUrlWithDeadline() {
        long deadline = 161394222;
        String key = TestConfig.testKey_na0;
        String domain = TestConfig.testDomain_na0_timeStamp;
        Auth auth = TestConfig.testAuth;

        try {
            String url = new DownloadUrl(domain, false, key).buildURL(auth, deadline);
            String urlExpire = "http://javasdk-na0-timestamp.peterpy.cn/do_not_delete/1.png?e=" + deadline + "&token=";
            System.out.println("create url:" + url + " expire url:" + urlExpire);
            Assert.assertTrue("create url:" + url + " expire url:" + urlExpire, url.contains(urlExpire));

            url = new DownloadUrl(domain, true, key).buildURL(auth, deadline);
            urlExpire = "https://javasdk-na0-timestamp.peterpy.cn/do_not_delete/1.png?e=" + deadline + "&token=";
            System.out.println("create url:" + url + " expire url:" + urlExpire);
            Assert.assertTrue("create url:" + url + " expire url:" + urlExpire, url.contains(urlExpire));
        } catch (QiniuException e) {
            Assert.assertTrue(e.error(), false);
        }
    }

    @Test
    public void testPrivateCloudUrl() {
        TestConfig.TestFile[] files = TestConfig.getTestFileArray();
        for (TestConfig.TestFile file : files) {
            String key = file.getKey();
            String bucket = file.getBucketName();
            String domain = file.getTestDomain();

            String attname = "test_file.jpg";
            String fop = "imageView2/2/w/320/h/480";
            String style = "iphone";
            String styleSeparator = "-";
            try {
                DownloadPrivateCloudUrl downloadUrl = new DownloadPrivateCloudUrl(domain, false, bucket, key, TestConfig.testAccessKey);
                String url = downloadUrl.setAttname(attname).setFop(fop).setStyle(style, styleSeparator).buildURL();
                ;
                String urlExpire = "http://" + domain + "/getfile/" + TestConfig.testAccessKey + "/" + bucket + "/" + key + styleSeparator + style + "?" + URLEncoder.encode(fop) + "&attname=" + URLEncoder.encode(attname);
                System.out.println("create url:" + url + " expire url:" + urlExpire);
                Assert.assertEquals("create url:" + url + " expire url:" + urlExpire, urlExpire, url);

                downloadUrl = new DownloadPrivateCloudUrl(domain, true, bucket, key, TestConfig.testAccessKey);
                url = downloadUrl.setAttname(attname).setFop(fop).setStyle(style, styleSeparator).buildURL();
                urlExpire = "https://" + domain + "/getfile/" + TestConfig.testAccessKey + "/" + bucket + "/" + key + styleSeparator + style + "?" + URLEncoder.encode(fop) + "&attname=" + URLEncoder.encode(attname);
                System.out.println("create url:" + url + " expire url:" + urlExpire);
                Assert.assertEquals("create url:" + url + " expire url:" + urlExpire, urlExpire, url);


                Configuration cfg = new Configuration();
                cfg.useHttpsDomains = false;
                String host = cfg.ioHost(TestConfig.testAccessKey, bucket);


                downloadUrl = new DownloadPrivateCloudUrl(cfg, bucket, key, TestConfig.testAccessKey);
                url = downloadUrl.setAttname(attname).setFop(fop).setStyle(style, styleSeparator).buildURL();
                urlExpire = host + "/getfile/" + TestConfig.testAccessKey + "/" + bucket + "/" + key + styleSeparator + style + "?" + URLEncoder.encode(fop) + "&attname=" + URLEncoder.encode(attname);
                System.out.println("create url:" + url + " expire url:" + urlExpire);
                Assert.assertEquals("create url:" + url + " expire url:" + urlExpire, urlExpire, url);


                cfg.useHttpsDomains = true;
                host = cfg.ioHost(TestConfig.testAccessKey, bucket);
                downloadUrl = new DownloadPrivateCloudUrl(cfg, bucket, key, TestConfig.testAccessKey);
                url = downloadUrl.setAttname(attname).setFop(fop).setStyle(style, styleSeparator).buildURL();
                urlExpire = host + "/getfile/" + TestConfig.testAccessKey + "/" + bucket + "/" + key + styleSeparator + style + "?" + URLEncoder.encode(fop) + "&attname=" + URLEncoder.encode(attname);
                System.out.println("create url:" + url + " expire url:" + urlExpire);
                Assert.assertEquals("create url:" + url + " expire url:" + urlExpire, urlExpire, url);
            } catch (QiniuException e) {
                Assert.assertTrue(e.error(), false);
            }
        }
    }
}
