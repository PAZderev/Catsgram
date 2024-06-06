package ru.yandex.practicum.catsgram.model;

import lombok.*;

@Setter
@Getter
@EqualsAndHashCode(of = {"id"})
@ToString
public class Image {
    private Long id;
    private long postId;
    private String originalFileName;
    private String filePath;
}
