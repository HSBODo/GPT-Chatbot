package com.example.chatbot.domain.member.service;

import com.example.chatbot.domain.member.Member;
import com.example.chatbot.domain.member.constant.MemberRole;
import com.example.chatbot.domain.member.dto.MemberDTO;

import java.util.List;

public interface MemberService {
    Member getMemberById(String id);

    List<MemberDTO> getMembersByRole(MemberRole role);

    MemberDTO join(MemberDTO memberDTO);

    MemberDTO kakaoJoin(String name, String phone, String userKey);

    MemberDTO update(MemberDTO memberDTO);

    boolean duplicationMember(String id);

    void delete(String id);

    void addReferralCode(String memberId, String referralCode);

    int availableReferralCode(String memberId);

    boolean isDuplicateReferralCode(String memberId, String referralCode);
}
