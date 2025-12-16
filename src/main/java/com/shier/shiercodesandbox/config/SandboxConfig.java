package com.shier.shiercodesandbox.config;

import com.shier.shiercodesandbox.CodeSandBox;
import com.shier.shiercodesandbox.template.JavaDockerCodeSandbox;
import com.shier.shiercodesandbox.template.JavaNativeCodeSandbox;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 沙箱配置类 - 支持本地和 Docker 两种运行模式
 *
 * @author Shier
 */
@Configuration
@EnableConfigurationProperties(SandboxConfig.SandboxProperties.class)
public class SandboxConfig {

  /**
   * 根据配置选择使用的沙箱实现
   */
  @Bean
  public CodeSandBox codeSandBox(SandboxProperties sandboxProperties) {
    if ("docker".equalsIgnoreCase(sandboxProperties.getMode())) {
      return new JavaDockerCodeSandbox();
    } else {
      return new JavaNativeCodeSandbox();
    }
  }

  /**
   * 沙箱配置属性
   */
  @ConfigurationProperties(prefix = "sandbox")
  public static class SandboxProperties {
    /**
     * 运行模式: native 或 docker
     */
    private String mode = "native";

    public String getMode() {
      return mode;
    }

    public void setMode(String mode) {
      this.mode = mode;
    }
  }
}
