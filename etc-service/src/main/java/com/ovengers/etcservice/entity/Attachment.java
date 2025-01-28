package com.ovengers.etcservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_attachment")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "file_url")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column
    private Type type;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "task_id")
    private String taskId;

    public enum Type {
        MESSAGE, TASK
    }
}


