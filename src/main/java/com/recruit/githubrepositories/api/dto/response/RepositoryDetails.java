package com.recruit.githubrepositories.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryDetails implements Serializable {

    private static final long serialVersionUID = 8269473897901856974L;


    private String fullName;
    private String description;
    private String cloneUrl;
    private Integer stars;
    private LocalDate createdAt;
}
