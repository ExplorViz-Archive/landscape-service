package net.explorviz.landscape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.quarkus.jackson.ObjectMapperCustomizer;
import javax.inject.Singleton;

/**
 * Jackson configuration
 */
@Singleton
public class RegisterCustomModuleCustomizer implements ObjectMapperCustomizer {

  public void customize(ObjectMapper mapper) {
    mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
  }
}
