package com.example.chatbot.domain.member;

import com.example.chatbot.common.PasswordUtil;
import com.example.chatbot.domain.BaseEntity;
import com.example.chatbot.domain.member.constant.MemberRole;
import com.example.chatbot.domain.member.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "member")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Column(unique = true)
    private String id;
    private String password;
    private String name;
    private String phone;
    @Enumerated(EnumType.STRING)
    private MemberRole role;
    private boolean isAlarmTalk;

    @Builder
    public Member(String id, String password, String name, String phone, MemberRole role) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.isAlarmTalk = true;
    }

    public static Member create(String id, String password, String name, String phone, MemberRole role) {
        return Member.builder()
                .id(id)
                .password(password)
                .name(name)
                .phone(phone)
                .role(role)
                .build();
    }

    public static Member createKakao(String id, String name, String phone) {
        return Member.builder()
                .id(id)
                .name(name)
                .phone(phone.replaceAll("-", ""))
                .role(MemberRole.MEMBER)
                .build();
    }

    public MemberDTO toDTO() {
        return MemberDTO.builder()
                .id(id)
                .name(name)
                .phone(phone)
                .role(role)
                .createDate(getCreateDate().toLocalDate())
                .build();
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changePhone(String phone) {
        this.phone = phone;
    }

    public void changePassword(String rawPassword) {
        this.password = PasswordUtil.encodePassword(rawPassword);
    }

    public void updateAlarmTalk(boolean isAlarmTalk) {
        this.isAlarmTalk = isAlarmTalk;
    }
}