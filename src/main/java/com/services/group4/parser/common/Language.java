package com.services.group4.parser.common;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;
import lombok.Generated;
import lombok.Getter;

@Generated
@Embeddable
@Data
@Getter
public class Language {
  @NotBlank private String langName;

  @NotBlank private List<String> version;

  public Language() {}

  public Language(String language, List<String> version) {
    this.langName = language.toLowerCase();
    this.version = version;
  }
}
