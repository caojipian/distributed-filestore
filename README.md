# Introduction
fileupload是为了抽象各种文件存储对接的组件模块，目前只集成了fastdfs和阿里云oss，后续会进行新增，欢迎大家给出建议

# 使用方式
### 配置
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
### 测试代码
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