package uk.gov.ons.census.caseapisvc.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

class ApplicationYamlTest {

  @Test
  void shouldEnableJacksonCompatibilityDefaultsAndHealthProbes() {
    YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
    yaml.setResources(new ClassPathResource("application.yml"));

    Properties properties = yaml.getObject();

    assertThat(properties)
        .isNotNull()
        .extractingByKey("spring.jackson.use-jackson2-defaults")
        .isEqualTo(true);
    assertThat(properties)
        .extractingByKey("management.endpoint.health.probes.enabled")
        .isEqualTo(true);
  }
}
