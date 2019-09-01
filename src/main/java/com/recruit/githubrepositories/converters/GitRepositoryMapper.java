package com.recruit.githubrepositories.converters;

import com.recruit.githubrepositories.api.dto.GithubRepositoryDetails;
import com.recruit.githubrepositories.api.dto.response.RepositoryDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GitRepositoryMapper {

    GitRepositoryMapper INSTANCE = Mappers.getMapper( GitRepositoryMapper.class );

    @Mappings({
            @Mapping(source = "full_name", target = "fullName"),
            @Mapping(source = "created_at", target = "createdAt"),
            @Mapping(source = "stargazers_count", target = "stars"),
            @Mapping(source = "clone_url", target = "cloneUrl"),
    })
    RepositoryDetails githubRepositoryDetailsToRepositoryDetails(GithubRepositoryDetails githubRepositoryDetails);

}
