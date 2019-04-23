package com.cjp.framework.fileupload;

import java.io.File;

public interface FileUploadClient {
  /**
         * 上传文件
   * @param file
   * @param key
   * @return
   * @throws Exception
   */
  public FileObject upload(File file,  String key) throws Exception;
  /**
       * 删除文件
   * @param key
   * @throws Exception
   */
  public void delete(String key)throws Exception;
  /**
   * 获取文件URL
   * @param key
   * @return
   * @throws Exception
   */
  public String getFileUrl(String key) throws Exception;
  /**
   * 获取文件（缩略图）URL
   * @param key
   * @return
   * @throws Exception
   */
  public String getFileUrl(String key, int width, int height) throws Exception;
}
