package ru.checkdev.notification.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Calendar;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TopicDTO {
    @EqualsAndHashCode.Include
    private int id;
    private String name;
    private String text;
    private Calendar created;
    private Calendar updated;
    private int total;
    private int position;
    private CategoryDTO category;
}
