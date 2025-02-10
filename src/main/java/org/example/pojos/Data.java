package org.example.pojos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter 
@Setter 
@EqualsAndHashCode 
@ToString 
@Builder(toBuilder = true) 
@AllArgsConstructor
@NoArgsConstructor 
@Jacksonized
public class Data {
    private int year;
    private float price;
    private String color;
}
