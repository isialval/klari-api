package com.isidora.klari_api.dto;

import java.util.Set;
import com.isidora.klari_api.model.enums.Goal;
import com.isidora.klari_api.model.enums.SkinType;

public record UserProfileDTO(
        Long id,
        String username,
        String email,
        SkinType skinType,
        Set<Goal> goals) {
}