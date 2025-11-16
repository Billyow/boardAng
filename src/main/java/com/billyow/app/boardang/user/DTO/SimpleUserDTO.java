package com.billyow.app.boardang.user.DTO;

public record SimpleUserDTO(
        // Using id this time for consistency with Mongo
        String id,
        String name,
        String email,
        Boolean isActive
        ){
}
