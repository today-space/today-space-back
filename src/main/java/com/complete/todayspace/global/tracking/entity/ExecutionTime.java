package com.complete.todayspace.global.tracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "table_execution_time")
@Getter
@NoArgsConstructor
public class ExecutionTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String methodName;

    @Column
    private Long time;

    public ExecutionTime(String methodName, Long time) {
        this.methodName = methodName;
        this.time = time;
    }

}
