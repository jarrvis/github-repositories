package com.recruit.githubrepositories.api.dto.response;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryDetails implements Serializable {

    private static final long serialVersionUID = 8269473897901856974L;

    public static final int MAX_LENGTH_NAME = 100;

    @NotEmpty
    @Size(max = MAX_LENGTH_NAME)
    private String fullName;

    private String description;

    @URL
    private String cloneUrl;

    @Min(value = 0)
    private Integer stars;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdAt;
}
