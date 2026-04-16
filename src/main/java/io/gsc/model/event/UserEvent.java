package io.gsc.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent {
    private String email;
    private ActionType actionType;

    public enum ActionType {
        CREATE, DELETE
    }
}
