package com.cjp.framework.fileupload.oss;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    String accessKeyId=properties.getProperty("aliyuncs.sh.accessKeyId");
    String secretAccessKey=properties.getProperty("aliyuncs.sh.secretAccessKey");
    FileUploadClientGroup fileUploadClientGroup=new OssClientGroup(endpoint,OssBeanDefinitionRegistrar.getBucketNameArray(),accessKeyId, secretAccessKey);
    return fileUploadClientGroup;
  }
}
