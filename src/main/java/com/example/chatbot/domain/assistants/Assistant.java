package com.example.chatbot.domain.assistants;

import com.example.chatbot.domain.BaseEntity;
import com.example.chatbot.domain.assistants.dto.AssistantDto;
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
public class Assistant extends BaseEntity {
    private String assistantId;
    private String name;
    private String title;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String prompt;
    private String model;
    @Builder
    public Assistant(String assistantId, String name, String title, String prompt, String model) {
        this.assistantId = assistantId;
        this.name = name;
        this.title = title;
        this.prompt = prompt;
        this.model = model;
    }

    public AssistantDto toDto() {
        return AssistantDto.builder()
                .id(getUuid().toString())
                .assistantId(assistantId)
                .name(name)
                .title(title)
                .prompt(prompt)
                .model(model)
                .createDate(getCreateDate())
                .build();
    }

    public void modifyTitle(String title) {
        this.title = title;
    }

    public void modifyPrompt(String prompt) {
        this.prompt = prompt;
    }
}
