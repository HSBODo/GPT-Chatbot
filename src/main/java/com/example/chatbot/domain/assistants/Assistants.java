package com.example.chatbot.domain.assistants;

import com.example.chatbot.domain.BaseEntity;
import com.example.chatbot.domain.assistants.dto.AssistantsDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "Assistants")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Assistants extends BaseEntity {
    @Column(unique = true)
    private String id;
    private String name;
    @Column(columnDefinition = "LONGTEXT")
    private String prompt;
    private String model;
    @Builder
    public Assistants(String id, String name, String prompt, String model) {
        this.id = id;
        this.name = name;
        this.prompt = prompt;
        this.model = model;
    }

    public AssistantsDto toDto() {
        return AssistantsDto.builder()
                .id(id)
                .name(name)
                .model(model)
                .prompt(prompt)
                .build();
    }

    public void updatePrompt(String newPrompt) {
        this.prompt = newPrompt;
    }
}