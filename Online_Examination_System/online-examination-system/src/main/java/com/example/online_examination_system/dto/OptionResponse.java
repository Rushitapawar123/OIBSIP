
    
package com.example.online_examination_system.dto;

public record OptionResponse(
        Long id,
        String optionText,
        boolean correct
) {}