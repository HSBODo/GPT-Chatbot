package com.example.chatbot.domain.member.service.impl;

import com.example.chatbot.common.PasswordUtil;
import com.example.chatbot.domain.member.Member;
import com.example.chatbot.domain.member.constant.MemberRole;
import com.example.chatbot.domain.member.dto.MemberDTO;
import com.example.chatbot.domain.member.repository.MemberRepository;
import com.example.chatbot.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    @Override
    public Member getMemberById(String id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Member가 존재하지 않습니다 id = " + id));
    }

    @Override
    public List<MemberDTO> getMembersByRole(MemberRole role) {
        return memberRepository.findAllByRole(role).stream()
                .map(Member::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public MemberDTO join(MemberDTO memberDTO) {
        String id = memberDTO.getId();
        String encodedPassword = PasswordUtil.encodePassword(memberDTO.getPassword());
        String name = memberDTO.getName();
        String phone = memberDTO.getPhone()
                .replaceAll("-","");

        Member member = Member.create(id,encodedPassword,name,phone,MemberRole.MEMBER);

        return memberRepository.save(member).toDTO();
    }
    @Transactional
    @Override
    public MemberDTO kakaoJoin(String name, String phone, String userKey) {

        Member member = Member.createKakao(userKey,name,phone);

        return memberRepository.save(member).toDTO();
    }

    @Transactional
    @Override
    public MemberDTO update(MemberDTO memberDTO) {
        String id = memberDTO.getId();
        String name = memberDTO.getName();
        String phone = memberDTO.getPhone();

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다. id = " + id));

        member.changeName(name);
        member.changePhone(phone);

        if (Objects.nonNull(memberDTO.getPassword())) {
            member.changePassword(memberDTO.getPassword());
        }

        return member.toDTO();
    }

    @Override
    public boolean duplicationMember(String id) {
        Optional<Member> maybeMember = memberRepository.findByIdAndRole(id,MemberRole.MEMBER);

        if (maybeMember.isPresent()) {
            return true;
        }

        return false;
    }

    @Transactional
    @Override
    public void delete(String id) {
        memberRepository.deleteById(id);
    }
}
