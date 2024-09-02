package com.kkokkomu.short_news.core.util;

import com.kkokkomu.short_news.core.exception.CommonException;
import com.kkokkomu.short_news.core.exception.ErrorCode;
import com.kkokkomu.short_news.core.type.ECategory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CategoryUtil {
    public ECategory getCategoryByName(String categoryName) {
        ECategory category = null;
        if (Objects.equals(categoryName, "정치")) {
            category = ECategory.POLITICS;
        } else if (Objects.equals(categoryName, "사회")) {
            category = ECategory.SOCIAL;
        } else if (Objects.equals(categoryName, "경제")) {
            category = ECategory.ECONOMY;
        } else if (Objects.equals(categoryName, "생활")) {
            category = ECategory.LIVING;
        } else if (Objects.equals(categoryName, "세계")) {
            category = ECategory.WORLD;
        } else if (Objects.equals(categoryName, "연예")) {
            category = ECategory.ENTERTAIN;
        } else if (Objects.equals(categoryName, "스포츠")) {
            category = ECategory.SPORTS;
        }

        return category;
    }// 카테고리 enum casting

    public List<ECategory> getCategoryList(String category) {
        // 문자열을 ','로 스플릿하여 배열로 분리
        String[] categoryArray = category.split(",");

        // 배열을 ECategory 타입의 리스트로 변환
        List<ECategory> categoryList = Arrays.stream(categoryArray)
                .map(String::trim) // 필요하면 앞뒤 공백 제거
                .map(String::toUpperCase)
                .map(this::convertToECategory) // ECategory로 변환
                .collect(Collectors.toList());

        return categoryList;
    }

    private ECategory convertToECategory(String category) {
        try {
            return ECategory.valueOf(category);
        } catch (IllegalArgumentException e) {
            throw new CommonException(ErrorCode.INVALID_CATEGORY_CONCAT);
        }
    }
}
