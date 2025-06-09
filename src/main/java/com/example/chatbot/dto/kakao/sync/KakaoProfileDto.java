package com.example.chatbot.dto.kakao.sync;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class KakaoProfileDto {
    @JsonProperty("id")
    private long id;

    @JsonProperty("connected_at")
    private String connectedAt;

    @JsonProperty("synched_at")
    private String synchedAt;

    @JsonProperty("properties")
    private Properties properties;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    public static class Properties {
        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("profile_image")
        private String profileImage;

        @JsonProperty("thumbnail_image")
        private String thumbnailImage;
    }

    @Getter
    public static class KakaoAccount {
        @JsonProperty("name_needs_agreement")
        private boolean nameNeedsAgreement;

        @JsonProperty("name")
        private String name;

        @JsonProperty("profile_needs_agreement")
        private boolean profileNeedsAgreement;

        @JsonProperty("profile")
        private Profile profile;

        @JsonProperty("has_email")
        private boolean hasEmail;

        @JsonProperty("email_needs_agreement")
        private boolean emailNeedsAgreement;

        @JsonProperty("is_email_valid")
        private boolean isEmailValid;

        @JsonProperty("is_email_verified")
        private boolean isEmailVerified;

        @JsonProperty("email")
        private String email;

        @JsonProperty("has_phone_number")
        private boolean hasPhoneNumber;

        @JsonProperty("phone_number_needs_agreement")
        private boolean phoneNumberNeedsAgreement;

        @JsonProperty("phone_number")
        private String phoneNumber;

        @JsonProperty("has_age_range")
        private boolean hasAgeRange;

        @JsonProperty("age_range_needs_agreement")
        private boolean ageRangeNeedsAgreement;

        @JsonProperty("age_range")
        private String ageRange;

        @JsonProperty("has_birthyear")
        private boolean hasBirthyear;

        @JsonProperty("birthyear_needs_agreement")
        private boolean birthyearNeedsAgreement;

        @JsonProperty("birthyear")
        private String birthyear;

        @JsonProperty("has_birthday")
        private boolean hasBirthday;

        @JsonProperty("birthday_needs_agreement")
        private boolean birthdayNeedsAgreement;

        @JsonProperty("birthday")
        private String birthday;

        @JsonProperty("birthday_type")
        private String birthdayType;

        @JsonProperty("has_gender")
        private boolean hasGender;

        @JsonProperty("gender_needs_agreement")
        private boolean genderNeedsAgreement;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("has_ci")
        private boolean hasCi;

        @JsonProperty("ci_needs_agreement")
        private boolean ciNeedsAgreement;

        @JsonProperty("ci")
        private String ci;

        @JsonProperty("ci_authenticated_at")
        private String ciAuthenticatedAt;
    }

    @Getter
    public static class Profile {
        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("thumbnail_image_url")
        private String thumbnailImageUrl;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;

        @JsonProperty("is_default_image")
        private boolean isDefaultImage;

        @JsonProperty("is_default_nickname")
        private boolean isDefaultNickname;
    }

}
