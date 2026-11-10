package com.techsisters.gatherly.response;

import java.util.ArrayList;
import java.util.List;

import com.techsisters.gatherly.dto.MentorProfileDTO;
import com.techsisters.gatherly.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllMentorsResponse extends ResponseDTO {
    private List<MentorProfileDTO> mentors = new ArrayList<>();
}
