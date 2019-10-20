package com.jarrvis.githubrepositories.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GithubRepositoryDetails implements Serializable {

    private static final long serialVersionUID = 8269473897901856964L;

    public static final int MAX_LENGTH_NAME = 100;

    @NotEmpty
    @Size(max = MAX_LENGTH_NAME)
    private String full_name;

    private String description;

    @URL
    private String clone_url;

    @Min(value = 0)
    private Integer stargazers_count;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate created_at;
}
