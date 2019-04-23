package com.cjp.framework.fileupload.fastdfs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.cjp.framework.fileupload.FileServerConfig;
import com.cjp.framework.fileupload.FileUploadClientGroup;

@Configuration
public class FastDfsConfig {
  @Autowired
  private FileServerConfig fileServerConfig;
  @Bean("fastdfsFileUploadClientGroup")
  public FileUploadClientGroup fastDfsClient() {
    FileUploadClientGroup fileUploadClient=new FastDfsClientGroup(fileServerConfig);
    return fileUploadClient;
  }
}
