package com.example.chatbot.dto.kakao.sync;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class KakaoAddressDto {
    @JsonProperty("user_id")
    private long userId;

    @JsonProperty("has_shipping_addresses")
    private boolean hasShippingAddresses;

    @JsonProperty("shipping_addresses_needs_agreement")
    private boolean shippingAddressesNeedsAgreement;

    @JsonProperty("shipping_addresses")
    private List<ShippingAddress> shippingAddresses;

    @Getter
    public static class ShippingAddress {

        @JsonProperty("default")
        private boolean isDefault;

        @JsonProperty("id")
        private long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("is_default")
        private boolean isDefaultAddress;

        @JsonProperty("updated_at")
        private long updatedAt;

        @JsonProperty("type")
        private String type;

        @JsonProperty("base_address")
        private String baseAddress;

        @JsonProperty("detail_address")
        private String detailAddress;

        @JsonProperty("receiver_name")
        private String receiverName;

        @JsonProperty("receiver_phone_number1")
        private String receiverPhoneNumber1;

        @JsonProperty("receiver_phone_number2")
        private String receiverPhoneNumber2;

        @JsonProperty("zone_number")
        private String zoneNumber;

        @JsonProperty("zip_code")
        private String zipCode;
    }
}
