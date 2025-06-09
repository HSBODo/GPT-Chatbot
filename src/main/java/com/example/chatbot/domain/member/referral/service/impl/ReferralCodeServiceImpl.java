package com.example.chatbot.domain.member.referral.service.impl;

import com.example.chatbot.domain.member.referral.repository.ReferralCodeRepository;
import com.example.chatbot.domain.member.referral.service.ReferralCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReferralCodeServiceImpl implements ReferralCodeService {
    private final ReferralCodeRepository referralCodeRepository;
}
