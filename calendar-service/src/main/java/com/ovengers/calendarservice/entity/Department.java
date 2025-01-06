package com.ovengers.calendarservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_department")
public class Department {

    @Id
    @Column(name = "department_id", nullable = false, length = 255)
    private String departmentId; // 부서 아이디

    @Column(name = "name", length = 30)
    private String name; // 부서 이름

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DepartmentType type; // 부서 유형 (ENUM)

    @ManyToOne(fetch = FetchType.LAZY) // 상위 부서 (Parent)
    @JoinColumn(name = "parent_id")
    private Department parent; // 상위 부서를 참조

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true) // 하위 부서 (Children)
    private List<Department> children; // 하위 부서를 참조

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일자

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일자

    public enum DepartmentType {
        TEAM, DIVISION, GROUP, OTHER
    }
}
