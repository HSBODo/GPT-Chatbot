package com.example.chatbot.dto.kakao.response.property.common;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Profile {
    private String nickname;
    private String imageUrl = "https://pointman-file-repository.s3.ap-northeast-2.amazonaws.com/image/profile/icon-friends-ryan.png";
     /**
      * 이미지 사이즈는 180px X 180px 추천합니다.
      **/
    public Profile(String nickname) {
        this.nickname = nickname;
    }

    public Profile(String nickname, String imageUrl) {
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }
    public void changeProfileImage(String imageUrl){
        this.imageUrl = imageUrl;
    }
    public void changeProfileNickname(String nickname){
        this.nickname = nickname;
    }
}
