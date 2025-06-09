package com.example.chatbot.domain.member;

import com.example.chatbot.common.PasswordUtil;
import com.example.chatbot.domain.BaseEntity;
import com.example.chatbot.domain.member.constant.MemberRole;
import com.example.chatbot.domain.member.dto.MemberDTO;
import com.example.chatbot.domain.member.referral.ReferralCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)

    @JoinColumn(name = "member_id")
    private List<ReferralCode> referralCodes = new ArrayList<>();

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

    public void addReferralCode(ReferralCode referralCode) {
        this.referralCodes.add(referralCode);
    }

    public void updateAlarmTalk(boolean isAlarmTalk) {
        this.isAlarmTalk = isAlarmTalk;
    }

    public void usedReferralCodes() {
        List<ReferralCode> referralCodes = this.referralCodes;

        Optional<ReferralCode> latestCode = referralCodes.stream()
                .max(Comparator.comparing(BaseEntity::getCreateDate));

        latestCode.ifPresent(referralCode -> {
            referralCode.use(); // 사용 처리
        });

    }

    public List<ReferralCode> availableReferralCodes() {
        return this.referralCodes.stream()
                .filter(code -> !code.isUseCode()) // 사용되지 않은 코드만 필터링
                .collect(Collectors.toList());
    }
}