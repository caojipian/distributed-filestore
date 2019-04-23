package com.cjp.framework.fileupload;

public interface FileObject {
  /**
   * 文件唯一ID
   * @return
   */
  public String key();
  /**
   * 文件URL地址
   * @return
   */
  public String url();
}
