package com.skillswap.skillswap.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UpdateProfileRequest {
    public String name;
    public String skillsHave;
    public String skillsWant;
    public String languages;
    public String bio;
    public String location;
    public String avatarBase64;
}
