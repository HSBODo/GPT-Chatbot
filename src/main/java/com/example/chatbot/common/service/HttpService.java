package com.example.chatbot.common.service;

import com.example.chatbot.domain.member.constant.MemberRole;
import com.example.chatbot.domain.member.dto.MemberDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class HttpService {

    public MemberDTO getMemberDTOFromHttpRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        return  (MemberDTO) request.getAttribute("memberInfo");
    }

    public MemberRole getMemberRole() {
        MemberDTO userInfoFromHttpRequest = getMemberDTOFromHttpRequest();

        return userInfoFromHttpRequest.getRole();
    }

    public boolean isAdmin() {
        MemberDTO userInfoFromHttpRequest = getMemberDTOFromHttpRequest();

        MemberRole role = userInfoFromHttpRequest.getRole();

        if (role.getName().equals(MemberRole.ADMIN.getName())) return true;

        return false;
    }

//
//    public void director() throws AuthenticationException {
//        MemberInfoDtoFromJwt userInfoFromHttpRequest = getUserInfoFromHttpRequest();
//
//        MemberRole role = userInfoFromHttpRequest.getRole();
//        if (role.getName().equals(MemberRole.관리자.getName())) return ;
//        if (role.getName().equals(MemberRole.원장님.getName())) return ;
//
//        throw new AuthenticationException("권한이 원장님이 아닙니다.");
//    }
//
//    public void teacher() throws AuthenticationException {
//        MemberInfoDtoFromJwt userInfoFromHttpRequest = getUserInfoFromHttpRequest();
//
//        MemberRole role = userInfoFromHttpRequest.getRole();
//        if (role.getName().equals(MemberRole.선생님.getName())) return ;
//        if (role.getName().equals(MemberRole.관리자.getName())) return ;
//        if (role.getName().equals(MemberRole.원장님.getName())) return ;
//        throw new AuthenticationException("권한이 선생님이 아닙니다.");
//    }
//
//    public boolean isDirector()  {
//        MemberInfoDtoFromJwt userInfoFromHttpRequest = getUserInfoFromHttpRequest();
//
//        MemberRole role = userInfoFromHttpRequest.getRole();
//        if (role.getName().equals(MemberRole.관리자.getName())) return true;
//        if (role.getName().equals(MemberRole.원장님.getName())) return true;
//        return false;
//    }
//
//    public boolean isTeacher() {
//        MemberInfoDtoFromJwt userInfoFromHttpRequest = getUserInfoFromHttpRequest();
//
//        MemberRole role = userInfoFromHttpRequest.getRole();
//        if (role.getName().equals(MemberRole.선생님.getName())) return true;
//        if (role.getName().equals(MemberRole.관리자.getName())) return true;
//        if (role.getName().equals(MemberRole.원장님.getName())) return true;
//        return false;
//    }
//
//    public boolean isStudent() throws AuthenticationException {
//        MemberInfoDtoFromJwt userInfoFromHttpRequest = getUserInfoFromHttpRequest();
//
//        MemberRole role = userInfoFromHttpRequest.getRole();
//        if (role.getName().equals(MemberRole.학생.getName())) return true;
//        throw new AuthenticationException("권한이 학생이 아닙니다.");
//    }
}
