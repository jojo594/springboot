package com.proje.login.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class MovieSearchResultDTO {

    // Getters and Setters
    private int page;
    
    private List<MovieDTO> results;
    
    @JsonProperty("total_results")
    private int totalResults;
    
    @JsonProperty("total_pages")
    private int totalPages;

}