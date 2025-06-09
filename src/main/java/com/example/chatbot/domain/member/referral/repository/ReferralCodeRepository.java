package com.example.chatbot.domain.member.referral.repository;

import com.example.chatbot.domain.member.referral.ReferralCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReferralCodeRepository extends JpaRepository<ReferralCode, UUID> {

}
