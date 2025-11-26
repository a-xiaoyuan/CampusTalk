package com.example.entity.vo.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class DetailsServiceVO {
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")  // 允许中文、英文、数字
    @Length(min = 1,max = 10)  // 长度1-10位
    String username;
    @Min(0)
    @Max(1)
    int gender;
    @Length(min = 11,max = 11)
    String phone;
    @Length(min = 8,max = 11)
    String qq;
    @Length(min = 5,max = 20)
    String wx;
    @Length(min = 10,max = 200)
    String desc;
}
