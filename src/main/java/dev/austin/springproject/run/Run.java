package dev.austin.springproject.run;

import java.time.LocalDateTime;

public record Run (
    Integer id,
    String title,
    LocalDateTime startedOn,
    LocalDateTime completedOn,
    Integer kilometers,
    Location location

){}

