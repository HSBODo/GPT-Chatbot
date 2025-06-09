package com.example.chatbot.domain.member.repository;

import com.example.chatbot.domain.member.Member;
import com.example.chatbot.domain.member.constant.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findById(String id);

    Optional<Member> findByIdAndRole(String id, MemberRole role);

    List<Member> findAllByRole(MemberRole role);

    void deleteById(String id);
}
