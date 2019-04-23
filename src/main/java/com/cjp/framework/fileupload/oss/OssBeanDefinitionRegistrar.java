package com.cjp.framework.fileupload.oss;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
public class OssBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar{
 
  @Autowired
  private static String[] bucketNameArray;
  
  @Override
  public void registerBeanDefinitions(AnnotationMetadata metadata,
      BeanDefinitionRegistry registry) {
    AnnotationAttributes attributes = AnnotationAttributes.fromMap(
        metadata.getAnnotationAttributes(EnableOss.class.getName()));
    OssBeanDefinitionRegistrar.bucketNameArray=attributes.getStringArray("bucketName");
  }

  public static String[] getBucketNameArray() {
    return bucketNameArray;
  }
}
