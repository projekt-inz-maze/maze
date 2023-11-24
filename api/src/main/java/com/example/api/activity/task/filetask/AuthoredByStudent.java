package com.example.api.activity.task.filetask;


import com.example.api.activity.submittask.SubmitTask;
import com.example.api.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class AuthoredByStudent {
    @TableGenerator(name = "myGen", table = "ID_GEN", pkColumnName = "GEN_KEY", valueColumnName = "GEN_VALUE", pkColumnValue = "NEXT_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "myGen")
    @Id
    private Long id;

    @ManyToOne
    private User author;

    @ManyToOne
    private SubmitTask submitTask;
}
