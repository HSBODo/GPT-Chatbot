package com.example.chatbot.dto.kakao.response.property.components;

import com.example.chatbot.dto.kakao.response.property.common.Button;
import com.example.chatbot.dto.kakao.response.property.common.Profile;
import com.example.chatbot.dto.kakao.response.property.common.Thumbnail;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ItemCard {
    private Thumbnail thumbnail;
    private ImageTitle imageTitle;
    private Profile profile;
    private String itemListAlignment;
    private List<ItemListSummary> itemList = new ArrayList<>();
    private ItemListSummary itemListSummary;
    private String title;
    private String description;
    private List<Button> buttons = new ArrayList<>();
    private String buttonLayout;

    public ItemCard(String itemListAlignment) {
        this.itemListAlignment = itemListAlignment;
    }

    @Getter
    @Setter
    public static class ImageTitle {
        private String title;
        private String description;
    }

    @Getter
    @Setter
    public static class ItemListSummary {
        private String title;
        private String description;
    }

    public void setItemListAlignment(String itemListAlignment) {
        // right, left
        this.itemListAlignment = itemListAlignment;
    }

    public void setItemListSummary(ItemListSummary itemListSummary) {
        this.itemListSummary = itemListSummary;
    }

    public void addItemList(ItemListSummary itemListSummary) {
        this.itemList.add(itemListSummary);
    }

    public void addItemList(String key, String value) {
        ItemListSummary summary = new ItemListSummary();
        summary.setTitle(key);
        summary.setDescription(value);

        this.itemList.add(summary);
    }

    public void setSummary(String key, String value) {
        this.itemListSummary = new ItemListSummary();
        this.itemListSummary.setTitle(key);
        this.itemListSummary.setDescription(value);
    }

    public void addButton(Button button) {
        this.buttons.add(button);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageTitle(String title, String description) {
        ImageTitle imageTitle = new ImageTitle();
        imageTitle.setTitle(title);
        imageTitle.setDescription(description);

        this.imageTitle = imageTitle;
    }
}
