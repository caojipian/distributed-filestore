# Introduction
distributed-filestore是为了文件存储抽象层，提供一个文件存储统一的API，目前只集成了fastdfs和阿里云oss，后续会进行新增，欢迎大家给出建议

# 使用方式
### 使用前提：项目需要是基于spring3.x以上版本，jdk1.8及以上版本
#### 配置
```Java
@Configuration
//如果使用oss
@EnableOss
//如果使用fastdfs
//@EnableFastdfs
public class FileUploadConfig{
  
  //导入配置到组件
  @Bean
  public FileServerConfig fileServerConfig() {
    return new FileServerConfig() {

      @Override
      public Properties properties() {
        Properties properties=new Properties();
        //以下是以apollo为例，使用者只需要把自身项目的配置参数读取并构造一个Properties对象返回即可
        Config config=ConfigService.getConfig("xxx");
        config.getPropertyNames().forEach(property->{
          properties.put(property, config.getProperty(property, ""));
        });
        return properties;
      }
    };
  }
}
```
#### 测试代码
```Java
@SpringBootApplication
public class App 
{
    public static void main( String[] args )
    {
      ApplicationContext context=SpringApplication.run(App.class, args);
      FileUploadClientGroup fileUploadClientGroup=context.getBean(FileUploadClientGroup.class);
      try {
        fileUploadClientGroup.get("123")
        .upload(new File("C:\\Users\\62368\\Desktop\\test.txt"), "cjptest.txt");
        String url=fileUploadClientGroup.get("123").getFileUrl("cjptest.txt");
        System.out.println(url);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
}
```
#### pom.xml中加入依赖
```Xml
<dependency>
	<groupId>com.cjp.framework</groupId>
	<artifactId>fileupload</artifactId>
	<version>1.0</version>
</dependency>
