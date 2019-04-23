package com.cjp.framework.fileupload.fastdfs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import com.cjp.framework.fileupload.FileObject;
import com.cjp.framework.fileupload.FileServerConfig;
import com.cjp.framework.fileupload.FileUploadClient;
import com.cjp.framework.fileupload.FileUploadClientGroup;

class FastDfsClientGroup implements FileUploadClientGroup{
  private Map<String,FastDfsClient> clientMap=new HashMap<>();
  private Lock lock=new ReentrantReadWriteLock().writeLock();
  
  @Override
  public FileUploadClient get(String groupName) {
    if(clientMap.get(groupName)==null) {
      try {
        lock.lock();
        if(clientMap.get(groupName)==null) {
          clientMap.put(groupName, new FastDfsClient(groupName));
        }
      }finally {
        lock.unlock();
      }
    }
    return clientMap.get(groupName);
  }
  
  private final FileServerConfig fileServerConfig;
  public static String trackerNginxUrl;
  public FastDfsClientGroup(FileServerConfig fileServerConfig) {
    this.fileServerConfig=fileServerConfig;
    FastDfsClientGroup.trackerNginxUrl=fileServerConfig.properties().getProperty("fastdfs.tracker.nginx_url");
  }
  
  @PostConstruct
  public void initFastdfs() {
    try {
      ClientGlobal.initByProperties(fileServerConfig.properties());
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("init fastdfs");
  }

  static class FastDfsClient implements FileUploadClient{
    private final String groupName;
    public FastDfsClient(String groupName) {
      this.groupName=groupName;
    }
    
    private StorageClient createFastDfsClient() throws IOException {
      TrackerClient trackerClient = new TrackerClient();
      TrackerServer trackerServer = trackerClient.getConnection();
      StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
      StorageClient storageClient = new StorageClient(trackerServer, storageServer);
      return storageClient;
    }

    @Override
    public FileObject upload(File file,String key) throws Exception {
      StorageClient storageClient = createFastDfsClient();
      int index=file.getName().lastIndexOf(".");
      String ext="";
      if(index!=-1) {
        ext=file.getName().substring(file.getName().lastIndexOf(".")+1);
      }
      byte[] fileBytes=FileUtils.readFileToByteArray(file);
      String[] uploadResults = storageClient.upload_file(groupName,fileBytes, ext, null);
      if (uploadResults != null && uploadResults.length == 2) {
        String remoteFileName = uploadResults[1];
        String fileAbsolutePath =
            String.format("%s/%s/%s", trackerNginxUrl, groupName, remoteFileName);
        return new FileObject() {
          
          @Override
          public String url() {
            return fileAbsolutePath;
          }

          @Override
          public String key() {
            return null;
          }
        };
      }
      return null;
    }

    @Override
    public void delete(String key) throws Exception {
      StorageClient storageClient = createFastDfsClient();
      storageClient.delete_file(groupName, key);
    }

    @Override
    public String getFileUrl(String key) throws Exception {
      return String.format("%s/%s/%s", trackerNginxUrl,groupName, key);
    }

    @Override
    public String getFileUrl(String key, int width, int height) throws Exception {
      return getFileUrl(key);
    }
  }
}
