package com.cjp.framework.fileupload.oss;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.cjp.framework.fileupload.FileObject;
import com.cjp.framework.fileupload.FileUploadClient;
import com.cjp.framework.fileupload.FileUploadClientGroup;

class OssClientGroup implements FileUploadClientGroup{
  private volatile Map<String,OssClient> clientMap=new HashMap<>();
  private final String endpoint;
  private final String accessKeyId;
  private final String secretAccessKey;

  public OssClientGroup(String endpoint,String[] bucketNameArray, String accessKeyId, String secretAccessKey) {
    this.endpoint=endpoint;
    this.accessKeyId=accessKeyId;
    this.secretAccessKey=secretAccessKey;
    Stream.of(bucketNameArray).forEach(bucketName->{
      clientMap.put(bucketName, new OssClient(endpoint,bucketName,accessKeyId,secretAccessKey));
    });
  }

  static class OssClient implements FileUploadClient{
    private final String style = "image/resize,m_lfit,w_%s,h_%s";
    private OSSClient ossClient;
    private String groupName;
    private OssClient(String endpoint,DefaultCredentialProvider credsProvider) {
      ossClient=new OSSClient(endpoint, credsProvider,null);
    }
    public OssClient(String endpoint,String groupName, String accessKeyId, String secretAccessKey) {
      this(endpoint, new DefaultCredentialProvider(accessKeyId,secretAccessKey));
      this.groupName=groupName;
    }
    @Override
    public FileObject upload(File file, String key) throws Exception {
      ossClient.putObject(groupName, key, file);
      return new FileObject() {
        
        @Override
        public String key() {
          return key;
        }

        @Override
        public String url() {
          return null;
        }
      };
    }
    @Override
    public void delete(String key) throws Exception {
      ossClient.deleteObject(groupName, key);
    }

    @Override
    public String getFileUrl(String key) throws Exception {
      Calendar expireDate = Calendar.getInstance();
      expireDate.add(Calendar.MINUTE, 10);
      return ossClient.generatePresignedUrl(groupName, key,expireDate.getTime()).toURI().toURL().toString();
    }
    @Override
    public String getFileUrl(String key,int width,int height) throws Exception {
      GeneratePresignedUrlRequest req = new  GeneratePresignedUrlRequest(this.groupName, key);
      req.setProcess(String.format(style, width,height));
      URL url = ossClient.generatePresignedUrl(req);
      return url.toURI().toURL().toString();
    }
  }
  
  private Lock lock=new ReentrantReadWriteLock().writeLock();

  @Override
  public FileUploadClient get(String groupName) {
    if(clientMap.get(groupName)==null) {
      try {
        lock.lock();
        if(clientMap.get(groupName)==null) {
          clientMap.put(groupName, new OssClient(endpoint,groupName,accessKeyId,secretAccessKey));
        }
      }finally {
        lock.unlock();
      }
    }
    return clientMap.get(groupName);
  }
}
