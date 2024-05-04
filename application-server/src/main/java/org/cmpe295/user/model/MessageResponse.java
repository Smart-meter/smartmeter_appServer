package org.cmpe295.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cmpe295.user.entity.enums.ACTION;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private ACTION action;
    private String message;
}
