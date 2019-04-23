package com.cjp.framework.fileupload.oss;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import com.cjp.framework.fileupload.FileServerConfig;
import com.cjp.framework.fileupload.FileUploadClientGroup;
@Configuration
public class OssConfig {
  @Autowired
  private FileServerConfig fileServerConfig;
  @Bean("ossFileUploadClientGroup")
  public FileUploadClientGroup ossClient() {
    Properties properties=fileServerConfig.properties();
    String endpoint=properties.getProperty("aliyuncs.sh.endpoint");
    Assert.isNull(endpoint,"properties aliyuncs.sh.endpoint is null");
    String accessKeyId=properties.getProperty("aliyuncs.sh.accessKeyId");
    Assert.isNull(accessKeyId,"properties aliyuncs.sh.accessKeyId is null");
    String secretAccessKey=properties.getProperty("aliyuncs.sh.secretAccessKey");
    Assert.isNull(secretAccessKey,"properties aliyuncs.sh.secretAccessKey is null");
    FileUploadClientGroup fileUploadClientGroup=new OssClientGroup(endpoint,OssBeanDefinitionRegistrar.getBucketNameArray(),accessKeyId, secretAccessKey);
    return fileUploadClientGroup;
  }
}
