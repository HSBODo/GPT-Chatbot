package com.example.chatbot.domain.member.referral;

import com.example.chatbot.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "referral_code")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReferralCode extends BaseEntity {

    @Column(nullable = false)
    private String registrant; // 추천인 코드

    @Column(nullable = false)
    private String code; // 추천인 코드

    @Column(nullable = false)
    private boolean isUseCode;

    private LocalDateTime usedAt;

    @Builder
    public ReferralCode(String registrant, String code) {
        this.registrant = registrant;
        this.code = code;
        this.isUseCode = false;
    }

    public void use() {
        this.isUseCode = true;
        this.usedAt = LocalDateTime.now();
    }
}
